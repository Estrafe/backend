package me.diegxherrera.estrafebackend.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TicketPDFGenerator {

    public static byte[] generateTicketPdf(String trainName, String trainId, String coach, String seatNumber,
                                           String departureTime, String departureStation, String arrivalTime,
                                           String arrivalStation, String ticketId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A5); // Set page size

        // --- ADD LOGO ---
        try {
            String imagePath = "src/main/resources/static/estrafe.png"; // Update with correct path
            if (Files.exists(Paths.get(imagePath))) {
                ImageData imageData = ImageDataFactory.create(imagePath);
                Image logo = new Image(imageData).setWidth(100).setHeight(60);
                document.add(logo);
            }
        } catch (Exception e) {
            System.err.println("Logo not found: " + e.getMessage());
        }

        // --- CREATE TABLE (Two Columns) ---
        float[] columnWidths = {250f, 250f, 250f}; // Two equal columns
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100)); // ✅ Fixed width setting
        table.setBackgroundColor(ColorConstants.LIGHT_GRAY);

        // Left Column (Ticket Info)
        table.addCell(createCell("Train:", true));
        table.addCell(createCell("Link Express", false));
        table.addCell(createCell(trainName, false));

        table.addCell(createCell("Coach:", true));
        table.addCell(createCell("", false));
        table.addCell(createCell(coach, false));

        table.addCell(createCell("Seat:", true));
        table.addCell(createCell("", false));
        table.addCell(createCell(seatNumber, false));

        table.addCell(createCell("Departure:", true));
        table.addCell(createCell(departureTime, false));
        table.addCell(createCell(departureStation, false));

        table.addCell(createCell("Arrival:", true));
        table.addCell(createCell(arrivalTime, false));
        table.addCell(createCell(arrivalStation, false));

        // Right Column (More Info)
        table.addCell(createCell("Ticket ID:", true));
        table.addCell(createCell("", false));
        table.addCell(createCell(ticketId, false));

        document.add(table); // Add table to the document

        // --- CLOSE DOCUMENT ---
        document.close();
        return outputStream.toByteArray();
    }

    private static Cell createCell(String text, boolean isBold) {
        Cell cell = new Cell().add(new Paragraph(text));
        if (isBold) {
            cell.setBold();
            cell.setFontColor(ColorConstants.DARK_GRAY);
        }
        return cell;
    }
}