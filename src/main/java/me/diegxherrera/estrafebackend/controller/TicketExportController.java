package me.diegxherrera.estrafebackend.controller;

import me.diegxherrera.estrafebackend.util.TicketImageGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketExportController {

    @GetMapping(value = "/{ticketId}/export", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> exportTicket(@PathVariable("ticketId") UUID ticketId) {
        try {
            // Here, you can fetch the ticket details from the database using the UUID
            // Example: Ticket ticket = ticketService.findById(ticketId);

            // For now, let's assume we have hardcoded data for demonstration
            int coach = 4; // fetched from DB
            String seatNumber = "12A"; // fetched from DB
            String departureTime = "15:30"; // fetched from DB
            String departureStation = "Madrid"; // fetched from DB
            String arrivalStation = "Barcelona"; // fetched from DB

            // Generate the ticket image
            BufferedImage ticketImage = TicketImageGenerator.generateTicketImage(
                    coach, seatNumber, departureTime, departureStation, arrivalStation);

            // Convert the image to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(ticketImage, "png", baos);

            // Return the image as response
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}