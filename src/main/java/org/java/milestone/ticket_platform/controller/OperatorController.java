package org.java.milestone.ticket_platform.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.java.milestone.ticket_platform.model.Note;
import org.java.milestone.ticket_platform.model.Ticket;
import org.java.milestone.ticket_platform.model.User;
import org.java.milestone.ticket_platform.repository.NoteRepository;
import org.java.milestone.ticket_platform.repository.TicketRepository;
import org.java.milestone.ticket_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/operator")
public class OperatorController {

    private final UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private NoteRepository noteRepository;

    OperatorController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/tickets")
    public String index(Authentication authentication, Model model,
            @RequestParam(name = "keyword", required = false) String keyword) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));
        List<Ticket> tickets;
        if (keyword != null && !keyword.isEmpty()) {
            tickets = ticketRepository.findByTitleContainingIgnoreCaseAndUserId(keyword, user.getId());
        } else {
            tickets = ticketRepository.findByUserId(user.getId());
        }
        model.addAttribute("tickets", tickets);
        return "operator/index";
    }

    @GetMapping("tickets/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket non trovato con id: " + id));
        model.addAttribute("ticket", ticket);
        return "operator/show";
    }

    @PostMapping("tickets/{id}")
    public String updateStatus(@Valid @PathVariable Integer id, @RequestParam("status") Ticket.TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket non trovato"));
        ticket.setStatus(status);
        ticketRepository.save(ticket);
        return "redirect:/operator/tickets/" + id;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "operator/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "operator/edit";
    }

    @PostMapping("/profile")
    public String update(@Valid @ModelAttribute("user") User formUser, BindingResult bindingResult,
            Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            return "operator/edit";
        }

        User user = userRepository.findById(formUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        if (user.getStatus() == User.UserStatus.Active && formUser.getStatus() == User.UserStatus.Non_active) {
            boolean hasOpenTickets = user.getTickets().stream()
                    .anyMatch(t -> t.getStatus() == Ticket.TicketStatus.to_do
                            || t.getStatus() == Ticket.TicketStatus.in_progress);

            if (hasOpenTickets) {
                model.addAttribute("user", formUser);
                model.addAttribute("ticketError",
                        "Non puoi passare in 'non attivo' finché hai ticket da fare o in corso.");
                return "operator/edit";
            }
        }

        user.setUsername(formUser.getUsername());
        user.setEmail(formUser.getEmail());
        user.setStatus(formUser.getStatus());
        userRepository.save(user);

        return "redirect:/operator/profile";
    }

    @PostMapping("/notes/create")
    public String addNote(@RequestParam("ticketId") Integer ticketId,
            @RequestParam("content") String content,
            Authentication authentication) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Note note = new Note();
        note.setContent(content);
        note.setTicket(ticket);
        note.setAuthor(authentication.getName());
        note.setCreatedAt(LocalDateTime.now());
        noteRepository.save(note);
        return "redirect:/operator/tickets/" + ticketId;
    }
}