package pl.uj.passgo.models.DTOs.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketFullResponse {
    public Long id;
    public String eventName;
    public BigDecimal price;
    public String sectorName;
    public Long rowNumber;
    public Long seatNumber;
    public boolean standingArea;
}
