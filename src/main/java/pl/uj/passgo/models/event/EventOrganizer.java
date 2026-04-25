package pl.uj.passgo.models.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.uj.passgo.models.member.MemberType;

import java.util.HashSet;
import java.util.Set;


@Getter
@Entity
@Table(name = "event_organizer", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organizer_id", "organizer_type"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventOrganizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "organizer_type", nullable = false)
    private MemberType organizerType;

    @OneToMany(mappedBy = "eventOrganizer")
    private Set<Event> events = new HashSet<>();

    public EventOrganizer(Long organizerId, MemberType organizerType) {
        this.organizerId = organizerId;
        this.organizerType = organizerType;
    }
}
