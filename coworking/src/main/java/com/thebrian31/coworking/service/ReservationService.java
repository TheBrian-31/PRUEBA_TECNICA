package com.thebrian31.coworking.service;

import com.thebrian31.coworking.dto.ReservationRequest;
import com.thebrian31.coworking.dto.ReservationResponse;
import com.thebrian31.coworking.entity.Reservation;
import com.thebrian31.coworking.entity.ReservationStatus;
import com.thebrian31.coworking.entity.RoleCode;
import com.thebrian31.coworking.entity.state.ReservationState;
import com.thebrian31.coworking.entity.Space;
import com.thebrian31.coworking.entity.state.ReservationStates;
import com.thebrian31.coworking.entity.User;
import com.thebrian31.coworking.exception.InvalidReservationException;
import com.thebrian31.coworking.exception.OverlappingReservationException;
import com.thebrian31.coworking.exception.ReservationAccessDeniedException;
import com.thebrian31.coworking.exception.ReservationNotFoundException;
import com.thebrian31.coworking.exception.SpaceNotFoundException;
import com.thebrian31.coworking.mapper.ReservationMapper;
import com.thebrian31.coworking.repository.ReservationRepository;
import com.thebrian31.coworking.repository.SpaceRepository;
import com.thebrian31.coworking.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentValidationClient paymentValidationClient;

    public ReservationService(
            ReservationRepository reservationRepository,
            SpaceRepository spaceRepository,
            UserRepository userRepository,
            ReservationMapper reservationMapper,
            ApplicationEventPublisher eventPublisher,
            PaymentValidationClient paymentValidationClient
    ) {
        this.reservationRepository = reservationRepository;
        this.spaceRepository = spaceRepository;
        this.userRepository = userRepository;
        this.reservationMapper = reservationMapper;
        this.eventPublisher = eventPublisher;
        this.paymentValidationClient = paymentValidationClient;
    }

    @Transactional
    @CacheEvict(cacheNames = OccupancyService.CACHE, allEntries = true)
    public ReservationResponse create(ReservationRequest request) {
        chekTimes(request.startAt(), request.endAt());

        Space space = spaceRepository.findById(request.spaceId())
                .orElseThrow(() -> new SpaceNotFoundException(request.spaceId()));
        if (space.isOutOfService()) {
            throw new InvalidReservationException("El espacio está fuera de servicio");
        }
        if (reservationRepository.existsOverlap(
                space.getId(),
                request.startAt(),
                request.endAt(),
                ReservationStatus.CANCELLED
        )) {
            throw new OverlappingReservationException();
        }

        User account = userRepository.getReferenceById(currentUser().getId());
        Reservation reservation = new Reservation();
        reservation.setAccount(account);
        reservation.setSpace(space);
        reservation.setStartAt(request.startAt());
        reservation.setEndAt(request.endAt());
        reservation.setStatus(ReservationStatus.PENDING);

        try {
            return reservationMapper.toResponse(reservationRepository.saveAndFlush(reservation));
        } catch (DataIntegrityViolationException exception) {
            if (isOverlap(exception)) {
                throw new OverlappingReservationException();
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll(ReservationStatus status) {
        User currentUser = currentUser();
        List<Reservation> reservations;
        if (isAdmin(currentUser)) {
            reservations = status == null
                    ? reservationRepository.findAllByOrderByStartAtDesc()
                    : reservationRepository.findByStatusOrderByStartAtDesc(status);
        } else if (status == null) {
            reservations = reservationRepository.findByAccountIdOrderByStartAtDesc(currentUser.getId());
        } else {
            reservations = reservationRepository.findByAccountIdAndStatusOrderByStartAtDesc(currentUser.getId(), status);
        }
        return reservations.stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse findById(Long id) {
        Reservation reservation = getReservation(id);
        ensureCanManage(reservation);
        return reservationMapper.toResponse(reservation);
    }

    @Transactional
    @CacheEvict(cacheNames = OccupancyService.CACHE, allEntries = true)
    public ReservationResponse cancel(Long id) {
        return transition(id, ReservationStatus.CANCELLED);
    }

    @Transactional
    @CacheEvict(cacheNames = OccupancyService.CACHE, allEntries = true)
    public ReservationResponse confirm(Long id) {
        Reservation reservation = getReservation(id);
        ensureCanManage(reservation);
        if (!reservation.getStatus().canTransitionTo(ReservationStatus.CONFIRMED)) {
            throw new InvalidReservationException(
                    "No se puede pasar de " + reservation.getStatus() + " a " + ReservationStatus.CONFIRMED
            );
        }

        PaymentOutcome outcome = paymentValidationClient.validate(reservation.getId());
        if (outcome == PaymentOutcome.REJECTED) {
            throw new InvalidReservationException("El pago fue rechazado");
        }
        if (outcome == PaymentOutcome.UNAVAILABLE) {
            if (reservation.getStatus().canTransitionTo(ReservationStatus.PENDING_PAYMENT)) {
                changeStatus(reservation, ReservationStatus.PENDING_PAYMENT);
            }
            return reservationMapper.toResponse(reservation);
        }

        changeStatus(reservation, ReservationStatus.CONFIRMED);
        eventPublisher.publishEvent(new ReservationConfirmedEvent(
                reservation.getId(),
                reservation.getAccount().getEmail(),
                reservation.getSpace().getId(),
                reservation.getStartAt(),
                reservation.getEndAt()
        ));
        return reservationMapper.toResponse(reservation);
    }

    @Transactional
    @CacheEvict(cacheNames = OccupancyService.CACHE, allEntries = true)
    public ReservationResponse complete(Long id) {
        Reservation reservation = getReservation(id);
        ensureCanManage(reservation);
        if (reservation.getEndAt().isAfter(OffsetDateTime.now())) {
            throw new InvalidReservationException("La reserva solo se puede completar cuando ya terminó el horario");
        }
        changeStatus(reservation, ReservationStatus.COMPLETED);
        return reservationMapper.toResponse(reservation);
    }

    private ReservationResponse transition(Long id, ReservationStatus next) {
        Reservation reservation = getReservation(id);
        ensureCanManage(reservation);
        changeStatus(reservation, next);
        return reservationMapper.toResponse(reservation);
    }

    private void changeStatus(Reservation reservation, ReservationStatus next) {
        ReservationState state = ReservationStates.of(reservation.getStatus());
        switch (next) {
            case CONFIRMED -> state.confirm(reservation);
            case CANCELLED -> state.cancel(reservation);
            case COMPLETED -> state.complete(reservation);
            case PENDING_PAYMENT -> state.awaitPayment(reservation);
            case PENDING -> throw new InvalidReservationException(
                    "No se puede pasar de " + reservation.getStatus() + " a " + next
            );
        }
    }

    private Reservation getReservation(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException(id));
    }

    private void ensureCanManage(Reservation reservation) {
        User currentUser = currentUser();
        if (!isAdmin(currentUser) && !reservation.getAccount().getId().equals(currentUser.getId())) {
            throw new ReservationAccessDeniedException();
        }
    }

    private User currentUser() {
        AccountUserDetails details = (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return details.getUser();
    }

    private void chekTimes(OffsetDateTime startAt, OffsetDateTime endAt) {
        if (!endAt.isAfter(startAt)) {
            throw new InvalidReservationException("La hora de fin debe ser posterior a la de inicio");
        }
    }

    private boolean isAdmin(User user) {
        return user.getRole().getCode() == RoleCode.ADMIN;
    }

    private boolean isOverlap(DataIntegrityViolationException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof SQLException sqlException && "23P01".equals(sqlException.getSQLState())) {
                return true;
            }
            if (cause.getMessage() != null && cause.getMessage().contains("ex_reservation_no_overlap")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
