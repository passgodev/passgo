package pl.uj.passgo.configuration.security.role;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.uj.passgo.configuration.security.role.Permission.READ_HEALTH;

@RequiredArgsConstructor
public enum Privilege {
	CLIENT(
			Set.of(READ_HEALTH)
	),
	ORGANIZER(
			Set.of(READ_HEALTH)
	),
	ADMINISTRATOR(
		Stream.of(CLIENT.permissions,
				  ORGANIZER.permissions)
			  .flatMap(Set::stream)
			  .collect(Collectors.toUnmodifiableSet())
	)
	;

	private final Set<Permission> permissions;

	public List<SimpleGrantedAuthority> getAuthorities() {
		var authorities = permissions.stream()
						  .map(permission -> new SimpleGrantedAuthority(permission.name()))
						  .collect(Collectors.toList());

		authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

		return authorities;
	}

}
