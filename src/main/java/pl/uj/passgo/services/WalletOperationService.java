package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import pl.uj.passgo.models.transaction.TransactionType;
import pl.uj.passgo.repos.WalletHistoryRepository;
import pl.uj.passgo.repos.WalletRepository;
import pl.uj.passgo.repos.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WalletOperationService {

    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;
    private final TransactionRepository transactionRepository;

    private final LoggedInMemberContextService loggedInMemberContextService;
    private final Clock clock = Clock.systemDefaultZone();

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

        Client client = loggedInMemberContextService.isClientLoggedIn().orElseThrow();
        var transaction = Transaction.builder()
                .client(client)
                .totalPrice(topUpWalletRequest.amount())
                .completedAt(LocalDateTime.now(clock))
                .transactionType(TransactionType.TOP_UP)
                .build();

        transactionRepository.save(transaction);

        return new WalletDto(wallet.getId(), wallet.getMoney());
    }

    public void chargeWalletForTicketPurchase(Client client, BigDecimal ticketsTotalPrice) {
        client.getWallet().setMoney(client.getWallet().getMoney().subtract(ticketsTotalPrice));

        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWallet(client.getWallet());
        walletHistory.setAmount(ticketsTotalPrice.negate());
        walletHistory.setDescription("Ticket Purchase");
        walletHistoryRepository.save(walletHistory);
    }

    public void rechargeWalletForTicketReturn(Client client, BigDecimal returnPrice) {
        client.getWallet().setMoney(client.getWallet().getMoney().add(returnPrice));

        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWallet(client.getWallet());
        walletHistory.setAmount(returnPrice);
        walletHistory.setDescription("Ticket Return");
        walletHistoryRepository.save(walletHistory);
    }
}
