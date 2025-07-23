package org.java.milestone.ticket_platform.controller;

import org.java.milestone.ticket_platform.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public String index(Model model){
        model.addAttribute("tickets", ticketRepository.findAll());
        return "pages/index";
    }
}
