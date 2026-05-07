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
@Table(name = "sector")
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Row> rows;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    @JsonBackReference
    private Building building;

    @Column(nullable = false)
    private Boolean standingArea;
}
