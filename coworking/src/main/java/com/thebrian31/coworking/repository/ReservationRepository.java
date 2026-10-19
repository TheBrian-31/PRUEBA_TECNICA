package com.thebrian31.coworking.repository;

import com.thebrian31.coworking.entity.Reservation;
import com.thebrian31.coworking.entity.ReservationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"account", "space"})
    List<Reservation> findByAccountIdOrderByStartAtDesc(Long accountId);

    @EntityGraph(attributePaths = {"account", "space"})
    List<Reservation> findByAccountIdAndStatusOrderByStartAtDesc(Long accountId, ReservationStatus status);

    @EntityGraph(attributePaths = {"account", "space"})
    List<Reservation> findAllByOrderByStartAtDesc();

    @EntityGraph(attributePaths = {"account", "space"})
    List<Reservation> findByStatusOrderByStartAtDesc(ReservationStatus status);

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
