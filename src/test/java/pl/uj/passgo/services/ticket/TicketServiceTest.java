package pl.uj.passgo.services.ticket;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.TicketPurchaseRequest;
import pl.uj.passgo.models.DTOs.ticket.TicketFullResponse;
import pl.uj.passgo.models.DTOs.ticket.TicketInfoDto;
import pl.uj.passgo.models.DTOs.ticket.TicketResponse;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.repos.*;
import pl.uj.passgo.repos.member.ClientRepository;
import pl.uj.passgo.repos.transaction.TransactionComponentRepository;
import pl.uj.passgo.repos.transaction.TransactionRepository;
import pl.uj.passgo.services.LoggedInMemberContextService;
import pl.uj.passgo.services.TicketService;
import pl.uj.passgo.services.WalletOperationService;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

	@Mock
	private TicketRepository ticketRepository;
	@Mock
	private EventRepository eventRepository;
	@Mock
	private SeatRepository seatRepository;
	@Mock
	private SectorRepository sectorRepository;
	@Mock
	private RowRepository rowRepository;
	@Mock
	private ClientRepository clientRepository;
	@Mock
	private WalletOperationService walletOperationService;
	@Mock
	private LoggedInMemberContextService loggedInMemberContextService;
	@Mock
	private TransactionRepository transactionRepository;
	@Mock
	private TransactionComponentRepository transactionComponentRepository;
	@Mock
	private Clock clock;

	@InjectMocks
	private TicketService ticketService;

	private final Clock fixedClock = Clock.fixed(
			Instant.parse("2024-01-01T10:00:00Z"),
			ZoneId.of("UTC")
	);

	@BeforeEach
	void setUp() {
				ticketService = new TicketService(
				ticketRepository,
				eventRepository,
				seatRepository,
				sectorRepository,
				rowRepository,
				clientRepository,
				walletOperationService,
				loggedInMemberContextService,
				fixedClock,
				transactionRepository,
				transactionComponentRepository
		);
	}

	@Test
	public void buyTickets() {
		// Arrange
		var ticket = new Ticket();
		ticket.setId(1L);
		ticket.setPrice(BigDecimal.TEN);

		var wallet = new Wallet();
		wallet.setMoney(BigDecimal.TEN);

		var client = new Client();
		client.setWallet(wallet);

		pl.uj.passgo.models.DTOs.ticket.TicketPurchaseRequest ticketsPurchaseRequest = new pl.uj.passgo.models.DTOs.ticket.TicketPurchaseRequest(List.of(1L));

		when(ticketRepository.getTicketsByIdIn(List.of(1L))).thenReturn(List.of(ticket));
		when(loggedInMemberContextService.isClientLoggedIn()).thenReturn(Optional.of(client));
		doAnswer(invocation -> {
			client.getWallet().setMoney(BigDecimal.ZERO);
			return null;
		}).when(walletOperationService).chargeWalletForTicketPurchase(any(Client.class), any(BigDecimal.class));
		when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		// Act
		var response = ticketService.purchaseTickets(ticketsPurchaseRequest);

		// Assert
		Assertions.assertAll(
			() -> Assertions.assertNotNull(ticket.getOwner()),
			() -> Assertions.assertEquals(BigDecimal.ZERO, wallet.getMoney()),
			() -> Assertions.assertEquals(1, response.ticketQuantity()),
			() -> Assertions.assertEquals(BigDecimal.TEN, response.totalPrice())
		);
	}
	@Test
	void getAllAvailableTicketsForEvent_shouldReturnList() {
		// Arrange
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		ticket.setPrice(BigDecimal.valueOf(20));

		Seat seat = new Seat().builder()
				.id(1L)
				.seatNumber(10L)
				.build();
		Row row = new Row().builder()
				.id(1L)
				.rowNumber(5L)
				.build();
		Sector sector = new Sector().builder()
				.id(1L)
				.name("A1")
				.build();

		ticket.setSeat(seat);
		ticket.setRow(row);
		ticket.setSector(sector);
		ticket.setStandingArea(false);

		when(ticketRepository.findAllByEventIdAndOwnerIsNull(1L)).thenReturn(List.of(ticket));

		// Act
		List<TicketResponse> result = ticketService.getAllAvailableTicketsForEvent(1L);

		// Assert
		assertEquals(1, result.size());
		assertEquals(1L, result.getFirst().getId());
		assertEquals("A1", result.getFirst().getSectorName());
	}

	@Test
	void deleteTicket_shouldDeleteWhenNoOwner() {
		// Arrange
		Ticket ticket = new Ticket();
		ticket.setId(1L);

		when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

		// Act
		ticketService.deleteTicket(1L);

		// Assert
		verify(ticketRepository).deleteById(1L);
	}

	@Test
	void getTicketByClientId_shouldReturnResponses() {
		// Arrange
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		ticket.setPrice(BigDecimal.TEN);
		ticket.setStandingArea(false);

		Row row = new Row();
		row.setRowNumber(5L);
		Seat seat = new Seat();
		seat.setSeatNumber(10L);
		Sector sector = new Sector();
		sector.setName("A1");
		Event event = new Event();
		event.setName("Concert");

		ticket.setRow(row);
		ticket.setSeat(seat);
		ticket.setSector(sector);
		ticket.setEvent(event);

		when(ticketRepository.findAllByOwnerId(1L)).thenReturn(List.of(ticket));

		// Act
		List<TicketFullResponse> responses = ticketService.getTicketByClientId(1L);

		// Assert
		assertEquals(1, responses.size());
		assertEquals("Concert", responses.getFirst().getEventName());
	}

	@Test
	void returnTicket_shouldResetOwnerAndRefund() {
		// Arrange
		Client client = new Client();
		client.setWallet(new Wallet());

		Ticket ticket = new Ticket();
		ticket.setId(1L);
		ticket.setPrice(BigDecimal.TEN);
		ticket.setOwner(client);

		when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
		when(loggedInMemberContextService.isClientLoggedIn()).thenReturn(Optional.of(client));
		when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		// Act
		ticketService.returnTicket(1L);

		// Assert
		assertNull(ticket.getOwner());
		verify(walletOperationService).rechargeWalletForTicketReturn(eq(client), eq(BigDecimal.TEN));
		verify(transactionComponentRepository).save(any());
	}

	@Test
	void getTicketById_shouldReturnTicket() {
		// Arrange
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

		// Act
		Ticket result = ticketService.getTicketById(1L);
		// Assert
		assertEquals(1L, result.getId());
	}

	@Test
	void getTicketById_shouldThrowIfNotFound() {
		// Arrange
		when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(Exception.class, () -> ticketService.getTicketById(1L));
	}

	@Test
	void getTicketsInfoByEventId_shouldReturnInfo() {
		// Arrange
		TicketInfoDto dto = new TicketInfoDto("name", 1L, 10L, BigDecimal.TEN);
		when(ticketRepository.getTicketSummaryByEvent(1L)).thenReturn(List.of(dto));

		// Act
		List<TicketInfoDto> result = ticketService.getTicketsInfoByEventId(1L);

		// Assert
		assertEquals(1, result.size());
		assertEquals(dto, result.getFirst());
	}

	@Test
	void deleteAllTicketsConnectedToEvent_shouldDeleteAll() {
		// Arrange
		Event futureEvent = new Event();
		futureEvent.setDate(LocalDateTime.now(fixedClock).plusDays(1));

		Client client = new Client();
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		ticket.setOwner(client);
		ticket.setEvent(futureEvent);
		ticket.setPrice(BigDecimal.TEN);

		when(ticketRepository.findAllByEventId(1L)).thenReturn(List.of(ticket));
		when(loggedInMemberContextService.isClientLoggedIn()).thenReturn(Optional.of(client));
		when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		// Act
		ticketService.deleteAllTicketsConnectedToEvent(1L);

		// Assert
		verify(walletOperationService).rechargeWalletForTicketReturn(any(), any());
		verify(ticketRepository).deleteAll(any());
	}

	@Test
	void updateTicket_shouldUpdateFields() {
		// Arrange
		Event event = new Event();
		event.setId(100L);
		Sector sector = new Sector();
		sector.setId(200L);
		Row row = new Row();
		row.setId(300L);
		Seat seat = new Seat();
		seat.setId(400L);
		Client owner = new Client();
		owner.setId(500L);

		Ticket ticket = new Ticket();
		ticket.setId(1L);
		ticket.setPrice(BigDecimal.TEN);
		ticket.setStandingArea(false);
		ticket.setEvent(event);
		ticket.setSector(sector);
		ticket.setRow(row);
		ticket.setSeat(seat);
		ticket.setOwner(owner);

		when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
		when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		TicketPurchaseRequest updated = new TicketPurchaseRequest();
		updated.setPrice(BigDecimal.valueOf(25));
		updated.setStandingArea(true);
		updated.setEventId(100L);
		updated.setSectorId(200L);
		updated.setRowId(300L);
		updated.setSeatId(400L);
		updated.setOwnerId(500L);

		// Act
		Ticket result = ticketService.updateTicket(updated, 1L);

		// Assert
		assertEquals(BigDecimal.valueOf(25), result.getPrice());
		assertTrue(result.getStandingArea());
	}
}
