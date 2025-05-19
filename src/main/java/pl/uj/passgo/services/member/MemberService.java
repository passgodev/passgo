package pl.uj.passgo.services.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.mappers.member.MemberResponseMapper;
import pl.uj.passgo.models.member.MemberType;
import pl.uj.passgo.models.member.Organizer;
import pl.uj.passgo.models.responses.member.ClientMemberResponse;
import pl.uj.passgo.models.responses.member.MemberResponse;
import pl.uj.passgo.models.responses.member.OrganizerMemberResponse;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;
import pl.uj.passgo.services.LoggedInMemberContextService;

import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MemberService {
	private final ClientRepository clientRepository;
	private final OrganizerRepository organizerRepository;
	private final MemberResponseMapper memberResponseMapper;
	private final LoggedInMemberContextService loggedInMemberContextService;

	public MemberResponse getMemberById(Long id, MemberType type) {
		return switch (type) {
			case CLIENT -> getClientById(id);
			case ORGANIZER -> getOrganizerById(id);
			case ADMINISTRATOR -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin can not be specified");
		};
	}

	/// Logged-in as client, can only retrieve information about itself (client). Other roles can retrieve information about any client
	private ClientMemberResponse getClientById(Long id) {
		var loggedInMemberCredential = loggedInMemberContextService.getLoggedInMemberCredential();

		return switch ( loggedInMemberCredential.getMemberType()) {
			case CLIENT -> {
				if ( !Objects.equals(loggedInMemberCredential.getId(), id) ) {
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Logged in client can only fetch its own information");
				}

				yield clientRepository.findByMemberCredential(loggedInMemberCredential)
									  .map(memberResponseMapper::toClientMemberResponse)
									  .orElseThrow(() -> {
										  log.error("Could not find client with id {}", id);
										  log.error("Invalid MemberCredential and Client state in database, credentials can not exist on its own without client and vice versa");
										  return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Client with id: %d not found", id));
									  });
			}
			case ORGANIZER, ADMINISTRATOR -> clientRepository.findById(id)
															 .map(memberResponseMapper::toClientMemberResponse)
															 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Client with id: %d not found", id)));
		};
	}

	/// logged-in as organizer, administrator can retrieve information about all-organizers
	private OrganizerMemberResponse getOrganizerById(Long id) {
		return organizerRepository.findById(id)
								  .map(memberResponseMapper::toOrganizerMemberResponse)
								  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Organizer with id: %d not found", id)));
	}

	public Page<MemberResponse> getMembersByType(MemberType memberType, Pageable pageable) {
		return switch ( memberType ) {
			case CLIENT -> getClients(pageable);
			case ORGANIZER -> getOrganizers(pageable);
			case ADMINISTRATOR -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin can not be specified");
		};
	}

	private Page<MemberResponse> getClients(Pageable pageable) {
		return clientRepository.findAll(pageable).map(memberResponseMapper::toClientMemberResponse);
	}

	private Page<MemberResponse> getOrganizers(Pageable pageable) {
		return organizerRepository.findAll(pageable).map(memberResponseMapper::toOrganizerMemberResponse);
	}

	@Transactional
	public OrganizerMemberResponse activateOrganizer(Long organizerId) {
		return organizerRepository.findById(organizerId)
								  .map(MemberService::activateOrganizer)
								  .map(memberResponseMapper::toOrganizerMemberResponse)
								  .orElseThrow(() -> {
									  var message = String.format("Organizer with id: %d not found and thus can not be activated", organizerId);
									  log.error(message);
									  return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
								  });
	}

	private static Organizer activateOrganizer(Organizer organizer) {
		organizer.getMemberCredential().setActive(true);
		return organizer;
	}
}
