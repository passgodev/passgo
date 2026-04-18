package pl.uj.passgo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "row_table")
public class Row {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "row", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Seat> seats;

    @Column(nullable = false)
    private Long rowNumber;

    @Column(nullable = false)
    private Long seatsCount;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    @JsonBackReference
    private Sector sector;

}
