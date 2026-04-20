package pl.uj.passgo.models.DTOs.statistics;

import org.springframework.data.domain.Page;


public record FullStatsResponse(
    Page<StatsResponse> eventsStats,
    Long totalTickets,
    Long totalBoughtTickets,
    Double averageAreaOccupy
) {
    public static FullStatsResponse empty() {
        return new FullStatsResponse(null, 0L, 0L, 0.0);
    }
}
