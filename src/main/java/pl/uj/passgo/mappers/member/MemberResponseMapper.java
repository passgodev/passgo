package pl.uj.passgo.mappers.member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.uj.passgo.mappers.client.ClientMapper;
import pl.uj.passgo.mappers.organizer.OrganizerMapper;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.member.Organizer;
import pl.uj.passgo.models.responses.member.ClientMemberResponse;
import pl.uj.passgo.models.responses.member.OrganizerMemberResponse;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MemberResponseMapper {
	private final ClientMapper clientMapper;
	private final OrganizerMapper organizerMapper;

	public ClientMemberResponse toClientMemberResponse(Client client) {
		var clientDto = clientMapper.toClientDto(client);
		return new ClientMemberResponse(clientDto);
	}

	public OrganizerMemberResponse toOrganizerMemberResponse(Organizer organizer) {
		var organizerDto = organizerMapper.toOrganizerDto(organizer);
		return new OrganizerMemberResponse(organizerDto);
	}
}
