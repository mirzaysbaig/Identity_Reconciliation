package Identity.reconcilliation.bitespeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Identity.reconcilliation.bitespeed.service.ContactService;

@RestController
@RequestMapping("/identify")
public class ContactController {
    @Autowired
    private ContactService contactService;

   
}
