package com.diverscinnova.site;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Recebe o formulário de contato do site e envia por e-mail via SMTP (SendPulse).
 * As credenciais SMTP vêm de variáveis de ambiente (ver application.properties) —
 * nada sensível fica no código nem no front-end.
 */
@RestController
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String smtpUser;

    @Value("${app.mail.to:}")
    private String mailTo;

    @Value("${app.mail.from:}")
    private String mailFrom;

    public ContactController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/api/contato")
    public ResponseEntity<Map<String, Object>> contato(@RequestBody(required = false) ContactForm form) {
        if (form == null) {
            return badRequest();
        }

        // Honeypot: se o campo escondido veio preenchido, é bot — aceita em silêncio e ignora.
        if (form.website() != null && !form.website().isBlank()) {
            return ResponseEntity.ok(Map.of("ok", true));
        }

        String name = trim(form.name());
        String email = trim(form.email());
        String message = trim(form.message());

        if (name.isEmpty() || message.isEmpty() || !EMAIL.matcher(email).matches()) {
            return badRequest();
        }

        String to = mailTo.isBlank() ? smtpUser : mailTo;
        String from = mailFrom.isBlank() ? smtpUser : mailFrom;

        if (smtpUser.isBlank() || to.isBlank()) {
            log.error("SMTP não configurado: defina as variáveis SMTP_USER/SMTP_PASS (e MAIL_TO).");
            return ResponseEntity.status(503).body(Map.of("ok", false, "error", "not_configured"));
        }

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setTo(to);
            helper.setFrom(from);
            helper.setReplyTo(email, name);
            helper.setSubject("Novo contato pelo site — " + name);
            helper.setText(
                    "Nome: " + name + "\n" +
                    "E-mail: " + email + "\n\n" +
                    "Mensagem:\n" + message
            );
            mailSender.send(mime);
            log.info("Contato enviado para {} (reply-to {}).", to, email);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception e) {
            log.error("Falha ao enviar o e-mail de contato: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("ok", false, "error", "send_failed"));
        }
    }

    private static ResponseEntity<Map<String, Object>> badRequest() {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "error", "invalid"));
    }

    private static String trim(String s) {
        return s == null ? "" : s.strip();
    }
}
