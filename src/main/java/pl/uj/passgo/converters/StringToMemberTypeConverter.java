package pl.uj.passgo.converters;

import org.springframework.core.convert.converter.Converter;
import pl.uj.passgo.models.member.MemberType;

import java.util.Locale;


public class StringToMemberTypeConverter implements Converter<String, MemberType> {
	@Override
	public MemberType convert(String source) {
		return MemberType.valueOf(source.toUpperCase(Locale.ENGLISH));
	}
}
