package pl.uj.passgo.models.responses.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import pl.uj.passgo.models.DTOs.member.OrganizerDto;


public record OrganizerMemberResponse(
	@JsonUnwrapped
	OrganizerDto organizerDto
) implements MemberResponse {
}
