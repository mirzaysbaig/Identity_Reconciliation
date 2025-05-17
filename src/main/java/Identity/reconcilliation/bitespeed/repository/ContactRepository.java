package Identity.reconcilliation.bitespeed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Identity.reconcilliation.bitespeed.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer>{
  
}
