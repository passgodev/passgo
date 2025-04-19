package pl.uj.passgo.models.DTOs.transaction;

import jakarta.persistence.*;
import pl.uj.passgo.models.DTOs.ticket.TicketDto;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.models.transaction.Transaction;


public record TransactionComponentDto(
	Long id,
	TicketDto ticket
) {
}
