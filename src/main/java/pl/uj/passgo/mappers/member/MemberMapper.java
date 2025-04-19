package pl.uj.passgo.mappers.member;

import org.springframework.stereotype.Service;
import pl.uj.passgo.models.DTOs.authentication.registration.ClientRegistrationRequest;
import pl.uj.passgo.models.DTOs.authentication.registration.OrganizerRegistrationRequest;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.member.Organizer;


@Service
public class MemberMapper {
	public Client toClient(ClientRegistrationRequest request) {
		var client = new Client();

		client.setFirstName(request.firstName());
		client.setLastName(request.lastName());
		client.setEmail(request.getCredentials().email());
		client.setBirthDate(request.birthDate());
		client.setActive(true);

		return client;
	}

	public Organizer toOrganizer(OrganizerRegistrationRequest request) {
		var organizer = new Organizer();

		organizer.setFirstName(request.firstName());
		organizer.setLastName(request.lastName());
		organizer.setEmail(request.getCredentials().email());
		organizer.setBirthDate(request.birthDate());
		organizer.setActive(true);
		organizer.setOrganization(request.organization());

		return organizer;
	}
}
