package me.diegxherrera.estrafebackend.controller;

import com.google.zxing.WriterException;
import me.diegxherrera.estrafebackend.util.TicketPDFGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/tickets")
public class TicketExportController {

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportTicket() {
        try {
            // Hardcoded Ticket Details for Testing UI
            String trainName = "AVE 02191";
            String coach = "3";
            String seatNumber = "1C";
            String departureTime = "19:32";
            String departureStation = "Lugano Süd";
            String arrivalTime = "20:52";
            String arrivalStation = "Zurich-Puerta de Andorra";

            // Generate Ticket PDF
            byte[] pdfBytes = TicketPDFGenerator.generateTicketPdf(
                    trainName, "24234", coach, seatNumber, departureTime, departureStation, arrivalTime, arrivalStation, "22324"
            );

            // Configure Response Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "train_ticket.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}