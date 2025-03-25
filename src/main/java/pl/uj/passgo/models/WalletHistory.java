package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "wallet_history")
public class WalletHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_history_id_seq")
    @SequenceGenerator(name = "wallet_history_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    private Wallet wallet;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime operationDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 128)
    private String description;
}
