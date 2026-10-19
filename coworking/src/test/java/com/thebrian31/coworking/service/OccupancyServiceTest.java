package com.thebrian31.coworking.service;

import com.thebrian31.coworking.dto.OccupancyReportResponse;
import com.thebrian31.coworking.entity.ReservationStatus;
import com.thebrian31.coworking.entity.Space;
import com.thebrian31.coworking.exception.InvalidReportRangeException;
import com.thebrian31.coworking.repository.ReservationIntervalView;
import com.thebrian31.coworking.repository.ReservationRepository;
import com.thebrian31.coworking.repository.SpaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OccupancyServiceTest {

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private OccupancyService occupancyService;

    @Test
    void reportClipsReservationsAndReturnsPercentPerSpace() {
        OffsetDateTime from = OffsetDateTime.of(2026, 10, 17, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime to = from.plusHours(2);
        Space room = space(1L, "Sala A");
        Space desk = space(2L, "Puesto B");
        when(spaceRepository.findAll()).thenReturn(List.of(desk, room));
        when(reservationRepository.findOverlappingIntervals(from, to, ReservationStatus.CANCELLED))
                .thenReturn(List.of(
                        interval(1L, from.minusMinutes(30), from.plusMinutes(30)),
                        interval(1L, from.plusHours(1), to.plusHours(1))
                ));

        OccupancyReportResponse report = occupancyService.report(from, to);

        assertThat(report.from()).isEqualTo(from);
        assertThat(report.to()).isEqualTo(to);
        assertThat(report.spaces()).hasSize(2);
        assertThat(report.spaces().get(0).spaceId()).isEqualTo(1L);
        assertThat(report.spaces().get(0).occupancyPercent()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(report.spaces().get(1).spaceId()).isEqualTo(2L);
        assertThat(report.spaces().get(1).occupancyPercent()).isEqualByComparingTo(new BigDecimal("0.00"));
        verify(reservationRepository).findOverlappingIntervals(eq(from), eq(to), eq(ReservationStatus.CANCELLED));
    }

    @Test
    void reportRejectsARangeThatDoesNotMoveForward() {
        OffsetDateTime from = OffsetDateTime.parse("2026-10-17T10:00:00Z");

        assertThatThrownBy(() -> occupancyService.report(from, from))
                .isInstanceOf(InvalidReportRangeException.class);
    }

    private Space space(Long id, String name) {
        Space space = new Space();
        space.setId(id);
        space.setName(name);
        return space;
    }

    private ReservationIntervalView interval(Long spaceId, OffsetDateTime startAt, OffsetDateTime endAt) {
        return new ReservationIntervalView() {
            @Override
            public Long getSpaceId() {
                return spaceId;
            }

            @Override
            public OffsetDateTime getStartAt() {
                return startAt;
            }

            @Override
            public OffsetDateTime getEndAt() {
                return endAt;
            }
        };
    }
}
