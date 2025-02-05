package me.diegxherrera.estrafebackend.controller;

import jakarta.transaction.Transactional;
import me.diegxherrera.estrafebackend.model.*;
import me.diegxherrera.estrafebackend.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Ensures that data persists across test cases¡
@Transactional
public class TicketTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private UUID ticketId;

    @BeforeEach
    @Commit
    @Transactional // ✅ Ensures this method runs within a transaction
    public void setUp() {
        // ✅ Step 1: Clear Database
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
        coachRepository.deleteAll();
        trainRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // ✅ Step 2: Create & Save Train
        Train train = new Train("High-Speed", "AVE 1234");
        train = trainRepository.saveAndFlush(train);

        // ✅ Step 3: Create & Save Coach
        Coach coach = new Coach(train, 1);
        coach = coachRepository.saveAndFlush(coach);

        // ✅ Step 4: Create & Save Seat
        Seat seat = new Seat("12A", "Economy", coach);
        seat = seatRepository.saveAndFlush(seat);

        // ✅ Step 5: Create & Save Ticket
        Ticket ticket = new Ticket(seat, coach, train, "GibraltarUser123");
        ticket = ticketRepository.saveAndFlush(ticket);

        // ✅ Persist Changes
        entityManager.flush();
        entityManager.clear();

        // ✅ Store Ticket ID for Test
        ticketId = ticket.getId();
    }

    @AfterEach
    @Transactional // ✅ Ensures cleanup is properly executed
    public void tearDown() {
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
        coachRepository.deleteAll();
        trainRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testDownloadTicketPDF() throws Exception {
        mockMvc.perform(get("/api/tickets/" + ticketId + "/export")
                        .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}