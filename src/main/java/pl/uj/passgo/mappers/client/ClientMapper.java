package pl.uj.passgo.mappers.client;

import org.springframework.stereotype.Service;
import pl.uj.passgo.models.DTOs.member.ClientDto;
import pl.uj.passgo.models.member.Client;


@Service
public class ClientMapper {
	public ClientDto toClientDto(Client client) {
		return new ClientDto(
			client.getId(),
			client.getFirstName(),
			client.getLastName(),
			client.getEmail(),
			client.getRegistrationDate(),
			client.getBirthDate(),
			client.isActive()
		);
	}
}
