package pl.uj.passgo.services.member;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.mappers.member.MemberResponseMapper;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.models.member.MemberType;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;
import pl.uj.passgo.services.LoggedInMemberContextService;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
	@Mock
	private ClientRepository clientRepository;
	@Mock
	private OrganizerRepository organizerRepository;
	@Mock
	private MemberResponseMapper memberResponseMapper;
	@Mock
	private LoggedInMemberContextService loggedInMemberContextService;

	@InjectMocks
	private MemberService memberService;


	@Test
	public void testGetClientById_clientGetsClientDoesNotExist() {
		// arrange
		Long clientId = 1L;
		var memberType = MemberType.CLIENT;
		when(loggedInMemberContextService.getLoggedInMemberCredential()).thenReturn(getValidMemberCredential(memberType));
		when(clientRepository.findByMemberCredential(any(MemberCredential.class)))
			.thenReturn(Optional.empty());

		// act
		Executable shouldFail = () -> memberService.getMemberById(clientId, MemberType.CLIENT);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	@ParameterizedTest
	@MethodSource("getMemberTypeOtherThanClient")
	public void testGetClientById_OtherMemberTypeGetsClientDoesNotExist(MemberType memberType) {
		// arrange
		Long clientId = 1L;
		when(loggedInMemberContextService.getLoggedInMemberCredential()).thenReturn(getValidMemberCredential(memberType));
		when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

		// act
		Executable shouldFail = () -> memberService.getMemberById(clientId, MemberType.CLIENT);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	private static Stream<Arguments> getMemberTypeOtherThanClient() {
		return Stream.of(
			Arguments.of(MemberType.ORGANIZER),
			Arguments.of(MemberType.ADMINISTRATOR)
		);
	}

	@Test
	public void testGetClientById_clientGetsOnlyItself() {
		// arrange
		var clientId = 1L;
		var memberCredential = getValidMemberCredential(MemberType.CLIENT);
		memberCredential.setId(clientId + 1);
		when(loggedInMemberContextService.getLoggedInMemberCredential()).thenReturn(memberCredential);

		// act
		Executable shouldFail = () -> memberService.getMemberById(clientId, MemberType.CLIENT);

		// act
		Assertions.assertAll(
			() -> Assertions.assertThrows(ResponseStatusException.class, shouldFail)
		);
	}

	@Test
	public void testGetAdminById_cannotGetAdmin() {
		// arrange
		Long adminId = 1L;

		// act
		Executable shouldFail = () -> memberService.getMemberById(adminId,  MemberType.ADMINISTRATOR);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	private static MemberCredential getValidMemberCredential(MemberType memberType) {
		return new MemberCredential(
			1L,
			"login",
			"password",
			memberType,
			true
		);
	}
}
