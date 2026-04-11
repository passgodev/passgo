package pl.uj.passgo.controllers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.uj.passgo.models.DTOs.SaleInfoDto;
import pl.uj.passgo.models.DTOs.TicketPurchaseRequest;
import pl.uj.passgo.models.DTOs.ticket.*;
import pl.uj.passgo.services.PDFGenerator;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.services.TicketSaleService;
import pl.uj.passgo.services.TicketService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Validated
public class TicketController {

    private final TicketService ticketService;
    private final PDFGenerator pdfGenerator;
    private final TicketSaleService ticketResellService;

    @GetMapping
    public ResponseEntity<Page<TicketFullResponse>> getAllTickets(@PageableDefault Pageable pageable) {
        var tickets = ticketService.getAllTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketFullResponse> getTicketById(@PathVariable("id") Long id) {
        var ticket = ticketService.getTicketFullResponseById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<List<TicketFullResponse>> getTicketByClientId(@PathVariable("id") Long id) {
        List<TicketFullResponse> tickets = ticketService.getTicketByClientId(id);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{eventId}/info")
    public ResponseEntity<List<TicketInfoDto>> getTicketsInfoByEventId(@PathVariable("eventId") Long eventId) {
        var ticketsInfoDto = ticketService.getTicketsInfoByEventId(eventId);
        return ResponseEntity.ok(ticketsInfoDto);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getTicketPdf(@PathVariable("id") Long id) {
        Ticket ticket = ticketService.getTicketById(id);

        byte[] pdf = pdfGenerator.generateTicketPdf(ticket);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("ticket_" + id + ".pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @PostMapping("/purchase")
    public ResponseEntity<TicketPurchaseResponse> purchaseTickets(@RequestBody BulkTicketPurchaseRequest tickets) {
        var purchasedTicketsResponse = ticketService.orderTickets(tickets.ticketIds());
        return ResponseEntity.ok(purchasedTicketsResponse);
    }

    //TODO: do zmiany (trzeba przeciez przeniść kase z sellera na buyera - nie można używwać tej samej metody)
    @PostMapping("/purchase-on-sale")
    public ResponseEntity<TicketPurchaseResponse> purchaseTicketsOnSale(@RequestBody BulkTicketPurchaseRequest tickets) {
        var purchasedTicketResponse = ticketResellService.orderOfferedTickets(tickets.ticketIds());
        return ResponseEntity.ok(purchasedTicketResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketFullResponse> updateTicket(@RequestBody TicketPurchaseRequest ticket, @PathVariable Long id) {
        var updatedTicket = ticketService.updateTicket(ticket, id);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicketWithRefund(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<String> returnTicket(@PathVariable Long id){
        ticketService.returnTicket(id);
        return ResponseEntity.ok(String.format("Ticket with id: %d was succesfully returned", id));
    }

    @PostMapping("/{id}/re-sell")
    public ResponseEntity<Void> resellTicket(@PathVariable Long id, @RequestParam @NotNull @Min(0) BigDecimal price) {
        ticketResellService.offerTicket(id, price);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/for-sale")
    public ResponseEntity<List<SaleInfoDto>> getTicketsForSale(@RequestParam(required = false) Long eventId) {
        List<SaleInfoDto> saleInfo = ticketResellService.getTicketsForSale(eventId);
        return ResponseEntity.ok(saleInfo);
    }
}
