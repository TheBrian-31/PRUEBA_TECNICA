package com.thebrian31.coworking.repository;

import java.time.OffsetDateTime;

public interface ReservationIntervalView {

    Long getSpaceId();

    OffsetDateTime getStartAt();

    OffsetDateTime getEndAt();
}
