package org.sumit.springdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;


@Configuration
public class AppConfig {

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.protocol}")
    private String mailProtocol;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean mailSmtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String mailSmtpSslTrust;   

    @Bean
    public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(mailHost);
    mailSender.setPort(mailPort);
    mailSender.setUsername(mailUsername);
    mailSender.setPassword(mailPassword);
    mailSender.setProtocol(mailProtocol);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", mailProtocol);
    props.put("mail.smtp.auth", mailSmtpAuth);
    props.put("mail.smtp.starttls.enable", starttlsEnable);
    props.put("mail.debug", "true");
    props.put("mail.smtp.ssl.trust", mailSmtpSslTrust);

    return mailSender;

    }
}
