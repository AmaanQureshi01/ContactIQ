package com.scm.services;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ContactService {

    //Save
    Contact save(ContactForm contactForm, Authentication auth);

    //Update
    Contact update(ContactForm contactForm,String id);

    //Get All Contact
    List<Contact> getAll();

    //Get Contact By Id
    Contact getById(String id);

    //Get All Contact of a User from userId
    List<Contact> getByUserId(String userId);

    //Delete Contact
    void delete(String id);

    //find by user object
    Page<Contact> findByUser(User user, int page, int size, String sortBy,String direction);

    //Search Contact
    Page<Contact> searchByName(String name, int page, int size, String sortBy,String direction,User user);

    Page<Contact> searchByEmail(String email, int page, int size, String sortBy,String direction,User user);

    Page<Contact> searchByPhone(String phone, int page, int size, String sortBy,String direction,User user);

    long countByUser(User user);

    long countFavoriteContactsByUser(User user);

    long countLinkedInContactsByUser(User user);

    List<Contact> getTop4FavoriteContacts(User user);

    List<Contact> getRecent5Contacts(User user);
}
