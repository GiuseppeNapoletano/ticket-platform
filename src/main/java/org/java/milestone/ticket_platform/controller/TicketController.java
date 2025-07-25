package org.java.milestone.ticket_platform.controller;

import java.util.List;

import org.java.milestone.ticket_platform.model.Ticket;
import org.java.milestone.ticket_platform.model.User.UserStatus;
import org.java.milestone.ticket_platform.repository.CategoryRepository;
import org.java.milestone.ticket_platform.repository.TicketRepository;
import org.java.milestone.ticket_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;
    
    @Autowired
    private TicketRepository ticketRepository;

    TicketController(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String index(Authentication authentication, Model model, @RequestParam( name = "keyword", required = false) String keyword){
        List<Ticket> tickets;
        if ( keyword != null && !keyword.isEmpty()){
            tickets = ticketRepository.findByTitleContainingIgnoreCase(keyword);
        } else {
            tickets = ticketRepository.findAll();
        }
        model.addAttribute("tickets", tickets);
        return "tickets/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket non trovato con id: " + id));
        model.addAttribute("ticket", ticket);
        return "tickets/show"; 
    }

    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("users", userRepository.findByRolesNameAndStatus("OPERATOR", UserStatus.Active));
        model.addAttribute("categories", categoryRepository.findAll());
        return "tickets/create";
    }

    @PostMapping
    public String store( @Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "tickets/create";
        }
        ticketRepository.save(formTicket);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model){
        model.addAttribute("ticket", ticketRepository.findById(id).get());
        model.addAttribute("users", userRepository.findByRolesNameAndStatus("OPERATOR", UserStatus.Active));
        model.addAttribute("categories", categoryRepository.findAll());
        return "tickets/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return "tickets/edit";
        }

        ticketRepository.save(formTicket);
        return "redirect:/tickets";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id, Model model){

        Ticket ticketToDelete = ticketRepository.findById(id).get();
        ticketRepository.delete(ticketToDelete);
         return "redirect:/tickets";
    }
}
