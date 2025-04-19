package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.uj.passgo.models.member.Client;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = true)
    private Sector sector;

    @ManyToOne
    @JoinColumn(name = "row_id", nullable = true)
    private Row row;

    @OneToOne
    @JoinColumn(name = "seat_id", nullable = true)
    private Seat seat;

    @Column(nullable = false)
    private Boolean standingArea;

    @OneToOne
    @JoinColumn(name = "client_id")
    private Client owner;
}
