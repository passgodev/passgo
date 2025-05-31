package pl.uj.passgo.services.authentication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.configuration.security.jwt.JwtService;
import pl.uj.passgo.mappers.member.MemberMapper;
import pl.uj.passgo.models.DTOs.authentication.login.LoginRequest;
import pl.uj.passgo.models.DTOs.authentication.logout.LogoutRequest;
import pl.uj.passgo.models.DTOs.authentication.refresh.RefreshTokenRequest;
import pl.uj.passgo.models.DTOs.authentication.registration.ClientRegistrationRequest;
import pl.uj.passgo.models.DTOs.authentication.registration.MemberRegistrationCredentials;
import pl.uj.passgo.models.DTOs.authentication.registration.MemberRegistrationRequest;
import pl.uj.passgo.models.DTOs.authentication.registration.OrganizerRegistrationRequest;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.authentication.RefreshToken;
import pl.uj.passgo.models.member.*;
import pl.uj.passgo.repos.WalletRepository;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.MemberCredentialRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
	private static final Clock CLOCK = Clock.systemDefaultZone();

	@Mock
	private MemberCredentialRepository memberCredentialRepository;
	@Mock
	private ClientRepository clientRepository;
	@Mock
	private WalletRepository walletRepository;
	@Mock
	private OrganizerRepository organizerRepository;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtService jwtService;
	@Mock
	private RefreshTokenService refreshTokenService;
	@Spy
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private MemberMapper memberMapper = new MemberMapper();

	private AuthenticationService authenticationService;

	@BeforeEach
	public void setup() {
		this.authenticationService = new AuthenticationService(
			memberCredentialRepository,
			memberMapper,
			walletRepository,
			clientRepository,
			organizerRepository,
			authenticationManager,
			bCryptPasswordEncoder,
			jwtService,
			refreshTokenService
		);
	}

	@ParameterizedTest
	@MethodSource("getMemberRegistartionRequest")
	public void testRegisterNewMember_userLoginAlreadyExist(MemberRegistrationRequest memberRegistrationRequest) {
		// arrange
		var login = memberRegistrationRequest.getCredentials().login();
		when(memberCredentialRepository.findByLogin(login))
			.thenReturn(Optional.of(new MemberCredential()));
		// act
		Executable shouldFail = () -> authenticationService.registerNewMember(memberRegistrationRequest);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	private static Stream<Arguments> getMemberRegistartionRequest() {
		return Stream.of(
			Arguments.of(getClientRegistrationRequest(CLOCK)),
			Arguments.of(getOrganizerRegistrationRequest(CLOCK))
		);
	}

	private static OrganizerRegistrationRequest getOrganizerRegistrationRequest(Clock clock) {
		return new OrganizerRegistrationRequest(
			memberRegistrationCredentials(),
			"firstName",
			"lastName",
			LocalDate.now(clock),
			"Organization"
		);
	}

	private static ClientRegistrationRequest getClientRegistrationRequest(Clock clock) {
		return new ClientRegistrationRequest(
			memberRegistrationCredentials(),
			"firstName",
			"lastName",
			LocalDate.now(clock)
		);
	}

	private static MemberRegistrationCredentials memberRegistrationCredentials() {
		return new MemberRegistrationCredentials("login", "password", "email");
	}

	@Test
	public void testRegisterNewMember_registerClient() {
		// arrange
		var clientRegisterRequest = getClientRegistrationRequest(CLOCK);
		var login = clientRegisterRequest.getCredentials().login();
		when(memberCredentialRepository.findByLogin(login))
			.thenReturn(Optional.empty());
		when(walletRepository.save(any(Wallet.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		when(bCryptPasswordEncoder.encode(anyString()))
			.thenReturn("abcdefghijklmn");
		when(memberCredentialRepository.save(any(MemberCredential.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		when(clientRepository.save(any(Client.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// act
		authenticationService.registerNewMember(clientRegisterRequest);

		// assert
		verify(walletRepository).save(any());
		verify(clientRepository).save(any());
		verify(memberCredentialRepository).save(any());
	}

	@Test
	public void testRegisterNewMember_registerOrganizer() {
		// arrange
		var organizerRegisterRequest = getOrganizerRegistrationRequest(CLOCK);
		var login = organizerRegisterRequest.getCredentials().login();
		when(memberCredentialRepository.findByLogin(login))
			.thenReturn(Optional.empty());
		when(bCryptPasswordEncoder.encode(anyString()))
			.thenReturn("abcdefghijklmn");
		when(memberCredentialRepository.save(any(MemberCredential.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		when(organizerRepository.save(any(Organizer.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// act
		authenticationService.registerNewMember(organizerRegisterRequest);

		// assert
		verify(memberCredentialRepository).save(any());
		verify(organizerRepository).save(any());
	}

	@Test
	public void testLoginMember_memberWithLoginNotFound() {
		// arrange
		var loginRequest = new LoginRequest("login", "password");
//		doNothing().when(authenticationManager).authenticate(any());
		when(memberCredentialRepository.findByLogin(anyString()))
			.thenReturn(Optional.empty());

		// act
		Executable shouldFail = () -> authenticationService.loginMember(loginRequest);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	@Test
	public void testLoginMember_memberInactive() {
		// arrange
		var loginRequest = new LoginRequest("login", "password");
		var inactiveMemberCredential = new MemberCredential();
		inactiveMemberCredential.setActive(false);

//		doNothing().when(authenticationManager).authenticate(any());
		when(memberCredentialRepository.findByLogin(anyString()))
			.thenReturn(Optional.of(inactiveMemberCredential));

		// act
		Executable shouldFail = () -> authenticationService.loginMember(loginRequest);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	@Test
	public void testLoginMember_loginSuccessfully() {
		// arrange
		var loginRequest = new LoginRequest("login", "password");
		var memberCredential = new MemberCredential();
		memberCredential.setActive(true);
		memberCredential.setMemberType(MemberType.CLIENT);
		var returnedMember = new Client();
		returnedMember.setId(1L);

//		doNothing().when(authenticationManager).authenticate(any());
		when(memberCredentialRepository.findByLogin(anyString()))
			.thenReturn(Optional.of(memberCredential));
		when(jwtService.generateToken(any(MemberCredential.class), anyLong()))
			.thenReturn("JwtTokenTest");
		when(refreshTokenService.createRefreshToken(any()))
			.thenReturn(createRefreshToken(1L, memberCredential));
		when(clientRepository.findByMemberCredential(any(MemberCredential.class))).thenReturn(Optional.of(returnedMember));

		// act
		var response = authenticationService.loginMember(loginRequest);

		// assert
		Assertions.assertAll(
			() -> Assertions.assertNotNull(response.refreshToken()),
			() -> Assertions.assertFalse(response.token().isBlank()),
			() -> verify(refreshTokenService).createRefreshToken(any())
		);
	}

	private RefreshToken createRefreshToken(Long id, MemberCredential memberCredential) {
		return new RefreshToken(1L, UUID.randomUUID(), memberCredential, LocalDateTime.now(CLOCK));
	}

	@Test
	public void testRefreshToken_hasExpired() {
		// arrange
		var refreshTokenRequest = new RefreshTokenRequest(UUID.randomUUID());
		var memberCredential = new MemberCredential();
		var refreshToken = createRefreshToken(1L, memberCredential);
		when(refreshTokenService.getByToken(any(UUID.class)))
			.thenReturn(refreshToken);
		doThrow(ResponseStatusException.class).when(refreshTokenService)
											  .isAfterExpirationDate(any(RefreshToken.class));

		// act
		Executable shouldFail = () -> authenticationService.refreshToken(refreshTokenRequest);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	@Test
	public void testRefreshToken_valid() {
		// arrange
		var refreshTokenRequest = new RefreshTokenRequest(UUID.randomUUID());
		var memberCredential = new MemberCredential();
		memberCredential.setMemberType(MemberType.CLIENT);
		var refreshToken = createRefreshToken(1L, memberCredential);
		var returnedMember = new Client();
		returnedMember.setId(1L);
		when(refreshTokenService.getByToken(any(UUID.class)))
			.thenReturn(refreshToken);
		doNothing().when(refreshTokenService)
				   .isAfterExpirationDate(any(RefreshToken.class));
		when(jwtService.generateToken(any(MemberCredential.class), anyLong()))
			.thenReturn("JwtTokenTest");
		when(clientRepository.findByMemberCredential(any(MemberCredential.class))).thenReturn(Optional.of(returnedMember));

		// act
		var refreshTokenResponse = authenticationService.refreshToken(refreshTokenRequest);

		// assert
		Assertions.assertAll(
			() -> Assertions.assertNotNull(refreshTokenResponse.refreshToken()),
			() -> Assertions.assertFalse(refreshTokenResponse.token().isBlank())
		);
	}

	@Test
	public void testLogout_noRefreshToken() {
		// arrange
		var logoutRequest = new LogoutRequest(null);

		// act
		authenticationService.logoutMember(logoutRequest);

		// assert
		verify(refreshTokenService, never()).deleteRefreshToken(any(UUID.class));
	}

	@Test
	public void testLogout_refreshTokenPresent() {
		// arrange
		var logoutRequest = new LogoutRequest(UUID.randomUUID());

		// act
		authenticationService.logoutMember(logoutRequest);

		// assert
		verify(refreshTokenService).deleteRefreshToken(any(UUID.class));
	}
}
