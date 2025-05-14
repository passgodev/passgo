package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.DTOs.WalletDto;
import pl.uj.passgo.models.DTOs.WalletHistoryDto;
import pl.uj.passgo.services.WalletService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<WalletDto> getWalletByClientID(@PathVariable("clientId") Long clientId) {
        WalletDto wallet = walletService.getWalletByClientID(clientId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/{walletId}/transaction")
    public ResponseEntity<WalletDto> updateBalance(@PathVariable("walletId") Long walletId, @RequestParam BigDecimal amount, @RequestParam(required = false) String description) {
        WalletDto updatedWallet = walletService.updateBalance(walletId, amount, description);
        return ResponseEntity.ok(updatedWallet);
    }

    @GetMapping("/{walletId}/history")
    public ResponseEntity<List<WalletHistoryDto>> getWalletHistory(@PathVariable("walletId") Long walletId) {
        List<WalletHistoryDto> walletHistoryList = walletService.getWalletHistory(walletId);
        return ResponseEntity.ok(walletHistoryList);
    }

}
