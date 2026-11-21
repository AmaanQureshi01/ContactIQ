package com.scm.controllers;

import com.scm.entities.User;
import com.scm.helper.Helper;
import com.scm.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class RootController {
    @Autowired
    private UserServices userServices;

    @ModelAttribute
    public void addLoggedInUserInfo(Model model, Authentication authentication){
        if(authentication == null){
            model.addAttribute("loggedInUser", null);
            return;
        }
        String name = Helper.getEmailOfLoggedInUser(authentication);
        if(name == null || name.equalsIgnoreCase("anonymousUser")){
            model.addAttribute("loggedInUser", null);
            return;
        }
        User user = userServices.getUserByEmail(name);
        model.addAttribute("loggedInUser", user);
    }
}
