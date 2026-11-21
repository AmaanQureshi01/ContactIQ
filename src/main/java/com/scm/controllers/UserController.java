package com.scm.controllers;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.FeedbackForm;
import com.scm.helper.Helper;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.repositories.ContactRepo;
import com.scm.services.ContactService;
import com.scm.services.FeedbackService;
import com.scm.services.ImageService;
import com.scm.services.UserServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(value="/user")
public class UserController {

    @Autowired
    private ContactService contactService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private UserServices userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/dashboard")
    public String userDashboard(Model model,Authentication authentication) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        //1.Summary Counts
        long totalContacts = contactService.countByUser(user);
        long favoriteContactsCount = contactService.countFavoriteContactsByUser(user);
        long socialConnectionsCounts = contactService.countLinkedInContactsByUser(user);

        //2. Favorite & Recent Contacts Lists
        List<Contact> favoriteContacts = contactService.getTop4FavoriteContacts(user);
        List<Contact> recentContacts = contactService.getRecent5Contacts(user);

        //3.Calculate Profile Completeness(%)
        int profileScore=40; //Base Score for Name + Email
        if(user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) profileScore += 20;
        if(user.getProfilePicture() != null && !user.getProfilePicture().isBlank()) profileScore+=20;
        if (user.getAbout() != null && !user.getAbout().isBlank()) profileScore += 20;
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favoriteContactsCount", favoriteContactsCount);
        model.addAttribute("socialConnectionsCount", socialConnectionsCounts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        model.addAttribute("recentContacts", recentContacts);
        model.addAttribute("profileScore", profileScore);
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(
            @RequestParam(name = "edit", defaultValue = "false") boolean editMode,
            Model model,
            Authentication authentication) {

        String email = Helper.getEmailOfLoggedInUser(authentication); // or authentication.getName()
        User user = userService.getUserByEmail(email);

        model.addAttribute("user", user);
        model.addAttribute("editMode", editMode);
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("user") User formUser,
            @RequestParam("profileImageFile") MultipartFile profileImageFile,
            @RequestParam(value = "oldPassword", required = false) String oldPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            Authentication authentication,
            HttpSession session) {

        String currentEmail = Helper.getEmailOfLoggedInUser(authentication);
        User existingUser = userService.getUserByEmail(currentEmail);

        boolean hasOldPassword = oldPassword != null && !oldPassword.isBlank();
        boolean hasNewPassword = newPassword != null && !newPassword.isBlank();

        // Check if the user attempted to change their password
        if (hasOldPassword || hasNewPassword) {
            if (!hasOldPassword || !hasNewPassword) {
                Message message = Message.builder().content("Please enter both your current password and a new password.").type(MessageType.red) .build();
                session.setAttribute("message",message);
                return "redirect:/user/profile?edit=true";
            }

            if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
                Message message = Message.builder().content("Current password is incorrect.").type(MessageType.red) .build();
                session.setAttribute("message",message);
                return "redirect:/user/profile?edit=true";
            }

            if (passwordEncoder.matches(newPassword, existingUser.getPassword())) {
                Message message = Message.builder().content("New password must be different from your current password.").type(MessageType.red) .build();
                session.setAttribute("message",message);
                return "redirect:/user/profile?edit=true";
            }

            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }

        // Update only the safe, editable fields
        existingUser.setName(formUser.getName());
        //existingUser.setEmail(formUser.getEmail());
        existingUser.setPhoneNumber(formUser.getPhoneNumber());
        existingUser.setAbout(formUser.getAbout());

        // Upload new picture only if a file was selected
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            // Generate a unique filename (or use existingUser.getUserId() to overwrite the old avatar)
            String filename = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(profileImageFile, filename);

            if (imageUrl != null) {
                existingUser.setProfilePicture(imageUrl);
            }
        }

        userService.directSave(existingUser);
        return "redirect:/user/profile";
    }


    // 1. Show Feedback Page + Previous Submissions
    @GetMapping("/feedback")
    public String showFeedbackPage(Model model, Authentication authentication) {
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        model.addAttribute("feedbackForm", new FeedbackForm());
        model.addAttribute("previousFeedbacks", feedbackService.getUserFeedbacks(user));

        return "user/feedback";
    }

    // 2. Process Feedback Submission
    @PostMapping("/feedback")
    public String processFeedback(
            @Valid @ModelAttribute("feedbackForm") FeedbackForm feedbackForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        if (bindingResult.hasErrors()) {
            model.addAttribute("previousFeedbacks", feedbackService.getUserFeedbacks(user));
            return "user/feedback";
        }

        feedbackService.saveFeedback(feedbackForm, user);

        redirectAttributes.addFlashAttribute("feedbackSuccess", "Thank you! Your feedback has been submitted.");
        return "redirect:/user/feedback";
    }

    // 3. Delete User Feedback
    @PostMapping("/feedback/delete/{id}")
    public String deleteFeedback(
            @PathVariable("id") String feedbackId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        feedbackService.deleteFeedback(feedbackId, user);

        redirectAttributes.addFlashAttribute("feedbackSuccess", "Feedback deleted successfully.");
        return "redirect:/user/feedback";
    }
}
