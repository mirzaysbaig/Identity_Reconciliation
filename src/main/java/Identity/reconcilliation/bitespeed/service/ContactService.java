package Identity.reconcilliation.bitespeed.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Identity.reconcilliation.bitespeed.dto.ContactDTO;
import Identity.reconcilliation.bitespeed.dto.IdentifyResponse;
import Identity.reconcilliation.bitespeed.model.Contact;
import Identity.reconcilliation.bitespeed.model.LinkPrecedence;
import Identity.reconcilliation.bitespeed.repository.ContactRepository;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;
    
   // first identifying user  based on email or phone number 
    public IdentifyResponse identify(String email, String phoneNumber){
       // s1: collecting all the contacts matching either mail or phonenumber
       //set to avoid duplicates 
       Set<Contact> allMatched=new HashSet<>();

       // either email or phonemuber not null 
       if (email != null) {
        allMatched.addAll(contactRepository.findByEmail(email));
        }

        if (phoneNumber != null) {
            allMatched.addAll(contactRepository.findByPhoneNumber(phoneNumber));
        }
        

        // Case A:
        // if no matches found this is a new user create and return 
        if (allMatched.isEmpty()) {
            return createAndReturnNewPrimary(email, phoneNumber);
        }   
        
        // Case B
        // finding the earlier primary contact 
        // if twoprimary are there create one as primary and other as secondary 
        Contact primary = resolvePrimaryContact(allMatched);

        // now updating all the other contacts to secondary if needed and linking the to primary  
        boolean linkedPrimary=linkAllToPrimary(allMatched, primary);
         
        // Case C
        // inserted new secondary contact if the provide comibination of email and phone is unique 
        // when either data is not null 
        if(linkedPrimary==false)  // if primary is not linked then we need secondary update here as already linked primary updates the prefrence
        insertNewSecondaryIfNeeded(email, phoneNumber, allMatched, primary);
        
        // final step
        // now for response body colleting all the related contacts from db
        List<Contact> relatedContacts = getAllRelatedContacts(primary);

        // building and return final response with that primary id and havinfg all related emails , phonennumbe and secondry ids
        return buildResponseFromContacts(primary, relatedContacts);

    }

    // Case A
    // creating new primary contact if no matching contacts exist 
    private IdentifyResponse createAndReturnNewPrimary(String email, String phoneNumber) {
        Contact newContact = new Contact();
        newContact.setEmail(email);
        newContact.setPhoneNumber(phoneNumber);
        newContact.setLinkPrecedence(LinkPrecedence.primary);
        newContact.setCreatedAt(LocalDateTime.now());
        newContact.setUpdatedAt(LocalDateTime.now());

        contactRepository.save(newContact);

        return buildResponseFromContacts(newContact, List.of(newContact));
    }  
    
     // Selects the oldest contact among the primary contacts for hirerachy
     
    
    private Contact resolvePrimaryContact(Set<Contact> contacts) {
        // Filter out primary contacts
        Set<Contact> primaries = contacts.stream()
            .filter(c -> c.getLinkPrecedence() == LinkPrecedence.primary)
            .collect(Collectors.toSet());

        // Picking  the one with the earliest createdAt timestamp
        return primaries.stream()
            .min(Comparator.comparing(Contact::getCreatedAt))
            .orElseThrow();
    }

    
    // Case B- converting primary into secondary 
     // If multiple primaries exist one by email and other by phone number change other to secondary and link it to primary  
    private boolean linkAllToPrimary(Set<Contact> contacts, Contact primary) {
        boolean linkedPrimary=false;
        for (Contact contact : contacts) {
            if (!contact.getId().equals(primary.getId()) && 
                contact.getLinkPrecedence() == LinkPrecedence.primary) {
                contact.setLinkPrecedence(LinkPrecedence.secondary); // setting other to secondary 
                contact.setLinkedId(primary.getId());
                contact.setUpdatedAt(LocalDateTime.now());
                contactRepository.save(contact);
                linkedPrimary=true;

            }
        }
        return linkedPrimary;
    }

    // case C: creating secondary 
     //If a unique combination of email/phone is provided that doesn’t match exactly any existing,
     //insert a new secondary contact and link it to the primary.
     
    private void insertNewSecondaryIfNeeded(String email, String phoneNumber, Set<Contact> allMatched, Contact primary) {
       // for new combination if both togt=ether donot exisit 
        boolean alreadyExists = allMatched.stream()
                .anyMatch(c -> Objects.equals(c.getEmail(), email)
                        && Objects.equals(c.getPhoneNumber(), phoneNumber));
        
        // for handling any one null and other exist 
        boolean phoneNumberExist=allMatched.stream()
                .anyMatch(c->Objects.equals(c.getPhoneNumber(), phoneNumber));
        boolean emailExist=allMatched.stream()
                .anyMatch(c->Objects.equals(c.getEmail(),email));
        
        if((email == null && phoneNumberExist) || (emailExist && phoneNumber==null)) return;

        if (!alreadyExists && (email != null || phoneNumber != null)) {
            Contact newSecondary = new Contact();
            newSecondary.setEmail(email);
            newSecondary.setPhoneNumber(phoneNumber);
            newSecondary.setLinkPrecedence(LinkPrecedence.secondary);
            newSecondary.setLinkedId(primary.getId());
            newSecondary.setCreatedAt(LocalDateTime.now());
            newSecondary.setUpdatedAt(LocalDateTime.now());

            contactRepository.save(newSecondary);
        }
    }

    
    // Fetching  all the  related contacts linked to the primary contact, including the primary itself.
     
    private List<Contact> getAllRelatedContacts(Contact primary) {
        List<Contact> relatedContacts = contactRepository.findByLinkedId(primary.getId());
        relatedContacts.add(0,primary); // adding current primary too in begning 
        return relatedContacts;
    }

    
     // Build IdentifyResponse DTO using contacts' data.
     // for respone body
    private IdentifyResponse buildResponseFromContacts(Contact primary, List<Contact> contacts) {
        // for unque emails 
        Set<String> emails = contacts.stream()
                .map(Contact::getEmail)
                .filter(Objects::nonNull) // for removing null 
                .collect(Collectors.toCollection(LinkedHashSet::new)); 
        // for all linked unique phone numbers 
        Set<String> phoneNumbers = contacts.stream()
                .map(Contact::getPhoneNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        // for secondary ids 
        List<Integer> secondaryIds = contacts.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.secondary)
                .map(Contact::getId)
                .collect(Collectors.toList());
        
       // creating contact dto according to the response needed
        ContactDTO dto = new ContactDTO();
        dto.setPrimaryContatctId(primary.getId());
        dto.setEmails(new ArrayList<>(emails));
        dto.setPhoneNumbers(new ArrayList<>(phoneNumbers));
        dto.setSecondaryContactIds(secondaryIds);

       // sending response 
        IdentifyResponse response = new IdentifyResponse();
        response.setContact(dto);
        return response;

    }
    
    

}
