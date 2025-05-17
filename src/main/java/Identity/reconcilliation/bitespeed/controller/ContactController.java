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
   
    // use of post endpoint as we need to update too and send accordingly 
    // accepts email and phone number in the request 
    @PostMapping
    public ResponseEntity<IdentifyResponse> identifyContact(@RequestBody IdentifyRequest request) {

        // Validate if both are null, return bad request and then we can through certain exception 
        if (request.getEmail() == null && request.getPhoneNumber() == null) {
            return ResponseEntity.badRequest().build();
        }

        // to fetch response from backend logic implemented in service layer 
        IdentifyResponse response = contactService.identify(request.getEmail(), request.getPhoneNumber());

        return ResponseEntity.ok(response);
    }  
 
   
}
