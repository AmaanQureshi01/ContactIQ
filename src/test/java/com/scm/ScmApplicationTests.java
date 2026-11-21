package com.scm;

import com.scm.entities.User;
import com.scm.services.EmailService;
import com.scm.services.UserServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest
class ScmApplicationTests {

    @Test
    void contextLoads() {
    }
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserServices userServices;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    void sendEmailTest(){
        emailService.sendEmail(
                "mohdamaanqureshi01@gmail.com",
                "just testing email service",
                "This is scm project working on email service"
        );
    }

    @Test
    void sendHtmlTest(){

        String html=""+"<h1 style='color:red; border:2px solid blue;'>Hello IT is HTML Testing CASe 1</h1>";

        emailService.sendEmailWithHtml(
                "mohdamaanqureshi01@gmail.com",
                "Email For HTML Testing",
                html
        );

    }

    @Test
    void sendAttaachment(){

        emailService.sendEmailWithAttachment(
                "mohdamaanqureshi01@gmail.com",
                "Email with Attachment",
                "testing email with attachment case - 1",
                new File("D:\\Codes\\Projects\\scm\\src\\main\\resources\\static\\image\\default-avatar.png")
        );
    }

    @Test
    void sendStream(){
        File file =new File("D:\\Codes\\Projects\\scm\\src\\main\\resources\\static\\image\\default-avatar.png");
        try {
            InputStream is = new FileInputStream(file);
            emailService.sendEmailwithFile(
                    "mohdamaanqureshi01@gmail.com",
                    "Email with file",
                    "testing email with file case - 1",
                    is
                    );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void changePassword(){
        User user=userServices.getUserByEmail("test2@gmail.com");
        user.setPassword(passwordEncoder.encode("test123"));
        userServices.directSave(user);
        System.out.println("passwordChanged");
    }
}
