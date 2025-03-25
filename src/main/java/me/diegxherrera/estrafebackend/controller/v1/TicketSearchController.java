package me.diegxherrera.estrafebackend.controller.v1;

import lombok.Getter;
import lombok.Setter;
import me.diegxherrera.estrafebackend.model.TrainSchedule;
import me.diegxherrera.estrafebackend.service.TrainScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets/search") // <-- Change this to avoid conflicts
public class TicketSearchController {

    private final TrainScheduleService trainScheduleService;

    @Autowired
    public TicketSearchController(TrainScheduleService trainScheduleService) {
        this.trainScheduleService = trainScheduleService;
    }

    // Wrapper response object for round-trip searches.
    @Setter
    @Getter
    public static class TicketSearchResponse {
        private List<TrainSchedule> outbound;
        private List<TrainSchedule> returnSchedules;

        public TicketSearchResponse(List<TrainSchedule> outbound, List<TrainSchedule> returnSchedules) {
            this.outbound = outbound;
            this.returnSchedules = returnSchedules;
        }

    }

    @GetMapping
    public ResponseEntity<?> searchTrains(
            @RequestParam UUID departureStationId,
            @RequestParam UUID arrivalStationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(defaultValue = "standard") String travelClass,
            @RequestParam(defaultValue = "1") int passengerCount
    ) {
        // Search for outbound schedules
        List<TrainSchedule> outboundSchedules = trainScheduleService.findSchedules(departureStationId, arrivalStationId, departureDate);

        // If a return date is provided, search for return schedules (swapping departure and arrival)
        if (returnDate != null) {
            List<TrainSchedule> returnSchedules = trainScheduleService.findSchedules(arrivalStationId, departureStationId, returnDate);
            TicketSearchResponse response = new TicketSearchResponse(outboundSchedules, returnSchedules);
            return ResponseEntity.ok(response);
        } else {
            // One-way search: simply return the outbound list.
            return ResponseEntity.ok(outboundSchedules);
        }
    }
}