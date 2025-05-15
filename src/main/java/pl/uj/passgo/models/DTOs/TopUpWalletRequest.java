package pl.uj.passgo.models.DTOs;

import java.math.BigDecimal;

public record TopUpWalletRequest(BigDecimal amount, String description) {
}
