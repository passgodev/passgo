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
import pl.uj.passgo.models.responses.member.MemberResponse;
import pl.uj.passgo.models.responses.member.OrganizerMemberResponse;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MemberService {
	private final ClientRepository clientRepository;
	private final OrganizerRepository organizerRepository;
	private final MemberResponseMapper memberResponseMapper;

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
									  var message = String.format("Organizer with id: %d not found", organizerId);
									  log.error(message);
									  return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
								  });
	}

	private static Organizer activateOrganizer(Organizer organizer) {
		organizer.setActive(true);
		return organizer;
	}
}
