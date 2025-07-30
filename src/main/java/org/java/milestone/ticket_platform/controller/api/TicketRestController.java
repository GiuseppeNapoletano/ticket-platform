package org.java.milestone.ticket_platform.controller.api;

import java.util.List;
import java.util.Optional;

import org.java.milestone.ticket_platform.model.Ticket;
import org.java.milestone.ticket_platform.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketRestController {
    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public ResponseEntity<List<Ticket>> index() {

        List<Ticket> tickets = ticketRepository.findAll();

        if (tickets.size() == 0) {
            return new ResponseEntity<List<Ticket>>(tickets, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Ticket>>(tickets, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> show(@PathVariable Integer id) {
        Optional<Ticket> ticketAttempt = ticketRepository.findById(id);

        if (ticketAttempt.isEmpty()) {
            return new ResponseEntity<Ticket>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Ticket>(ticketAttempt.get(), HttpStatus.OK);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<Ticket>> byCategory(@PathVariable Integer categoryId) {
        List<Ticket> tickets = ticketRepository.findByCategoryId(categoryId);

        if (tickets.isEmpty()) {
            return new ResponseEntity<>(tickets, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> byStatus(@PathVariable String status) {
        Ticket.TicketStatus statusEnum;

        try {
            statusEnum = Ticket.TicketStatus.valueOf(status.toLowerCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Ticket> tickets = ticketRepository.findByStatus(statusEnum);

        if (tickets.isEmpty()) {
            return new ResponseEntity<>(tickets, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
}
