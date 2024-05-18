package com.example.demo.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Contact;
import com.example.demo.form.ContactForm;
import com.example.demo.repository.InquiryRepository;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Override
    public void saveContact(ContactForm contactForm) {
        Contact contact = new Contact();
        contact.setLastName(contactForm.getLastName());
        contact.setFirstName(contactForm.getFirstName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhone(contactForm.getPhone());
        contact.setZipCode(contactForm.getZipCode());
        contact.setAddress(contactForm.getAddress());
        contact.setBuildingName(contactForm.getBuildingName());
        contact.setContactType(contactForm.getContactType());
        contact.setBody(contactForm.getBody());
        contact.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        contact.setUpdateAt(new Timestamp(System.currentTimeMillis()));

        System.out.println("Saving contact to the database...");
        inquiryRepository.save(contact);
    }
}
