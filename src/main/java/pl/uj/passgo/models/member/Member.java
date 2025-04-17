package pl.uj.passgo.models.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "first_name")
    private String firstName;
    @Column(nullable = false, name = "last_name")
    private String lastName;
    private String email;
    @CreationTimestamp
    @Column(nullable = false, name = "registration_date")
    private LocalDateTime registrationDate;
    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "is_active")
    private boolean isActive;

    @OneToOne
    @JoinColumn(name = "member_credential_id", nullable = false, unique = true)
    private MemberCredential memberCredential;
}
