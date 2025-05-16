package pl.uj.passgo.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.uj.passgo.models.DTOs.ticket.TicketPurchaseRequest;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.SeatRepository;
import pl.uj.passgo.repos.TicketRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;


public class TicketServiceTest {
	@Mock
	private TicketRepository ticketRepository;
	@Mock
	private EventRepository eventRepository;
	@Mock
	private SeatRepository seatRepository;
	@Mock
	private LoggedInMemberContextService loggedInMemberContextService;

	@InjectMocks
	private TicketService ticketService;

	@BeforeEach
	public void initialize() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void buyTickets() {
		// arrange
		var ticket = new Ticket();
		ticket.setId(1L);
		ticket.setPrice(BigDecimal.TEN);

		var wallet = new Wallet();
		wallet.setMoney(BigDecimal.TEN);

		var client = new Client();
		client.setWallet(wallet);

		var ticketsPurchaseRequest = new TicketPurchaseRequest(List.of(1L));
		when(ticketRepository.getTicketsByIdIn(anyCollection())).thenReturn(List.of(ticket));
		when(loggedInMemberContextService.isClientLoggedIn()).thenReturn(Optional.of(client));

		// act
		var response = ticketService.purchaseTickets(ticketsPurchaseRequest);

		// assert
		Assertions.assertAll(
			() -> Assertions.assertNotNull(ticket.getOwner()),
			() -> Assertions.assertEquals(BigDecimal.ZERO, wallet.getMoney()),
			() -> Assertions.assertEquals(1, response.ticketQuantity()),
			() -> Assertions.assertEquals(BigDecimal.TEN, response.totalPrice())
		);
	}
}
