package pl.uj.passgo.mappers.event;

import org.springframework.stereotype.Service;
import pl.uj.passgo.models.DTOs.event.EventDto;
import pl.uj.passgo.models.Event;


@Service
public class EventMapper {
	public EventDto toEventDto(Event event) {
		return new EventDto(
			event.getId(),
			event.getName(),
			event.getDate(),
			event.getDescription(),
			event.getCategory()
		);
	}
}
