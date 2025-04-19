package pl.uj.passgo.mappers.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.uj.passgo.mappers.client.ClientMapper;
import pl.uj.passgo.mappers.event.EventMapper;
import pl.uj.passgo.models.DTOs.ticket.TicketDto;
import pl.uj.passgo.models.Ticket;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TicketMapper {
	private final EventMapper eventMapper;
	private final ClientMapper clientMapper;

	public TicketDto toDto(Ticket ticket) {
		var eventDto = eventMapper.toEventDto(ticket.getEvent());
		var clientDto = clientMapper.toClientDto(ticket.getOwner());

		return new TicketDto(
			ticket.getId(),
			ticket.getPrice(),
			eventDto,
			ticket.getSector().getId(),
			ticket.getRow().getId(),
			ticket.getSeat().getId(),
			ticket.getStandingArea(),
			clientDto
		);
	}
}
