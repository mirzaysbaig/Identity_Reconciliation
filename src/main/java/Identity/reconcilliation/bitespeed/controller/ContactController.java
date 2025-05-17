package Identity.reconcilliation.bitespeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Identity.reconcilliation.bitespeed.dto.IdentifyRequest;
import Identity.reconcilliation.bitespeed.dto.IdentifyResponse;
import Identity.reconcilliation.bitespeed.service.ContactService;

@RestController
@RequestMapping("/identify")
public class ContactController {
    @Autowired
    private ContactService contactService;
   
 
   
}
