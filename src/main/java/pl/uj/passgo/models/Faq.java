package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "faq")
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faq_id_seq")
    @SequenceGenerator(name = "faq_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, length = 512)
    private String answer;

    @Column(nullable = false)
    @CurrentTimestamp
    private LocalDate addDate;

    @Column(nullable = false)
    @CurrentTimestamp
    private LocalDate updateDate;
}
