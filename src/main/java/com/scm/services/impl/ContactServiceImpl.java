package com.scm.services.impl;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.helper.Helper;
import com.scm.helper.ResourceNotFoundException;
import com.scm.repositories.ContactRepo;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    private ImageService imageService;
    Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepo contactRepo;
    private final UserServices userServices;

    public ContactServiceImpl(ContactRepo contactRepo, UserServices userServices) {
        this.contactRepo = contactRepo;
        this.userServices = userServices;
    }

    @Override
    public Contact save(ContactForm contactForm, Authentication auth) {
        String id= UUID.randomUUID().toString();
        User user=userServices.getUserByEmail(Helper.getEmailOfLoggedInUser(auth));

        logger.info(contactForm.getContactImage().getOriginalFilename());
        //Image Processing code
        String filename=UUID.randomUUID().toString();
        String fileURL=imageService.uploadImage(contactForm.getContactImage(),filename);
        Contact con = new Contact();
        con.setId(id);
        con.setName(contactForm.getName());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setEmail(contactForm.getEmail());
        con.setFavorite(contactForm.isFavorite());
        con.setLinkdin(contactForm.getLinkdin());
        con.setSocialLinks(contactForm.getSocialLinks1());
        con.setPhoneNumber(contactForm.getPhone());
        con.setPicture(fileURL);
        con.setCloudinaryImagePublicId(filename);
        con.setUser(user);
        return contactRepo.save(con);
    }

    @Override
    public Contact update(ContactForm contactForm, String id) {
        var contact = contactRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact Not Found"));
        
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhone());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setSocialLinks(contactForm.getSocialLinks1());
        contact.setLinkdin(contactForm.getLinkdin());
        // Only process image IF a new file is uploaded
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()){
            String filename=UUID.randomUUID().toString();
            String fileURL=imageService.uploadImage(contactForm.getContactImage(),filename);
            // Overwrite only when a new image was chosen
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);

        }
        return contactRepo.save(contact);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contact not found with given id"));
    }

    @Override
    public List<Contact> getByUserId(String userId) {
        return contactRepo.findByUserId(userId);
    }

    @Override
    public void delete(String id) {
//        var con=contactRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contact not found with given id"));
//        contactRepo.delete(con);
        contactRepo.deleteById(id);
    }

    @Override
    public Page<Contact> findByUser(User user,int page, int size, String sortBy,String direction) {

        Sort sort=direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page,size,sort);


        return contactRepo.findByUser(user, pageable);
    }

    @Override
    public Page<Contact> searchByName(String name, int page, int size, String sortBy, String direction,User user) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        var pageable =PageRequest.of(page,size,sort);

        return contactRepo.findByUserAndNameContaining(user,name, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String email, int page, int size, String sortBy, String direction,User user) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        var pageable =PageRequest.of(page,size,sort);

        return contactRepo.findByUserAndEmailContaining(user,email, pageable);
    }

    @Override
    public Page<Contact> searchByPhone(String phone, int page, int size, String sortBy, String direction,User user) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        var pageable =PageRequest.of(page,size,sort);

        return contactRepo.findByUserAndPhoneNumberContaining(user,phone, pageable);
    }


    //new
    @Override
    public long countByUser(User user) {
        return contactRepo.countByUser(user);
    }

    @Override
    public long countFavoriteContactsByUser(User user) {
        return contactRepo.countByUserAndFavoriteTrue(user);
    }

    @Override
    public long countLinkedInContactsByUser(User user) {
        return contactRepo.countByUserAndLinkdinIsNotNullAndLinkdinNot(user, "");
    }

    @Override
    public List<Contact> getTop4FavoriteContacts(User user) {
        return contactRepo.findTop4ByUserAndFavoriteTrue(user);
    }

    @Override
    public List<Contact> getRecent5Contacts(User user) {
        return contactRepo.findTop5ByUserOrderByIdDesc(user);
    }
}
