package org.java.milestone.ticket_platform.controller;

import java.util.List;

import org.java.milestone.ticket_platform.model.Category;
import org.java.milestone.ticket_platform.model.Ticket;
import org.java.milestone.ticket_platform.model.User;
import org.java.milestone.ticket_platform.model.User.UserStatus;
import org.java.milestone.ticket_platform.repository.CategoryRepository;
import org.java.milestone.ticket_platform.repository.RoleRepository;
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
@RequestMapping("/admin")
public class AdminController {

    private final RoleRepository roleRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;
    
    @Autowired
    private TicketRepository ticketRepository;

    AdminController(UserRepository userRepository, CategoryRepository categoryRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/tickets")
    public String index(Authentication authentication, Model model, @RequestParam( name = "keyword", required = false) String keyword){
        List<Ticket> tickets;
        if ( keyword != null && !keyword.isEmpty()){
            tickets = ticketRepository.findByTitleContainingIgnoreCase(keyword);
        } else {
            tickets = ticketRepository.findAll();
        }
        model.addAttribute("tickets", tickets);
        return "admin/index";
    }

    @GetMapping("/tickets/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket non trovato con id: " + id));
        model.addAttribute("ticket", ticket);
        return "admin/show"; 
    }

    @GetMapping("/tickets/create")
    public String create(Model model){
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("users", userRepository.findByRolesNameAndStatus("OPERATOR", UserStatus.Active));
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/create";
    }

    @PostMapping("/tickets/create")
    public String store( @Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "admin/tickets/create";
        }
        ticketRepository.save(formTicket);
        return "redirect:/admin/tickets";
    }

    @GetMapping("/tickets/{id}/edit")
    public String edit(@PathVariable Integer id, Model model){
        model.addAttribute("ticket", ticketRepository.findById(id).get());
        model.addAttribute("users", userRepository.findByRolesNameAndStatus("OPERATOR", UserStatus.Active));
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/edit";
    }

    @PostMapping("/tickets/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return "admin/tickets/edit";
        }

        ticketRepository.save(formTicket);
        return "redirect:/admin/tickets";
    }

    @PostMapping("tickets/{id}/delete")
    public String delete(@PathVariable("id") Integer id, Model model){

        Ticket ticketToDelete = ticketRepository.findById(id).get();
        ticketRepository.delete(ticketToDelete);
         return "redirect:/admin/tickets";
    }

    @GetMapping("/addoperator")
    public String createOperator(Model model){
        model.addAttribute("users", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/add_operator";
    }

    @PostMapping("/addoperator")
    public String storeOperator( @Valid @ModelAttribute("users") User formUser, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "admin/addoperator";
        }
        formUser.setPassword("{noop}" + formUser.getPassword());
        userRepository.save(formUser);
        return "redirect:/admin/tickets";
    }

    @GetMapping("/categories")
    public String category(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/category";
    }

    @GetMapping("/categories/create")
    public String createCategory(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categories);
        return "admin/add_category";
    }

    @PostMapping("/categories")
    public String newCategory(@Valid @ModelAttribute("category") Category formCategory, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/add_category";
        }
        categoryRepository.save(formCategory);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable("id") Integer id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }
}
