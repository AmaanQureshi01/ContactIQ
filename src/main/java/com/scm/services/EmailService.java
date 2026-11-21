package com.scm.services;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public interface EmailService {

    //Send Email to Single Person
    void sendEmail(String to, String subject, String body);

    //Send Email to multiple person
    void sendEmail(String []to, String subject,String message);

    //Send Email with HTML
    void sendEmailWithHtml(String to, String subject, String message);

    //Send Email with Files and documents
    void sendEmailWithAttachment(String to, String subject, String message, File file);

    //Send Email with dynamic file
    void sendEmailwithFile(String to, String subject, String body, InputStream inputStream);
}
