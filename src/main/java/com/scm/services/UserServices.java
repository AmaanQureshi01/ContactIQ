package com.scm.services;

import com.scm.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserServices {

    User saveUser(User user);
    Optional<User> getUserById(String id);
    Optional<User> updateUser(User user);
    void deleteUser(User user);
    boolean isUserExist(String id);
    boolean isUserExistByEmail(String email);
    List<User> getAllUsers();
   User getUserByEmail(String email);
   Optional<User> getUserByToken(String token);
   User directSave(User user);
}
