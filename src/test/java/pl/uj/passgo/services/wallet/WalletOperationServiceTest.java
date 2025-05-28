package pl.uj.passgo.services.wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.DTOs.TopUpWalletRequest;
import pl.uj.passgo.models.DTOs.WalletDto;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.WalletHistory;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.repos.WalletHistoryRepository;
import pl.uj.passgo.repos.WalletRepository;
import pl.uj.passgo.services.WalletOperationService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension .class)
class WalletOperationServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletHistoryRepository walletHistoryRepository;

    @InjectMocks
    private WalletOperationService walletOperationService;

    @Test
    void shouldTopUpBalance_whenAmountIsPositive() {
        // Arrange
        Long clientId = 1L;
        Long walletId = 1L;
        Client client = new Client().builder()
                .id(clientId)
                .firstName("firstName")
                .lastName("lastName")
                .build();
        Wallet wallet = new Wallet(walletId, client, BigDecimal.valueOf(100));

        TopUpWalletRequest request = new TopUpWalletRequest(BigDecimal.valueOf(50), "Top up");

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        WalletDto result = walletOperationService.topUpBalance(walletId, request);

        // Assert
        assertEquals(walletId, result.id());
        assertEquals(BigDecimal.valueOf(150), result.money());
        verify(walletRepository).findById(walletId);
        verify(walletRepository).save(wallet);
        verify(walletHistoryRepository).save(any(WalletHistory.class));
    }

    @Test
    void shouldThrowException_whenTopUpAmountIsNegative() {
        // Arrange
        TopUpWalletRequest request = new TopUpWalletRequest(BigDecimal.valueOf(-10), "Invalid Top up");

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> walletOperationService.topUpBalance(1L, request));
        verify(walletRepository, never()).findById(any());
    }

    @Test
    void shouldChargeWalletAndSaveHistory_whenClientPurchasesTicket() {
        // Arrange
        BigDecimal price = BigDecimal.valueOf(20);
        Wallet wallet = new Wallet();
        wallet.setMoney(BigDecimal.valueOf(100));

        Client client = new Client();
        client.setWallet(wallet);

        // Act
        walletOperationService.chargeWalletForTicketPurchase(client, price);

        // Assert
        assertEquals(BigDecimal.valueOf(80), client.getWallet().getMoney());
        verify(walletHistoryRepository).save(argThat(history ->
                history.getAmount().equals(price.negate()) &&
                        history.getDescription().equals("Ticket Purchase")
        ));
    }

    @Test
    void shouldRechargeWalletAndSaveHistory_whenTicketIsReturned() {
        // Arrange
        BigDecimal returnPrice = BigDecimal.valueOf(30);
        Wallet wallet = new Wallet();
        wallet.setMoney(BigDecimal.valueOf(70));

        Client client = new Client();
        client.setWallet(wallet);

        // Act
        walletOperationService.rechargeWalletForTicketReturn(client, returnPrice);

        // Assert
        assertEquals(BigDecimal.valueOf(100), client.getWallet().getMoney());
        verify(walletHistoryRepository).save(argThat(history ->
                history.getAmount().equals(returnPrice) &&
                        history.getDescription().equals("Ticket Return")
        ));
    }
}