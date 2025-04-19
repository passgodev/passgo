package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public Wallet getWalletByClientID(Long clientId) {
        return walletRepository.findWalletByClientId(clientId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client ID: " + clientId + " has no wallet"));
    }

    public List<WalletHistory> getWalletHistory(Long walletId) {
        return walletHistoryRepository.findByWalletId(walletId);
    }

    public Wallet updateBalance(Long walletId, BigDecimal amount, String description) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "wallet id: " + walletId + "does not exists."));

        BigDecimal newBalance = wallet.getMoney().add(amount);
        wallet.setMoney(newBalance);
        walletRepository.save(wallet);

        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setDescription(description);
        walletHistory.setAmount(amount);
        walletHistoryRepository.save(walletHistory);

        return wallet;
    }
}
