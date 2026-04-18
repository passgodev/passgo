package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.DTOs.TopUpWalletRequest;
import pl.uj.passgo.models.DTOs.WalletDto;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.WalletHistory;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.transaction.Transaction;
import pl.uj.passgo.models.enums.TransactionType;
import pl.uj.passgo.repos.WalletHistoryRepository;
import pl.uj.passgo.repos.WalletRepository;
import pl.uj.passgo.repos.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletOperationService {

    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;
    private final TransactionRepository transactionRepository;

    private final LoggedInMemberContextService loggedInMemberContextService;
    private final Clock clock;

    @Transactional
    public WalletDto topUpBalance(Long walletId, TopUpWalletRequest topUpWalletRequest) {
        if(topUpWalletRequest.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero.");
        }

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet id: " + walletId + " does not exists."));

        BigDecimal newBalance = wallet.getMoney().add(topUpWalletRequest.amount());
        wallet.setMoney(newBalance);
        walletRepository.save(wallet);

        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setDescription(topUpWalletRequest.description());
        walletHistory.setAmount(topUpWalletRequest.amount());
        walletHistoryRepository.save(walletHistory);

        Client client = loggedInMemberContextService.isClientLoggedIn().orElseThrow(
                () -> new ResponseStatusException(HttpStatus.CONFLICT, "User is not logged in.")
        );
        var transaction = Transaction.builder()
                .client(client)
                .totalPrice(topUpWalletRequest.amount())
                .completedAt(LocalDateTime.now(clock))
                .transactionType(TransactionType.TOP_UP)
                .build();

        transactionRepository.save(transaction);

        return new WalletDto(wallet.getId(), wallet.getMoney());
    }

    public void createWalletHistoryEntry(Client client, BigDecimal operationPrice, String operationDescription) {
        Wallet wallet = client.getWallet();
        BigDecimal clientMoney = wallet.getMoney();
        BigDecimal clientMoneyAfterOperation = clientMoney.add(operationPrice);

        if (clientMoneyAfterOperation.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Operation is not possible.");
        }

        wallet.setMoney(clientMoneyAfterOperation);
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setAmount(operationPrice);
        walletHistory.setDescription(operationDescription);
        walletHistoryRepository.save(walletHistory);
    }
}
