package pl.uj.passgo.configuration.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.uj.passgo.models.member.Administrator;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.models.member.MemberType;
import pl.uj.passgo.repos.member.AdministratorRepository;
import pl.uj.passgo.repos.member.MemberCredentialRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminInitializer implements CommandLineRunner {
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final Clock clock;
	private final MemberCredentialRepository memberCredentialRepository;
	private final AdministratorRepository administratorRepository;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		if ( administratorRepository.count() != 0 ) {
			log.info("Administrators have not been added, something already exist");
			return;
		}

		// create admin
		var admin = new Administrator();
		admin.setFirstName("admin");
		admin.setLastName("admin");
		admin.setEmail("admin@admin.com");
		admin.setBirthDate(LocalDate.now(clock));
		admin.setRegistrationDate(LocalDateTime.now(clock));

		var adminCredential = new MemberCredential();
		adminCredential.setLogin("admin");
		adminCredential.setPassword(bCryptPasswordEncoder.encode("admin"));
		adminCredential.setMemberType(MemberType.ADMINISTRATOR);
		adminCredential.setActive(true);

		var savedAdminCredential = memberCredentialRepository.save(adminCredential);

		admin.setMemberCredential(savedAdminCredential);

		var savedAdmin = administratorRepository.save(admin);
		log.info("Added administrator with id {}", savedAdmin.getId());
	}
}
