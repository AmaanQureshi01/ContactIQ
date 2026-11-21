package com.scm.controllers;

import com.scm.entities.User;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.services.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class EmailController {
    private UserServices userServices;

    public EmailController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token")String token, HttpSession session){

        User user = userServices.getUserByToken(token).orElse(null);
        if(user != null){
            user.setEmailVerified(true);
            user.setEnabled(true);
            userServices.directSave(user);
            Message message = Message.builder().content("Email Verified Successfully! Now you can login.").type(MessageType.green).build();
            session.setAttribute("message",message);
            return "login";
        }
        else{
            Message message = Message.builder().content("Email Not Verified! Try Again.").type(MessageType.red).build();
            session.setAttribute("message",message);
            return "login";
        }
    }
}
