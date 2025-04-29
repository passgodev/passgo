package pl.uj.passgo.models.responses.member;

public sealed interface MemberResponse permits ClientMemberResponse, OrganizerMemberResponse {
}
