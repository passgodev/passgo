package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.WalletHistory;
import pl.uj.passgo.services.WalletService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<Wallet> getWalletByClientID(@PathVariable("clientId") Long clientId) {
        Wallet wallet = walletService.getWalletByClientID(clientId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/{walletId}/transaction")
    public ResponseEntity<Wallet> updateBalance(@PathVariable("walletId") Long walletId, @RequestParam BigDecimal amount, @RequestParam(required = false) String description) {
        Wallet updatedWallet = walletService.updateBalance(walletId, amount, description);
        return ResponseEntity.ok(updatedWallet);
    }

    @GetMapping("/{walletId}/history")
    public ResponseEntity<List<WalletHistory>> getWalletHistory(@PathVariable("walletId") Long walletId) {
        List<WalletHistory> walletHistoryList = walletService.getWalletHistory(walletId);
        return ResponseEntity.ok(walletHistoryList);
    }

}
