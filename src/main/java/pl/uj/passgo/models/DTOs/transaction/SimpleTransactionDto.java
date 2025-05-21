package pl.uj.passgo.models.DTOs.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.uj.passgo.models.DTOs.member.ClientDto;
import pl.uj.passgo.models.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
