package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.uj.passgo.exception.MemberCredentialNotFoundException;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.MemberCredentialRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoggedInMemberContextService {
	private final ClientRepository clientRepository;
	private final OrganizerRepository organizerRepository;
	private final MemberCredentialRepository memberCredentialRepository;

	public Optional<Client> isClientLoggedIn() {
		var credentials = getMemberCredential();
		return clientRepository.findByMemberCredential((credentials));
	}

	private MemberCredential getMemberCredential() {
		log.debug("getMemberCredential - invoked");
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Authentication: {}", authentication);

		var userDetails = (UserDetails)authentication.getPrincipal();
		log.debug("UserDetails: {}", userDetails);

		var credentials = memberCredentialRepository.findByLogin(userDetails.getUsername())
													.orElseThrow(() -> {
														var msg = "MemberCredential not found for login: " + userDetails.getUsername();
														log.error(msg);
														return new MemberCredentialNotFoundException(msg);
													});
		log.debug("credentials: {}", credentials);
		log.debug("getMemberCredential - returning");

		return credentials;
	}

	public MemberCredential getLoggedInMemberCredential() {
		return getMemberCredential();
	}
}
