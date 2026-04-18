package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.*;
import pl.uj.passgo.models.member.Client;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_seq")
    @SequenceGenerator(name = "wallet_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne(optional = false, mappedBy = "wallet")
    @ToString.Exclude
    private Client client;

    @Column
    private BigDecimal money = BigDecimal.ZERO;
}
