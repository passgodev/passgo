package pl.uj.passgo.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import pl.uj.passgo.models.Ticket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PDFGenerator {

    public byte[] generateTicketPdf(Ticket ticket) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);

        try (Document document = new Document(pdf)) {
            document.add(addImage());

            Paragraph ticketIdParagraph = new Paragraph("Ticket ID: " + ticket.getId()).setBold();
            Image qrImage = addQRCode(ticket.getId());

            Table idTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            idTable.addCell(new Cell().add(ticketIdParagraph).setBorder(Border.NO_BORDER));
            idTable.addCell(new Cell().add(qrImage).setBorder(Border.NO_BORDER));

            document.add(idTable);

            document.add(addTable(ticket));
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private Image addImage() throws IOException {
        Image img = new Image(ImageDataFactory.create(new ClassPathResource("passgo.png").getURL()));
        img.setWidth(200);
        return img;
    }

    private Table addTable(Ticket ticket) {
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addCell("Price:");
        table.addCell(ticket.getPrice().toString());

        table.addCell("Event:");
        table.addCell(ticket.getEvent().getName());

        table.addCell("Date:");
        table.addCell(ticket.getEvent().getDate().toString());

        table.addCell("Description:");
        table.addCell(ticket.getEvent().getDescription());

        table.addCell("Sector name:");
        table.addCell(ticket.getSector() != null ? ticket.getSector().getName() : "---");

        table.addCell("Row number:");
        table.addCell(ticket.getRow() != null ? ticket.getRow().getRowNumber().toString() : "---");

        table.addCell("Seat number:");
        table.addCell(ticket.getSeat() != null ? ticket.getSeat().getId().toString() : "---");

        table.addCell("Standing area:");
        table.addCell(ticket.getStandingArea() ? "Yes" : "No");

        table.addCell("Owner:");
        table.addCell(ticket.getOwner() != null ? ticket.getOwner().getFirstName() + " " + ticket.getOwner().getLastName() : "---");

        return table;
    }

    private Image addQRCode(Long ticketID) throws IOException, WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode("Ticket ID: " + ticketID, BarcodeFormat.QR_CODE, 100, 100);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
        return new Image(ImageDataFactory.create(out.toByteArray()));
    }


}