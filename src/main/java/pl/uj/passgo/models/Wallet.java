package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_seq")
    @SequenceGenerator(name = "wallet_id_seq", allocationSize = 1)
    private Long id;

    // todo assign the User class when implemented
//    @OneToOne(optional = false)
//    @JoinColumn(name = "user_id", unique = true, nullable = false)
//    private User user;

    @Column
    private BigDecimal money;
}
