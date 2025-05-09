package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne
    @JoinColumn(name = "building_id", referencedColumnName = "id")
    private Building building;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String category;

    @Column(nullable = false)
    private Boolean approved = false;

    @Column(nullable = true)
    private String imagePath = null;
}
