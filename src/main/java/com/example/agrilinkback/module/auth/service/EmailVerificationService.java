package com.example.agrilinkback.module.auth.service;

import com.example.agrilinkback.common.exception.BusinessException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_INTERVAL = Duration.ofSeconds(60);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final JavaMailSender mailSender;
    private final String sender;
    private final Map<String, VerificationCode> codes = new ConcurrentHashMap<>();

    public EmailVerificationService(JavaMailSender mailSender,
                                    @Value("${spring.mail.username:}") String sender) {
        this.mailSender = mailSender;
        this.sender = sender;
    }

    public void sendCode(String rawEmail) {
        String email = normalize(rawEmail);
        if (sender.isBlank()) {
            throw new BusinessException(503, "163 mail sender is not configured");
        }

        Instant now = Instant.now();
        VerificationCode previous = codes.get(email);
        if (previous != null && now.isBefore(previous.sentAt().plus(RESEND_INTERVAL))) {
            throw new BusinessException(429, "Please wait 60 seconds before requesting another code");
        }

        String code = "%06d".formatted(RANDOM.nextInt(1_000_000));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject("Agri-Link registration verification code");
        message.setText("Your Agri-Link verification code is " + code
                + ". It is valid for 5 minutes. Do not share it with anyone.");
        try {
            mailSender.send(message);
            codes.put(email, new VerificationCode(code, now, now.plus(CODE_TTL)));
        } catch (MailException ex) {
            throw new BusinessException(503, "Verification email could not be sent");
        }
    }

    public void verify(String rawEmail, String code) {
        String email = normalize(rawEmail);
        VerificationCode saved = codes.remove(email);
        if (saved == null || Instant.now().isAfter(saved.expiresAt()) || !saved.value().equals(code)) {
            throw new BusinessException("Verification code is invalid or expired");
        }
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private record VerificationCode(String value, Instant sentAt, Instant expiresAt) {
    }
}
