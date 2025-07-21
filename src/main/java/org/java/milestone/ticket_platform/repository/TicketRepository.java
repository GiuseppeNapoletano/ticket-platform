package org.java.milestone.ticket_platform.repository;

import org.java.milestone.ticket_platform.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer>{

    
}