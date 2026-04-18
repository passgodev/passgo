package pl.uj.passgo.models.member;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.uj.passgo.models.Wallet;


@EqualsAndHashCode(callSuper=false)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "client")
public class Client extends Member {
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;
}
