package com.scm.repositories;

import com.scm.entities.Contact;
import com.scm.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepo extends JpaRepository<Contact, String> {
    //find the contact by user
    Page<Contact> findByUser(User user, Pageable pageable);

    //find the contacts by user id
    @Query("SELECT c FROM Contact c WHERE c.user.userId=:userId")
    List<Contact> findByUserId(@Param("userId") String userId);

    Page<Contact> findByUserAndNameContaining( User user,String name, Pageable pageable);

    Page<Contact> findByUserAndEmailContaining(User user,String email, Pageable pageable);

    Page<Contact> findByUserAndPhoneNumberContaining(User user, String phoneNumber, Pageable pageable);

    // Add these methods inside ContactRepo.java
    long countByUser(User user);

    long countByUserAndFavoriteTrue(User user);

    long countByUserAndLinkdinIsNotNullAndLinkdinNot(User user, String empty);

    List<Contact> findTop4ByUserAndFavoriteTrue(User user);

    List<Contact> findTop5ByUserOrderByIdDesc(User user);
}
