package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.WalletHistory;

import java.util.List;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {
    List<WalletHistory> findByWalletId(Long walletId);
}
