package pl.uj.passgo.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketPurchaseRequest {
    private BigDecimal price;
    private Long eventId;
    private Boolean standingArea;
    private Long sectorId;
    private Long rowId;
    private Long seatId;
    private Long ownerId;
}
