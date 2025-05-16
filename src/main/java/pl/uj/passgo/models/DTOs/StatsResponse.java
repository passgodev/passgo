package pl.uj.passgo.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsResponse {
    private String eventName;
    private String category;
    private Long ticketsNumber;
    private Long availableTickets;
    private Double arenaOccupancy;
}
