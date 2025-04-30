package pl.uj.passgo.mappers.organizer;

import org.springframework.stereotype.Service;
import pl.uj.passgo.models.DTOs.member.OrganizerDto;
import pl.uj.passgo.models.member.Organizer;


@Service
public class OrganizerMapper {
	public OrganizerDto toOrganizerDto(Organizer organizer) {
		return new OrganizerDto(
			organizer.getId(),
			organizer.getFirstName(),
			organizer.getLastName(),
			organizer.getEmail(),
			organizer.getRegistrationDate(),
			organizer.getBirthDate(),
			organizer.getMemberCredential()
					 .isActive(),
			organizer.getOrganization()
		);
	}
}
