package Identity.reconcilliation.bitespeed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Identity.reconcilliation.bitespeed.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer>{
    
    //Find all contacts matching the given email.
    
    List<Contact> findByEmail(String email);

    //Find all contacts matching the given phone number.
    
    List<Contact> findByPhoneNumber(String phoneNumber);

     //Find all contacts that are secondaries (linked) to a given primary contact.
    
    List<Contact> findByLinkedId(Integer linkedId);
}
