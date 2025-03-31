package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper=false)
@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "client")
public class Client extends Member {
    @OneToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
