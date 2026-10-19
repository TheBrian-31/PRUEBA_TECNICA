package com.thebrian31.coworking.repository;

import com.thebrian31.coworking.entity.Reservation;
import com.thebrian31.coworking.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"account", "space"})
    Page<Reservation> findByAccountId(Long accountId, Pageable pageable);

    @EntityGraph(attributePaths = {"account", "space"})
    Page<Reservation> findByAccountIdAndStatus(Long accountId, ReservationStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"account", "space"})
    Page<Reservation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"account", "space"})
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    @Query("""
            SELECT COUNT(reservation) > 0
            FROM Reservation reservation
            WHERE reservation.space.id = :spaceId
              AND reservation.status <> :cancelled
              AND reservation.startAt < :endAt
              AND reservation.endAt > :startAt
            """)
    boolean existsOverlap(Long spaceId, OffsetDateTime startAt, OffsetDateTime endAt, ReservationStatus cancelled);

    @Query("""
            SELECT reservation.space.id AS spaceId,
                   reservation.startAt AS startAt,
                   reservation.endAt AS endAt
            FROM Reservation reservation
            WHERE reservation.status <> :cancelled
              AND reservation.startAt < :to
              AND reservation.endAt > :from
            """)
    List<ReservationIntervalView> findOverlappingIntervals(OffsetDateTime from, OffsetDateTime to, ReservationStatus cancelled);
}
