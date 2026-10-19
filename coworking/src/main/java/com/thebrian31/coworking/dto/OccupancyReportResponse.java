package com.thebrian31.coworking.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record OccupancyReportResponse(
        OffsetDateTime from,
        OffsetDateTime to,
        List<SpaceOccupancyResponse> spaces
) {
}
