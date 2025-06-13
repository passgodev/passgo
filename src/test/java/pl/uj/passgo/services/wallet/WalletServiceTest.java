package pl.uj.passgo.services.wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.DTOs.WalletDto;
import pl.uj.passgo.models.DTOs.WalletHistoryDto;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.WalletHistory;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.repos.WalletHistoryRepository;
import pl.uj.passgo.repos.WalletRepository;
import pl.uj.passgo.services.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletHistoryRepository walletHistoryRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldReturnWalletDto_whenWalletExistsForClientId() {
        // Arrange
        Long clientId = 1L;
        Long walletId = 1L;
        Client client = Client.builder()
                .id(clientId)
                .firstName("firstName")
                .lastName("lastName")
                .build();
        Wallet wallet = new Wallet(walletId, client, BigDecimal.valueOf(100));
        when(walletRepository.findWalletByClientId(clientId)).thenReturn(Optional.of(wallet));

        // Act
        WalletDto result = walletService.getWalletByClientID(clientId);

        // Assert
        assertEquals(1L, result.id());
        assertEquals(BigDecimal.valueOf(100), result.money());
        verify(walletRepository).findWalletByClientId(clientId);
    }

    @Test
    void shouldThrowException_whenWalletNotFoundForClientId() {
        // Arrange
        Long clientId = 1L;
        when(walletRepository.findWalletByClientId(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> walletService.getWalletByClientID(clientId));
        verify(walletRepository).findWalletByClientId(clientId);
    }

    @Test
    void shouldReturnWalletHistory_whenHistoryExistsForWallet() {
        // Arrange
        Long walletId = 1L;
        WalletHistory history = new WalletHistory();
        history.setAmount(BigDecimal.valueOf(50));
        history.setDescription("Top-up");
        history.setOperationDate(LocalDateTime.now());

        when(walletHistoryRepository.findByWalletId(walletId)).thenReturn(List.of(history));

        // Act
        List<WalletHistoryDto> result = walletService.getWalletHistory(walletId);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Top-up", result.getFirst().description());
        assertEquals(BigDecimal.valueOf(50), result.getFirst().amount());
        verify(walletHistoryRepository).findByWalletId(walletId);
    }

    @Test
    void shouldReturnEmptyWalletHistory_whenHistoryNotFoundForWalletId() {
        // Arrange
        Long walletId = 1L;
        when(walletHistoryRepository.findByWalletId(walletId)).thenReturn(List.of());

        // Act
        List<WalletHistoryDto> result = walletService.getWalletHistory(walletId);

        // Assert
        assertEquals(0, result.size());
        verify(walletHistoryRepository).findByWalletId(walletId);
    }

}