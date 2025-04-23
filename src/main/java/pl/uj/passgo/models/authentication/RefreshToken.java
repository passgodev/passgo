package pl.uj.passgo.models.authentication;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.uj.passgo.models.member.MemberCredential;

import java.time.LocalDateTime;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_id_seq")
	@SequenceGenerator(name = "refresh_token_id_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	private UUID token;

	@ManyToOne
	@JoinColumn(name = "member_credential_id", nullable = false)
	private MemberCredential memberCredential;

	@Column(nullable = false)
	private LocalDateTime expiresAt;
}
