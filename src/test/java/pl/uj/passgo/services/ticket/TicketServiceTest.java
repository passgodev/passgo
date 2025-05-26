package pl.uj.passgo.services.ticket;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import pl.uj.passgo.models.DTOs.ticket.TicketPurchaseRequest;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.models.Wallet;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.SeatRepository;
import pl.uj.passgo.repos.TicketRepository;
import pl.uj.passgo.repos.transaction.TransactionComponentRepository;
import pl.uj.passgo.repos.transaction.TransactionRepository;
import pl.uj.passgo.services.LoggedInMemberContextService;
import pl.uj.passgo.services.TicketService;
import pl.uj.passgo.services.WalletOperationService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doAnswer;
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
	@Mock
	private WalletOperationService walletOperationService;
	@Mock
	private TransactionRepository transactionRepository;
	@Mock
	private TransactionComponentRepository ticketTransactionComponentRepository;

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
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				var client = (Client)invocationOnMock.getArgument(0);
				client.getWallet().setMoney(wallet.getMoney().subtract(ticket.getPrice()));
				return null;
			}
		}).when(walletOperationService).chargeWalletForTicketPurchase(any(), any());

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
