package pl.uj.passgo.models.DTOs.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.uj.passgo.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleTransactionDto {
    private Long id;
    private BigDecimal totalPrice;
    private LocalDateTime completedAt;
    private TransactionType transactionType;
}
