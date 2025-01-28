package me.diegxherrera.estrafebackend.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TicketImageGenerator {

    public static BufferedImage generateTicketImage(
            int coach,
            String seatNumber,
            String departureTime,
            String departureStation,
            String arrivalStation) {

        int width = 600;
        int height = 300;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        // Set background color
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        // Draw border
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, width - 1, height - 1);

        // Set font
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font detailsFont = new Font("Arial", Font.PLAIN, 18);
        graphics.setFont(titleFont);

        // Draw Title
        graphics.drawString("Train Ticket", 220, 40);

        // Draw Details
        graphics.setFont(detailsFont);
        graphics.drawString("Coach: " + coach, 50, 100);
        graphics.drawString("Seat: " + seatNumber, 50, 140);
        graphics.drawString("Departure Time: " + departureTime, 50, 180);
        graphics.drawString("From: " + departureStation, 50, 220);
        graphics.drawString("To: " + arrivalStation, 50, 260);

        // Clean up
        graphics.dispose();

        return image;
    }
}