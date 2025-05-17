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
public class TicketResponse {
    public Long id;
    public BigDecimal price;
    public Long sectorId;
    public String sectorName;
    public Long rowId;
    public Long seatId;
    public boolean standingArea;
}
