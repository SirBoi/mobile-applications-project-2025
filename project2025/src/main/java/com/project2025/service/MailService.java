package com.project2025.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Mejl slanje ne sme da obori glavni tok (npr. prihvatanje vožnje) ako
    // SMTP privremeno ne radi, zato gutamo grešku i samo je logujemo.
    public void send(String to, String subject, String body) {
        if (to == null || to.isBlank()) return;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Slanje mejla na " + to + " nije uspelo: " + e.getMessage());
        }
    }
}