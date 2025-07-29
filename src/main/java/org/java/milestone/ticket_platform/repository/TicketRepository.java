package org.java.milestone.ticket_platform.repository;

import java.util.List;

import org.java.milestone.ticket_platform.model.Ticket;
import org.java.milestone.ticket_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    public List<Ticket> findByTitleContainingIgnoreCase(String title);
    List<Ticket> findByUser(User user);
    List<Ticket> findByUserId(Integer userId);
    List<Ticket> findByTitleContainingIgnoreCaseAndUserId(String title, Integer userId);
}