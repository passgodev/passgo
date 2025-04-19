package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Wallet;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
