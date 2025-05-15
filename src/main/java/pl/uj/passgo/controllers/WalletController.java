package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.DTOs.TopUpWalletRequest;
import pl.uj.passgo.models.DTOs.WalletDto;
import pl.uj.passgo.models.DTOs.WalletHistoryDto;
import pl.uj.passgo.services.WalletOperationService;
import pl.uj.passgo.services.WalletService;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WalletController {

    private final WalletService walletService;
    private final WalletOperationService walletOperationService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<WalletDto> getWalletByClientID(@PathVariable("clientId") Long clientId) {
        var wallet = walletService.getWalletByClientID(clientId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/{walletId}/transaction")
    public ResponseEntity<WalletDto> topUpBalance(@PathVariable("walletId") Long walletId, @RequestBody TopUpWalletRequest topUpWalletRequest) {
        var updatedWallet = walletOperationService.topUpBalance(walletId, topUpWalletRequest);
        return ResponseEntity.ok(updatedWallet);
    }

    @GetMapping("/{walletId}/history")
    public ResponseEntity<List<WalletHistoryDto>> getWalletHistory(@PathVariable("walletId") Long walletId) {
        var walletHistoryList = walletService.getWalletHistory(walletId);
        return ResponseEntity.ok(walletHistoryList);
    }

}
