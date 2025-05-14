package pl.uj.passgo.models.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletHistoryDto(LocalDateTime operationDate, BigDecimal amount, String description) {
}
