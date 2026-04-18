package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.*;
import pl.uj.passgo.models.enums.TicketSaleStatus;
import pl.uj.passgo.models.member.Client;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "ticket_sale")
public class TicketSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Client seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Client buyer;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketSaleStatus status;

}
