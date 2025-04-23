package pl.uj.passgo.mappers.role;

import pl.uj.passgo.configuration.security.role.Privilege;
import pl.uj.passgo.models.member.MemberType;


public class PrivilegeMapper {
	public static Privilege fromMemberType(MemberType memberType) {
		return switch (memberType) {
			case CLIENT -> Privilege.CLIENT;
			case ORGANIZER -> Privilege.ORGANIZER;
			case ADMINISTRATOR -> Privilege.ADMINISTRATOR;
		};
	}
}
