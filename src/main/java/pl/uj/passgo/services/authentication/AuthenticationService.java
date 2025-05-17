package pl.uj.passgo.services.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.configuration.security.jwt.JwtService;
import pl.uj.passgo.mappers.member.MemberMapper;
import pl.uj.passgo.models.DTOs.authentication.login.LoginRequest;
import pl.uj.passgo.models.DTOs.authentication.login.LoginResponse;
import pl.uj.passgo.models.DTOs.authentication.logout.LogoutRequest;
import pl.uj.passgo.models.DTOs.authentication.refresh.RefreshTokenRequest;
import pl.uj.passgo.models.DTOs.authentication.refresh.RefreshTokenResponse;
import pl.uj.passgo.models.DTOs.authentication.registration.ClientRegistrationRequest;
import pl.uj.passgo.models.DTOs.authentication.registration.MemberRegistrationRequest;
import pl.uj.passgo.models.DTOs.authentication.registration.OrganizerRegistrationRequest;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.member.Member;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.models.member.MemberType;
import pl.uj.passgo.repos.WalletRepository;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.MemberCredentialRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationService {
	private final MemberCredentialRepository memberCredentialRepository;

	private final MemberMapper memberMapper;
	private final ClientRepository clientRepository;
	private final WalletRepository walletRepository;
	private final OrganizerRepository organizerRepository;

	private final AuthenticationManager authenticationManager;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtService jwtService;

	private final RefreshTokenService refreshTokenService;

	@Transactional
	public void registerNewMember(MemberRegistrationRequest request) {
		if (memberCredentialRepository.findByLogin(request.getCredentials().login()).isPresent()) {
			var message = "User with provided login already exists, login: " + request.getCredentials().login();
			log.warn(message);
			throw new ResponseStatusException(HttpStatus.CONFLICT, message);
		}

		switch ( request ) {
			case ClientRegistrationRequest c -> handleClientRegistration(c);
			case OrganizerRegistrationRequest o -> handleOrganizerRegistration(o);
		}
	}

	private static boolean memberIsActive(MemberType memberType) {
		return switch (memberType) {
			case CLIENT, ADMINISTRATOR -> true;
			case ORGANIZER -> false;
		};
	}

	private <T extends Member, U extends MemberRegistrationRequest> void setCredentials(T member, U request) {
		var memberCredential = new MemberCredential();

		memberCredential.setMemberType(request.getMemberType());
		memberCredential.setLogin(request.getCredentials().login());
		memberCredential.setPassword(bCryptPasswordEncoder.encode(request.getCredentials().password()));
		memberCredential.setActive(memberIsActive(request.getMemberType()));

		var saved = memberCredentialRepository.save(memberCredential);

		member.setMemberCredential(saved);
	}

	private void handleClientRegistration(ClientRegistrationRequest request) {
		var client = memberMapper.toClient(request);

		var wallet = new Wallet();
		wallet.setClient(client);
		var saved = walletRepository.save(wallet);
		client.setWallet(saved);

		setCredentials(client, request);

		var savedClient = clientRepository.save(client);
		log.info("Saved Client: {}", savedClient);
	}

	private void handleOrganizerRegistration(OrganizerRegistrationRequest request) {
		var organizer = memberMapper.toOrganizer(request);

		setCredentials(organizer, request);

		var savedOrganizer = organizerRepository.save(organizer);
		log.info("Saved Organizer: {}", savedOrganizer);
	}

	public LoginResponse loginMember(LoginRequest request) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.login(), request.password()));

		var memberCredential = memberCredentialRepository.findByLogin(request.login())
			.orElseThrow(() -> {
				var message = "Member with provided login does not exist, login: " + request.login();
				log.warn(message);
				return new ResponseStatusException(HttpStatus.CONFLICT, message);
			});

		if (!memberCredential.isActive()) {
			var message = "Member is not active, login: " + request.login();
			log.warn(message);
			throw new ResponseStatusException(HttpStatus.CONFLICT, message);
		}

		var jwtToken = jwtService.generateToken(memberCredential);
		var refreshToken = refreshTokenService.createRefreshToken(memberCredential);

		return new LoginResponse(refreshToken.getToken(), jwtToken);
	}

	public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
		var refreshToken = refreshTokenService.getByToken(request.refreshToken());
		refreshTokenService.isAfterExpirationDate(refreshToken);

		var memberCredential = refreshToken.getMemberCredential();
		var jwtToken = jwtService.generateToken(memberCredential);

		return new RefreshTokenResponse(refreshToken.getToken(), jwtToken);
	}

	public void logoutMember(LogoutRequest request) {
		var refreshToken = request.refreshToken();
		if ( refreshToken == null ) {
			log.info("logout refresh token is null, returning...");
			return;
		}

		refreshTokenService.deleteRefreshToken(refreshToken);
	}
}
