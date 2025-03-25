package pl.uj.passgo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
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
    private LocalDate add_date;
}
