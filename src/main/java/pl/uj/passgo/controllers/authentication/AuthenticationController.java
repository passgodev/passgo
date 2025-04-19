package pl.uj.passgo.controllers.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.uj.passgo.models.DTOs.authentication.login.LoginRequest;
import pl.uj.passgo.models.DTOs.authentication.login.LoginResponse;
import pl.uj.passgo.models.DTOs.authentication.registration.MemberRegistrationRequest;
import pl.uj.passgo.resolvers.authentication.RegistrationRequestResolver.DynamicMemberRegistrationRequest;
import pl.uj.passgo.services.authentication.AuthenticationService;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	public ResponseEntity<Void> registerMember(@DynamicMemberRegistrationRequest(requestParam = "member") MemberRegistrationRequest request) {
		authenticationService.registerNewMember(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> loginMember(@RequestBody LoginRequest request) {
		var responseToken = authenticationService.loginMember(request);
		return ResponseEntity.ok(responseToken);
	}
}
