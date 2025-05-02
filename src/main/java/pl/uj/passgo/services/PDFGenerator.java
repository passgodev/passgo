package pl.uj.passgo.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.Address;
import pl.uj.passgo.models.Ticket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class PDFGenerator {

    private static final String EMPTY_VALUE = "---";

    public byte[] generateTicketPdf(Ticket ticket) {
        var out = new ByteArrayOutputStream();

        try(Document document = new Document()) {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(addImage());

            var ticketIdParagraph = new Paragraph("Ticket ID: " + ticket.getId(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            ticketIdParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(ticketIdParagraph);

            document.add(addQRCode(ticket.getId()));

            document.add(addTable(ticket));
            log.info("PDF generated successfully for ticket ID: {}", ticket.getId());
        } catch (IOException e) {
            log.error("I/O error while generating PDF for ticket ID {}: {}", ticket.getId(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF for ticket ID: " + ticket.getId(), e);
        } catch (DocumentException e) {
            log.error("PDF document error for ticket ID {}: {}", ticket.getId(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF for ticket ID: " + ticket.getId(), e);
        } catch (Exception e) {
            log.error("Unexpected error while generating PDF for ticket ID {}: {}", ticket.getId(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF for ticket ID: " + ticket.getId(), e);
        }
        return out.toByteArray();
    }

    private Image addImage() throws IOException, BadElementException {
        var image = Image.getInstance(new ClassPathResource("passgo.png").getURL());
        float maxWidth = 400f;

        if (image.getWidth() > maxWidth) {
            float scalePercent = (maxWidth / image.getWidth()) * 100;
            image.scalePercent(scalePercent);
        }

        image.setAlignment(Image.ALIGN_CENTER);
        return image;
    }

    private PdfPTable addTable(Ticket ticket) throws DocumentException {
        var table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addRow(table, "Price:", ticket.getPrice().toString());
        addRow(table, "Date:", ticket.getEvent().getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        addRow(table, "Event:", ticket.getEvent().getName());
        addRow(table, "Address:", ticket.getEvent().getBuilding() != null ? getAddress(ticket.getEvent().getBuilding().getAddress()) : EMPTY_VALUE);
        addRow(table, "Description:", ticket.getEvent().getDescription());
        addRow(table, "Sector name:", ticket.getSector() != null ? ticket.getSector().getName() : EMPTY_VALUE);
        addRow(table, "Row number:", ticket.getRow() != null ? ticket.getRow().getRowNumber().toString() : EMPTY_VALUE);
        addRow(table, "Seat number:", ticket.getSeat() != null ? ticket.getSeat().getId().toString() : EMPTY_VALUE);
        addRow(table, "Standing area:", ticket.getStandingArea() ? "Yes" : "No");
        addRow(table, "Owner:", ticket.getOwner() != null ? ticket.getOwner().getFirstName() + " " + ticket.getOwner().getLastName() : EMPTY_VALUE);

        return table;
    }

    private String getAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getStreet()).append(" ")
                .append(address.getBuildingNumber()).append(", ")
                .append(address.getCity()).append(", ")
                .append(address.getCountry());
        return sb.toString();
    }

    private void addRow(PdfPTable table, String label, String value) {
        var labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        labelCell.setFixedHeight(30f);
        labelCell.setBorder(Rectangle.NO_BORDER);

        var valueCell = new PdfPCell(new Phrase(value));
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valueCell.setFixedHeight(30f);
        valueCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private Image addQRCode(Long ticketID) throws IOException, WriterException, BadElementException {
        var qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode("Ticket ID: " + ticketID, BarcodeFormat.QR_CODE, 100, 100);
        var out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

        var image = Image.getInstance(out.toByteArray());
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleAbsolute(100, 100);
        return image;
    }
}