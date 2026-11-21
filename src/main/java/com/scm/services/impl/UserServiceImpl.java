package com.scm.services.impl;

import com.scm.entities.User;
import com.scm.helper.Helper;
import com.scm.helper.ResourceNotFoundException;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserServices {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public User saveUser(User user) {
        //user id generate
        String userId= UUID.randomUUID().toString();
        user.setUserId(userId);
        user.setProfilePicture("https://static.vecteezy.com/system/resources/previews/036/280/651/original/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //email token
        String emailToken=UUID.randomUUID().toString();
        String emailLink = Helper.getLinkForEmailVerification(emailToken);
        user.setEmailToken(emailToken);
        User savedUser = userRepo.save(user);
        emailService.sendEmail(savedUser.getEmail(),
                "Verify your email address – Smart Contact Manager",
                "Hi "+savedUser.getName()+",\n" +
                        "\n" +
                        "Thank you for registering with Smart Contact Manager! \n" +
                        "\n" +
                        "Please click the link below to verify your email address and activate your account:\n" +
                        emailLink+"\n" +
                        "\n" +
                        "This link will expire in 24 hours.\n" +
                        "\n" +
                        "If you did not create an account, please ignore this email.\n" +
                        "\n" +
                        "Best regards,\n" +
                        "Smart Contact Manager Team");
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
       User user1 = userRepo.findById(user.getUserId()).orElseThrow(()-> new ResourceNotFoundException("User Not Found"));
       user1.setName(user.getName());
       user1.setEmail(user.getEmail());
       user1.setPassword(passwordEncoder.encode(user.getPassword()));
       user1.setPhoneNumber(user.getPhoneNumber());
       user1.setProfilePicture(user.getProfilePicture());
       user1.setAbout(user.getAbout());
       user1.setEnabled(user.isEnabled());
       user1.setEmailVerified(user.isEmailVerified());
       user1.setPhoneVerified(user.isPhoneVerified());
       user1.setProvider(user.getProvider());
       user1.setProviderId(user.getProviderId());

       User save=userRepo.save(user1);

       return Optional.ofNullable(save);
    }

    @Override
    public void deleteUser(User user) {
        User user1 = userRepo.findById(user.getUserId()).orElseThrow(()-> new ResourceNotFoundException("User Not Found"));
        userRepo.delete(user1);
    }

    @Override
    public boolean isUserExist(String id) {
        User user1 = userRepo.findById(id).orElse(null);

        return user1 != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user1 = userRepo.findByEmail(email).orElse(null);

        return user1 != null ?true:false;
    }

    @Override
    public List<User> getAllUsers() {

        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        return userRepo.findByEmailToken(token);
    }

    @Override
    public User directSave(User user) {
        return userRepo.save(user);
    }
}
