package pl.uj.passgo.controllers.wallet;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.uj.passgo.models.DTOs.TopUpWalletRequest;
import pl.uj.passgo.models.DTOs.WalletDto;
import pl.uj.passgo.models.DTOs.WalletHistoryDto;
import pl.uj.passgo.services.WalletOperationService;
import pl.uj.passgo.services.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @MockitoBean
    private WalletOperationService walletOperationService;

    @Test
    void shouldReturnWallet_whenGetWalletByClientId() throws Exception {
        // Arrange
        WalletDto walletDto = new WalletDto(1L, BigDecimal.valueOf(100));
        when(walletService.getWalletByClientID(1L)).thenReturn(walletDto);

        // Act & Assert
        mockMvc.perform(get("/wallets/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.money").value(100));
    }

    @Test
    void shouldReturnUpdatedWallet_whenTopUpWallet() throws Exception {
        // Arrange
        TopUpWalletRequest request = new TopUpWalletRequest(BigDecimal.valueOf(50), "Top up");
        WalletDto updatedWallet = new WalletDto(1L, BigDecimal.valueOf(150));
        when(walletOperationService.topUpBalance(eq(1L), any(TopUpWalletRequest.class)))
                .thenReturn(updatedWallet);

        // Act & Assert
        mockMvc.perform(post("/wallets/1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "amount": 50,
                                "description": "Top up"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.money").value(150));
    }

    @Test
    void shouldReturnWalletHistory_whenGetWalletHistory() throws Exception {
        // Arrange
        List<WalletHistoryDto> historyList = List.of(
                new WalletHistoryDto(LocalDateTime.now(), BigDecimal.valueOf(20), "Ticket Purchase"),
                new WalletHistoryDto(LocalDateTime.now(), BigDecimal.valueOf(50), "Top up")
        );
        when(walletService.getWalletHistory(1L)).thenReturn(historyList);

        // Act & Assert
        mockMvc.perform(get("/wallets/1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].description").value("Ticket Purchase"))
                .andExpect(jsonPath("$[1].description").value("Top up"))
                .andExpect(jsonPath("$[0].amount").value(20))
                .andExpect(jsonPath("$[1].amount").value(50));
    }
}