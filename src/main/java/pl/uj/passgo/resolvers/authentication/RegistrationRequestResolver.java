package pl.uj.passgo.resolvers.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pl.uj.passgo.models.DTOs.authentication.registration.ClientRegistrationRequest;
import pl.uj.passgo.models.DTOs.authentication.registration.MemberRegistrationRequest;
import pl.uj.passgo.models.DTOs.authentication.registration.OrganizerRegistrationRequest;
import pl.uj.passgo.models.member.MemberType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegistrationRequestResolver implements HandlerMethodArgumentResolver {
	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DynamicMemberRegistrationRequest {
		public String requestParam() default QUERY_PARAM_NAME;
	}

	private static final String QUERY_PARAM_NAME = "member";
	private final ObjectMapper objectMapper;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(DynamicMemberRegistrationRequest.class) != null &&
			MemberRegistrationRequest.class.isAssignableFrom(parameter.getParameterType());
	}

	private static String retrieveParameterName(MethodParameter methodParameter) {
		var paramAnnotation = methodParameter.getParameterAnnotation(DynamicMemberRegistrationRequest.class);
		assert paramAnnotation != null;

		return paramAnnotation.requestParam();
	}

	private static MemberType parseMemberType(String queryParameterValue) {
		var typeParam = Optional.ofNullable(queryParameterValue)
								.orElseThrow(() -> new RuntimeException(QUERY_PARAM_NAME + " must be specified, available options are: " + Arrays.toString(Arrays.stream(MemberType.values())
																																								 .map(Enum::name)
																																								 .toArray())));
		return MemberType.valueOf(typeParam.toUpperCase(Locale.ROOT));
	}

	private static String retrieveJsonBody(NativeWebRequest webRequest) throws Exception {
		var servletRequest = Optional.ofNullable(webRequest.getNativeRequest(HttpServletRequest.class))
			.orElseThrow(() -> new RuntimeException("NativeRequest HttpServletRequest could not be retrieved"));

		return servletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory
	) throws Exception {
		var requestParam = retrieveParameterName(parameter);
		var memberType = parseMemberType(webRequest.getParameter(requestParam));

		var jsonBody = retrieveJsonBody(webRequest);
		var targetClass = switch (memberType) {
			case MemberType.CLIENT -> ClientRegistrationRequest.class;
			case MemberType.ORGANIZER -> OrganizerRegistrationRequest.class;
			case MemberType.ADMINISTRATOR -> throw new RuntimeException("Admin registration is disabled");
		};

		return objectMapper.readValue(jsonBody, targetClass);
	}
}
