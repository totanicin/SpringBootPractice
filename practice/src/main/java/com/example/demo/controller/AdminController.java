package com.example.demo.controller;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Contact;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.InquiryRepository;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/admin/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin/signup";
    }

    @PostMapping("/admin/signup")
    public String registerAdmin(Admin admin, Model model) {
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            model.addAttribute("error", "メールアドレスは既に登録されています");
            return "admin/signup";
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return "redirect:/admin/signin";
    }

    @GetMapping("/admin/signin")
    public String showSigninForm() {
        return "admin/signin";
    }

    @PostMapping("/admin/signin")
    public String loginAdmin(@RequestParam String email, @RequestParam String password, Model model) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword())) {
                model.addAttribute("admin", admin);
                return "redirect:/admin/contacts";
            }
        }
        model.addAttribute("error", "Invalid email or password");
        return "admin/signin";
    }

    @GetMapping("/admin/contacts")
    public String showContacts(Model model) {
        model.addAttribute("contacts", inquiryRepository.findAll());
        return "admin/contacts";
    }

    @GetMapping("/admin/contacts/{id}")
    public String showContactDetails(@PathVariable Long id, Model model) {
        Optional<Contact> contactOpt = inquiryRepository.findById(id);
        if (contactOpt.isPresent()) {
            model.addAttribute("contact", contactOpt.get());
            return "admin/contactDetails";
        } else {
            return "redirect:/admin/contacts"; // IDが存在しない場合は一覧画面にリダイレクト
        }
    }

    @GetMapping("/admin/contacts/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Contact> contactOpt = inquiryRepository.findById(id);
        if (contactOpt.isPresent()) {
            model.addAttribute("contact", contactOpt.get());
            return "admin/contactEdit";
        } else {
            return "redirect:/admin/contacts"; // IDが存在しない場合は一覧画面にリダイレクト
        }
    }

    @PostMapping("/admin/contacts/{id}/edit")
    public String updateContact(@PathVariable Long id, Contact contactDetails, Model model) {
        Optional<Contact> contactOpt = inquiryRepository.findById(id);
        if (contactOpt.isPresent()) {
            Contact contact = contactOpt.get();
            contact.setLastName(contactDetails.getLastName());
            contact.setFirstName(contactDetails.getFirstName());
            contact.setEmail(contactDetails.getEmail());
            contact.setPhone(contactDetails.getPhone());
            contact.setZipCode(contactDetails.getZipCode());
            contact.setAddress(contactDetails.getAddress());
            contact.setBuildingName(contactDetails.getBuildingName());
            contact.setContactType(contactDetails.getContactType());
            contact.setBody(contactDetails.getBody());
            contact.setUpdateAt(new Timestamp(System.currentTimeMillis()));
            inquiryRepository.save(contact);
            return "redirect:/admin/contacts";
        } else {
            model.addAttribute("error", "お問い合わせが見つかりませんでした");
            return "admin/contactEdit";
        }
    }

    @PostMapping("/admin/contacts/{id}/delete")
    public String deleteContact(@PathVariable Long id) {
        inquiryRepository.deleteById(id);
        return "redirect:/admin/contacts";
    }

    @RequestMapping(value = "/admin/logout", method = RequestMethod.GET)
    public String logout() {
        return "redirect:/admin/signin?logout";
    }
}
