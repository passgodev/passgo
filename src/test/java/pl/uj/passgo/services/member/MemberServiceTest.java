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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.mappers.client.ClientMapper;
import pl.uj.passgo.mappers.member.MemberResponseMapper;
import pl.uj.passgo.mappers.organizer.OrganizerMapper;
import pl.uj.passgo.models.DTOs.member.OrganizerDto;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.models.member.MemberType;
import pl.uj.passgo.models.member.Organizer;
import pl.uj.passgo.models.responses.member.OrganizerMemberResponse;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;
import pl.uj.passgo.services.LoggedInMemberContextService;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
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

	@Spy
	private OrganizerMapper organizerMapper;

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

	@Test
	public void testGetMembersById_cannotGetAdmin() {
		// arrange
		var memberType = MemberType.ADMINISTRATOR;

		// act
		Executable shouldFail = () -> memberService.getMembersByType(memberType, Pageable.unpaged());

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	@Test
	public void testActivateOrganizer_organizerNotExist() {
		// arrange
		Long organizerId = 1L;
		when(organizerRepository.findById(organizerId)).thenReturn(Optional.empty());

		// act
		Executable shouldFail = () -> memberService.activateOrganizer(organizerId);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	@Test
	public void testActivateOrganizer_ativated() {
		// arrange
		// create organizer object
		var organizerCredential = getValidMemberCredential(MemberType.ORGANIZER);
		var organizerId = organizerCredential.getId();
		var organizer = new Organizer();
		organizer.setId(organizerId);
		organizer.setMemberCredential(organizerCredential);
		when(organizerRepository.findById(eq(organizerId))).thenReturn(Optional.of(organizer));
		// mock mapper to obtain mapped organizer
		var mappedOrganizer = organizerMapper.toOrganizerDto(organizer);
		when(memberResponseMapper.toOrganizerMemberResponse(any(Organizer.class)))
			.thenReturn(new OrganizerMemberResponse(mappedOrganizer));

		// act
		memberService.activateOrganizer(organizerId);

		// assert
		Assertions.assertTrue(organizerCredential.isActive());
	}
}
