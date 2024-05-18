package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.demo.form.ContactForm;
import com.example.demo.service.ContactService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/contact")
@SessionAttributes("contactForm")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public String contact(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contact";
    }

    @PostMapping
    public String contact(@Validated @ModelAttribute("contactForm") ContactForm contactForm, BindingResult errorResult, HttpServletRequest request) {
        if (errorResult.hasErrors()) {
            return "contact";
        }

        HttpSession session = request.getSession();
        session.setAttribute("contactForm", contactForm);
        return "redirect:/contact/confirm";
    }

    @GetMapping("/confirm")
    public String confirm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        ContactForm contactForm = (ContactForm) session.getAttribute("contactForm");
        if (contactForm == null) {
            return "redirect:/contact";
        }
        model.addAttribute("contactForm", contactForm);
        return "confirmation";
    }

    @PostMapping("/register")
    public String register(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ContactForm contactForm = (ContactForm) session.getAttribute("contactForm");
        if (contactForm == null) {
            return "redirect:/contact";
        }

        contactService.saveContact(contactForm);
        return "redirect:/contact/complete";
    }

    @GetMapping("/complete")
    public String complete(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/contact";
        }

        ContactForm contactForm = (ContactForm) session.getAttribute("contactForm");
        if (contactForm == null) {
            return "redirect:/contact";
        }
        model.addAttribute("contactForm", contactForm);
        session.invalidate();
        return "completion";
    }
}
