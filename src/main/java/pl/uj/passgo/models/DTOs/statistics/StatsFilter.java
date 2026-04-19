package pl.uj.passgo.models.DTOs.statistics;

import lombok.Data;
import pl.uj.passgo.models.enums.Status;

import java.time.Instant;
import java.util.List;

@Data
public class StatsFilter {
    private Instant from;
    private Instant to;
    private List<Long> eventIds;
    private Status status;
}
