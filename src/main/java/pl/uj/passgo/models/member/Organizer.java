package pl.uj.passgo.models.member;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper=false)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "organizer")
public class Organizer extends Member {
     private String organization;
}
