package com.taquigrafia.ai.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendActivationEmail(String to, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Ative sua conta");
        message.setText("Por favor, clique no link a seguir para ativar sua conta: " + activationLink);
        mailSender.send(message);
    }
}
