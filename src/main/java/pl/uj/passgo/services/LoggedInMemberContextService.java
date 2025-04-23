package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.MemberCredentialRepository;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoggedInMemberContextService {
	private final ClientRepository clientRepository;
	private final MemberCredentialRepository memberCredentialRepository;

	public Optional<Client> isClientLoggedIn() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Authentication: {}", authentication);

		var userDetails = (UserDetails)authentication.getPrincipal();
		log.debug("UserDetails: {}", userDetails);

		var credentials = memberCredentialRepository.findByLogin(userDetails.getUsername()).orElseThrow();
		log.debug("credentials: {}", credentials);

		var client = clientRepository.findByMemberCredential((credentials));
		log.debug("client: {}", client);

		return client;
	}

}
