//package pl.uj.passgo.services;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//import com.itextpdf.io.image.ImageDataFactory;
//import com.itextpdf.layout.Document;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.borders.Border;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Image;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.properties.HorizontalAlignment;
//import com.itextpdf.layout.properties.TextAlignment;
//import com.itextpdf.layout.properties.UnitValue;
//import com.itextpdf.layout.properties.VerticalAlignment;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//import pl.uj.passgo.models.Address;
//import pl.uj.passgo.models.Ticket;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.time.format.DateTimeFormatter;
//
//@Service
//public class PDFGenerator {
//    private static final String EMPTY_VALUE = "---";
//
//    public byte[] generateTicketPdf(Ticket ticket) {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        PdfWriter writer = new PdfWriter(out);
//        PdfDocument pdf = new PdfDocument(writer);
//
//        try (Document document = new Document(pdf)) {
//            document.add(addImage());
//
//            Paragraph ticketIdParagraph = new Paragraph("Ticket ID: " + ticket.getId()).setTextAlignment(TextAlignment.CENTER).setBold();
//            Image qrImage = addQRCode(ticket.getId());
//            qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
//
//            Table idTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
//            idTable.addCell(new Cell().add(ticketIdParagraph)
//                    .setBorder(Border.NO_BORDER)
//                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
//
//            idTable.addCell(new Cell().add(qrImage)
//                    .setBorder(Border.NO_BORDER)
//                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
//
//            document.add(idTable);
//
//            document.add(addTable(ticket));
//        } catch (IOException | WriterException e) {
//            e.printStackTrace();
//        }
//        return out.toByteArray();
//    }
//
//    private Image addImage() throws IOException {
//        Image img = new Image(ImageDataFactory.create(new ClassPathResource("passgo.png").getURL()));
//        img.setWidth(200);
//        img.setHorizontalAlignment(HorizontalAlignment.CENTER);
//        return img;
//    }
//
//    private Table addTable(Ticket ticket) {
//        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
//
//        addRow(table, "Price:", ticket.getPrice().toString());
//        addRow(table, "Date:", ticket.getEvent().getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
//        addRow(table, "Event:", ticket.getEvent().getName());
//        addRow(table, "Address:", ticket.getEvent().getBuilding() != null ? getAddress(ticket.getEvent().getBuilding().getAddress()) : EMPTY_VALUE);
//        addRow(table, "Description:", ticket.getEvent().getDescription());
//        addRow(table, "Sector name:", ticket.getSector() != null ? ticket.getSector().getName() : EMPTY_VALUE);
//        addRow(table, "Row number:", ticket.getRow() != null ? ticket.getRow().getRowNumber().toString() : EMPTY_VALUE);
//        addRow(table, "Seat number:", ticket.getSeat() != null ? ticket.getSeat().getId().toString() : EMPTY_VALUE);
//        addRow(table, "Standing area:", ticket.getStandingArea() ? "Yes" : "No");
//        addRow(table, "Owner:", ticket.getOwner() != null ? ticket.getOwner().getFirstName() + " " + ticket.getOwner().getLastName() : EMPTY_VALUE);
//
//        return table;
//    }
//
//    private String getAddress(Address address) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(address.getStreet()).append(" ")
//                .append(address.getBuildingNumber()).append(", ")
//                .append(address.getCity()).append(", ")
//                .append(address.getCountry());
//        return sb.toString();
//    }
//
//    private void addRow(Table table, String label, String value) {
//        Cell labelCell = new Cell()
//                .add(new Paragraph(label).setBold())
//                .setTextAlignment(TextAlignment.CENTER)
//                .setVerticalAlignment(VerticalAlignment.MIDDLE)
//                .setHeight(30f);
//
//        Cell valueCell = new Cell()
//                .add(new Paragraph(value))
//                .setTextAlignment(TextAlignment.CENTER)
//                .setVerticalAlignment(VerticalAlignment.MIDDLE)
//                .setHeight(30f);
//
//        table.addCell(labelCell);
//        table.addCell(valueCell);
//    }
//
//    private Image addQRCode(Long ticketID) throws IOException, WriterException {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode("Ticket ID: " + ticketID, BarcodeFormat.QR_CODE, 100, 100);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
//        return new Image(ImageDataFactory.create(out.toByteArray()));
//    }
//
//
//}