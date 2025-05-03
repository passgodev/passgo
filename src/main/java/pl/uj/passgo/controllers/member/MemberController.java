package pl.uj.passgo.controllers.member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.member.MemberType;
import pl.uj.passgo.models.responses.member.MemberResponse;
import pl.uj.passgo.models.responses.member.OrganizerMemberResponse;
import pl.uj.passgo.services.member.MemberService;


@RestController
@RequestMapping("/members")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MemberController {
	private final MemberService memberService;

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<MemberResponse>> getMembers(@RequestParam(value = "type") MemberType type, @PageableDefault Pageable pageable) {
		var membersResponse = memberService.getMembersByType(type, pageable);
		return ResponseEntity.ok(membersResponse);
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	@PatchMapping(value = "/organizers/{organizer-id}/activation", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrganizerMemberResponse> activateOrganizer(@PathVariable("organizer-id") Long organizerId) {
		var approvedOrganizerResponse = memberService.activateOrganizer(organizerId);
		return ResponseEntity.ok(approvedOrganizerResponse);
	}
}
