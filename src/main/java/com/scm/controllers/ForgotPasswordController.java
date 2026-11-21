package com.scm.controllers;

import com.scm.entities.User;
import com.scm.helper.Helper;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class ForgotPasswordController {
    @Autowired
    private UserServices userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Show "Enter Email" page (Works from both Login and Edit Profile)
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Authentication authentication, Model model) {
        if (Helper.isAuthenticated(authentication)) {
            model.addAttribute("prefilledEmail", Helper.getEmailOfLoggedInUser(authentication));
        }
        return "forgot_password";
    }

    // 2. Generate Magic Link and send via your sendEmailWithHtml method
    @PostMapping("/forgot-password")
    public String sendMagicLink(
            @RequestParam("email") String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User user = userService.getUserByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "No account found with that email address.");
            return "redirect:/forgot-password";
        }

        if (user.getPassword() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "This account uses Google/GitHub login and has no local password.");
            return "redirect:/forgot-password";
        }

        // Plain single-use UUID token (no expiration timer)
        String token = UUID.randomUUID().toString();
        user.setEmailToken(token);
        userService.directSave(user);

        // Build base URL dynamically (e.g., http://localhost:8080)
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort());
        String magicLink = baseUrl + "/reset-password?token=" + token;

        String subject = "Reset Your Password - Smart Contact Manager";
        String htmlBody = Helper.buildResetEmailHtml(user.getName(), magicLink);

        try {
            emailService.sendEmailWithHtml(user.getEmail(), subject, htmlBody);
            redirectAttributes.addFlashAttribute("successMessage", "A password reset link has been sent to " + user.getEmail() + ".");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send email. Please try again later.");
        }

        return "redirect:/forgot-password";
    }

    // 3. Validate Magic Link when clicked in email
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        User user = userRepo.findByEmailToken(token).orElse(null);

        if (user == null) {
            model.addAttribute("errorMessage", "This password reset link is invalid or has already been used.");
            return "reset_password";
        }

        model.addAttribute("token", token);
        return "reset_password";
    }

    // 4. Save new password and invalidate the Magic Link
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = userRepo.findByEmailToken(token).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "This password reset link is invalid or has already been used.");
            return "redirect:/forgot-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password and confirm password do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setEmailToken(null); // Single-use: clear token after reset
        userService.directSave(user);

        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");

        if (Helper.isAuthenticated(authentication)) {
            return "redirect:/user/profile";
        }
        return "redirect:/login";
    }


}
