package pl.uj.passgo.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateRequest {
    private String name;
    private Long buildingId;
    private LocalDateTime date;
    private String description;
    private String category;
    private Map<Long, BigDecimal> rowPrices;
}
