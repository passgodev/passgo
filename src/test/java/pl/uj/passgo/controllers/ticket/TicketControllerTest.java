package pl.uj.passgo.controllers.ticket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.uj.passgo.models.DTOs.ticket.TicketFullResponse;
import pl.uj.passgo.models.DTOs.ticket.TicketInfoDto;
import pl.uj.passgo.models.DTOs.ticket.TicketPurchaseResponse;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.services.PDFGenerator;
import pl.uj.passgo.services.TicketService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private PDFGenerator pdfGenerator;

    @Test
    void getAllTickets_shouldReturnPage() throws Exception {
        // Arrange
        Page<TicketFullResponse> page = new PageImpl<>(List.of(new TicketFullResponse()));
        when(ticketService.getAllTickets(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0]").exists());

        verify(ticketService).getAllTickets(any(Pageable.class));
    }

    @Test
    void getTicketById_shouldReturnTicket() throws Exception {
        // Arrange
        TicketFullResponse ticket = new TicketFullResponse();
        ticket.setId(1L);
        when(ticketService.getTicketFullResponseById(1L)).thenReturn(ticket);

        // Act & Assert
        mockMvc.perform(get("/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(ticketService).getTicketFullResponseById(1L);
    }

    @Test
    void getTicketByClientId_shouldReturnList() throws Exception {
        // Arrange
        TicketFullResponse response = mock(TicketFullResponse.class);
        when(ticketService.getTicketByClientId(5L)).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/tickets/client/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(ticketService).getTicketByClientId(5L);
    }

    @Test
    void getTicketsInfoByEventId_shouldReturnList() throws Exception {
        // Arrange
        TicketInfoDto infoDto = mock(TicketInfoDto.class);
        when(ticketService.getTicketsInfoByEventId(10L)).thenReturn(List.of(infoDto));

        // Act & Assert
        mockMvc.perform(get("/tickets/10/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(ticketService).getTicketsInfoByEventId(10L);
    }

    @Test
    void getTicketPdf_shouldReturnPdf() throws Exception {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        byte[] pdfBytes = "pdfContent".getBytes();

        when(ticketService.getTicketById(1L)).thenReturn(ticket);
        when(pdfGenerator.generateTicketPdf(ticket)).thenReturn(pdfBytes);

        // Act & Assert
        mockMvc.perform(get("/tickets/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"ticket_1.pdf\""))
                .andExpect(content().bytes(pdfBytes));

        verify(ticketService).getTicketById(1L);
        verify(pdfGenerator).generateTicketPdf(ticket);
    }

    @Test
    void purchaseTickets_shouldReturnResponse() throws Exception {
        // Arrange
        TicketPurchaseResponse response = mock(TicketPurchaseResponse.class);
        when(ticketService.purchaseTickets(any())).thenReturn(response);

        String jsonRequest = "{\"ticketIds\":[1,2,3]}";

        // Act & Assert
        mockMvc.perform(post("/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(ticketService).purchaseTickets(any());
    }

    @Test
    void updateTicket_shouldReturnUpdatedTicket() throws Exception {
        // Arrange
        TicketFullResponse updated = new TicketFullResponse();
        updated.setId(1L);
        when(ticketService.updateTicket(any(), eq(1L))).thenReturn(updated);

        String jsonRequest = "{\"price\":25,\"standingArea\":true}";

        // Act & Assert
        mockMvc.perform(put("/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(ticketService).updateTicket(any(), eq(1L));
    }

    @Test
    void deleteTicket_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(ticketService).deleteTicketWithRefund(1L);

        // Act & Assert
        mockMvc.perform(delete("/tickets/1"))
                .andExpect(status().isNoContent());

        verify(ticketService).deleteTicketWithRefund(1L);
    }

    @Test
    void returnTicket_shouldReturnSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(ticketService).returnTicket(1L);

        // Act & Assert
        mockMvc.perform(post("/tickets/1/return"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ticket with id: 1 was succesfully returned"));

        verify(ticketService).returnTicket(1L);
    }
}