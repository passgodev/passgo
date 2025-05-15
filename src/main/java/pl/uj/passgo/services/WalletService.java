package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.DTOs.TopUpWalletRequest;
import pl.uj.passgo.models.DTOs.WalletDto;
import pl.uj.passgo.models.DTOs.WalletHistoryDto;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.WalletHistory;
import pl.uj.passgo.repos.WalletHistoryRepository;
import pl.uj.passgo.repos.WalletRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;

    public WalletDto getWalletByClientID(Long clientId) {
        return walletRepository.findWalletByClientId(clientId)
                .map(wallet -> new WalletDto(wallet.getId(), wallet.getMoney()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client ID: " + clientId + " has no wallet."));
    }

    public List<WalletHistoryDto> getWalletHistory(Long walletId) {
        return walletHistoryRepository.findByWalletId(walletId)
                .stream()
                .map(walletHistory -> new WalletHistoryDto(walletHistory.getOperationDate(), walletHistory.getAmount(), walletHistory.getDescription()))
                .toList();
    }
}
