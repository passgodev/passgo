package pl.uj.passgo.services.member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.mappers.member.MemberResponseMapper;
import pl.uj.passgo.models.member.MemberType;
import pl.uj.passgo.models.responses.member.MemberResponse;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;


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
}
