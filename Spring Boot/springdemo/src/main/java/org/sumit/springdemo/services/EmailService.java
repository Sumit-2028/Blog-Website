package org.sumit.springdemo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.sumit.springdemo.util.mail.EmailDetail;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;
    // Method to send email

    @Value("${spring.mail.username}")
    private String user;

    public boolean sendSimpleEmail(EmailDetail emailDetail) {
        try {
            // Create a Simple MailMessage object
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user); // Set the recipient email address
            message.setSubject(emailDetail.getSubject());
            message.setText(emailDetail.getBody());
            message.setFrom(user);
            
            // Send the email
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
