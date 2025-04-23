package pl.uj.passgo.models.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.uj.passgo.mappers.role.PrivilegeMapper;

import java.util.Collection;


@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MemberCredential implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_credential_id_seq")
	@SequenceGenerator(name = "member_credential_id_seq", allocationSize = 1)
	private Long id;
	@Column(name = "login", length = 32, nullable = false, unique = true)
	private String login;
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "member_type", nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private MemberType memberType;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return PrivilegeMapper.fromMemberType(memberType).getAuthorities();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return login;
	}
}
