package com.thebrian31.coworking.service;

import com.thebrian31.coworking.dto.OccupancyReportResponse;
import com.thebrian31.coworking.dto.SpaceOccupancyResponse;
import com.thebrian31.coworking.entity.ReservationStatus;
import com.thebrian31.coworking.entity.Space;
import com.thebrian31.coworking.exception.InvalidReportRangeException;
import com.thebrian31.coworking.repository.ReservationIntervalView;
import com.thebrian31.coworking.repository.ReservationRepository;
import com.thebrian31.coworking.repository.SpaceRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OccupancyService {

    public static final String CACHE = "occupancy";

    private final SpaceRepository spaceRepository;
    private final ReservationRepository reservationRepository;

    public OccupancyService(SpaceRepository spaceRepository, ReservationRepository reservationRepository) {
        this.spaceRepository = spaceRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CACHE, key = "#from.toInstant().toEpochMilli() + '-' + #to.toInstant().toEpochMilli()")
    public OccupancyReportResponse report(OffsetDateTime from, OffsetDateTime to) {
        Instant rangeStart = from.toInstant();
        Instant rangeEnd = to.toInstant();
        long rangeMillis = Duration.between(rangeStart, rangeEnd).toMillis();
        if (rangeMillis  <= 0) {
            throw new InvalidReportRangeException("La fecha de fin debe ser posterior a la de inicio");
        }

        Map<Long, Long> occupiedMillis = new HashMap<>();
        for (ReservationIntervalView interval : reservationRepository.findOverlappingIntervals(from, to, ReservationStatus.CANCELLED)) {
            Instant start = later(interval.getStartAt().toInstant(), rangeStart);
            Instant end = earlier(interval.getEndAt().toInstant(), rangeEnd);
            long millis = Duration.between(start, end).toMillis();
            if (millis > 0) {
                occupiedMillis.merge(interval.getSpaceId(), millis, Long::sum);
            }
        }

        List<SpaceOccupancyResponse> spaces = spaceRepository.findAll().stream()
                .sorted(Comparator.comparing(Space::getId))
                .map(space -> new SpaceOccupancyResponse(
                        space.getId(),
                        space.getName(),
                        percent(occupiedMillis.getOrDefault(space.getId(), 0L), rangeMillis)
                ))
                .toList();

        return new OccupancyReportResponse(
                rangeStart.atOffset(ZoneOffset.UTC),
                rangeEnd.atOffset(ZoneOffset.UTC),
                spaces
        );
    }

    private BigDecimal percent(long occupiedMillis, long rangeMillis) {
        BigDecimal value = BigDecimal.valueOf(occupiedMillis)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(rangeMillis), 2, RoundingMode.HALF_UP);
        BigDecimal full = new BigDecimal("100.00");
        return capAtFul(value, full);
    }

    private BigDecimal capAtFul(BigDecimal value, BigDecimal full) {
        return value.compareTo(full) > 0 ? full : value;
    }

    private Instant later(Instant left, Instant right) {
        return left.isAfter(right) ? left : right;
    }

    private Instant earlier(Instant left, Instant right) {
        return left.isBefore(right) ? left : right;
    }
}
