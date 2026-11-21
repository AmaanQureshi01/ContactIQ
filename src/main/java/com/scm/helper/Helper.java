package com.scm.helper;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication){

        if(authentication instanceof  OAuth2AuthenticationToken) {
            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            String authProvider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
            //Sign in with Google
            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
            if (authProvider.equalsIgnoreCase("google")) {
                return user.getAttribute("email").toString();
            }

            //Sign in with Github
            else if (authProvider.equalsIgnoreCase("github")) {
                return user.getAttribute("email") != null ? user.getAttribute("email").toString() : user.getAttribute("login").toString() + "@gmail.com";
            }

        }
        //Sign in with email and password

        return authentication.getName();

    }

    public static String getLinkForEmailVerification(String emailToken){
        String link="http://localhost:8080/auth/verify-email?token="+emailToken;

        return link;
    }

    // HTML Email Template for Magic Link
    public static String buildResetEmailHtml(String name, String magicLink) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 520px; margin: 0 auto; padding: 24px; border: 1px solid #e5e7eb; border-radius: 12px; background-color: #ffffff;">
                    <h2 style="color: #111827; margin-top: 0; margin-bottom: 12px;">Reset Your Password</h2>
                    <p style="color: #4b5563; font-size: 14px; line-height: 1.6;">
                        Hi <strong>%s</strong>,<br><br>
                        We received a request to reset your password. Click the button below to choose a new password:
                    </p>
                    <div style="margin: 28px 0; text-align: center;">
                        <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 14px; display: inline-block;">
                            Reset Password
                        </a>
                    </div>
                    <p style="color: #6b7280; font-size: 12px; line-height: 1.5;">
                        If the button doesn't work, copy and paste this link into your browser:<br>
                        <a href="%s" style="color: #2563eb; word-break: break-all;">%s</a>
                    </p>
                    <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 20px 0;">
                    <p style="color: #9ca3af; font-size: 12px; margin-bottom: 0;">
                        If you didn't request a password reset, you can safely ignore this email.
                    </p>
                </div>
                """.formatted(name, magicLink, magicLink, magicLink);
    }

    public static boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
