package com.scm.configurations;

import com.scm.entities.Provider;
import com.scm.entities.User;
import com.scm.repositories.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OAuthenticationSuccessHandler.class);
    private UserRepo userRepo;
    public OAuthenticationSuccessHandler(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("OAuthenticationSuccessHandler");
        //data ko database store

        //Identify the

        //1. identify provider
        var oAuth2AuthenticationToken =(OAuth2AuthenticationToken)authentication;
        String authProvider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        DefaultOAuth2User user=(DefaultOAuth2User)authentication.getPrincipal();
        User user1=new User();
        user1.setEmailVerified(true);
        user1.setPassword("password");
        user1.setUserId(UUID.randomUUID().toString());
        user1.setEnabled(true);

        user.getAttributes().forEach((key,value) ->{
            logger.info("{} => {}",key, value);
        });


        //google
        if(authProvider.equalsIgnoreCase("google")){



            //Getting Data From Google login
            String email = user.getAttribute("email").toString();
            String name=user.getAttribute("name").toString();
            String picture = user.getAttribute("picture").toString();

            //Creating User
            user1.setEmail(email);

            user1.setName(name);

            user1.setProfilePicture(picture);

            user1.setProvider(Provider.GOOGLE);

            user1.setProviderId(user.getName());

        }
        //github
        else if (authProvider.equalsIgnoreCase("github")) {
            String email = user.getAttribute("email") != null ? user.getAttribute("email").toString() : user.getAttribute("login").toString() + "@gmail.com";
            String picture = user.getAttribute("avatar_url").toString();
            String name = user.getAttribute("login").toString();
            String providerId = user.getName();

            user1.setName(name);
            user1.setEmail(email);
            user1.setProfilePicture(picture);
            user1.setProviderId(providerId);
            user1.setProvider(Provider.GITHUB);

        }
        //Saving Data into Database
        User user2 = userRepo.findByEmail(user1.getEmail()).orElse(null);
        if(user2 == null){
            userRepo.save(user1);
            logger.info("User Saved Successfully");
        }

        new DefaultRedirectStrategy().sendRedirect(request,response,"/user/dashboard");
    }
}
