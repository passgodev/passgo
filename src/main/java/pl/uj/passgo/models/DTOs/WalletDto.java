package pl.uj.passgo.models.DTOs;

import java.math.BigDecimal;

public record WalletDto(Long id, BigDecimal money) {
}
