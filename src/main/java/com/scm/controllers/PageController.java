package com.scm.controllers;

import ch.qos.logback.core.joran.spi.HttpUtil;
import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.services.EmailService;
import com.scm.services.UserServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller 
public class PageController {
    @Autowired
    private UserServices userServices;
    @Autowired
    private EmailService emailService;
@Value("${MAIL_TO}")
    private String to;
    @GetMapping({"/","/home"})
    public String home(Model model) {
        model.addAttribute("activePage", "home");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("activePage", "about");
        return "about";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("activePage", "services");
        return "services";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("activePage", "contact");
        return "contact";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("activePage", "login");
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        UserForm userForm = new UserForm();
        //userForm.setName("Amaan");
        //userForm.setEmail("Amaan@gmail.com");
        model.addAttribute("userForm", userForm);
        //model.addAttribute("activePage", "signup");
        return "signup";
    }

    @PostMapping("/do-signup")
    public String processSignup(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, HttpSession session){
       System.out.println(userForm);

       if(bindingResult.hasErrors()){
           return "/signup";
       }

       User user = new User();
       user.setName(userForm.getName());
       user.setEmail(userForm.getEmail());
       user.setAbout(userForm.getAbout());
       user.setPassword(userForm.getPassword());
       user.setPhoneNumber(userForm.getPhoneNumber());

       //new
        user.setEnabled(false);


       User savedUser = userServices.saveUser(user);


       System.out.println();
       System.out.println("User Saved Successfully.");
       Message message= Message.builder().content("Registration successful! Please check your inbox and verify your email address before logging in.").type(MessageType.green).build();
       session.setAttribute("message", message);
       return "redirect:/signup";
    }

    @PostMapping("/contact")
    public String handleContactForm(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("subject") String subject,
            @RequestParam("message") String message,
            RedirectAttributes redirectAttributes) {

        String emailSubject = "[ContactIQ Inquiry] " + subject + " - from " + name;

        String emailBody = "You have received a new contact message from ContactIQ:\n\n"
                + "----------------------------------------\n"
                + "Sender Name  : " + name + "\n"
                + "Sender Email : " + email + "\n"
                + "Subject      : " + subject + "\n"
                + "----------------------------------------\n\n"
                + "Message:\n"
                + message;

        // Sends the email to your configured spring.mail.username address
        emailService.sendEmail(to, emailSubject, emailBody);

        redirectAttributes.addFlashAttribute("contactSuccess",
                "Thanks " + name + "! Your message has been sent.");
        return "redirect:/contact";
    }
}
