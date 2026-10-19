package com.newasteralnx.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final String from;
    private final String publicBaseUrl;
    private final String mailHost;

    public MailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.mail.from:noreply@asteralnx.com}") String from,
            @Value("${app.public-base-url:http://localhost:8080}") String publicBaseUrl,
            @Value("${spring.mail.host:}") String mailHost) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.from = from;
        this.publicBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
        this.mailHost = mailHost != null ? mailHost.trim() : "";
    }

    /** @return true if an email was accepted by SMTP; false if logged-only (no SMTP host). */
    public boolean sendPasswordResetEmail(String toEmail, String fullName, String otp) {
        String resetLink = publicBaseUrl + "/index.html?otp=" + otp;
        String subject = "AsteraLNX — Password reset code";
        String html = buildHtml(fullName, otp, resetLink);
        String text = buildText(fullName, otp, resetLink);

        if (!StringUtils.hasText(mailHost) || mailSender == null) {
            log.info("""
                    ===== PASSWORD RESET EMAIL (SMTP not configured — logged only) =====
                    To: {}
                    Subject: {}
                    OTP: {}
                    Link: {}
                    Configure MAIL_HOST / MAIL_USERNAME / MAIL_PASSWORD to send real email.
                    ================================================================
                    """, toEmail, subject, otp, resetLink);
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, html);
            mailSender.send(message);
            log.info("Password reset email sent to={}", toEmail);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send password reset email to={}: {}", toEmail, ex.getMessage());
            log.warn("Fallback log OTP for email={} otp={} link={}", toEmail, otp, resetLink);
            throw new IllegalStateException("Unable to send password reset email. Please try again later or contact the administrator.");
        }
    }


    public boolean sendBusinessEmail(String toEmail, String subject, String htmlBody) {
        if (!StringUtils.hasText(toEmail) || !toEmail.contains("@")) {
            throw new IllegalArgumentException("Selected customer's email address is missing or invalid");
        }
        if (!StringUtils.hasText(mailHost) || mailSender == null) {
            throw new IllegalStateException("Email service is not configured. Configure MAIL_HOST, MAIL_USERNAME and MAIL_PASSWORD before sending customer emails.");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Business email sent to={} subject={}", toEmail, subject);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send business email to={}: {}", toEmail, ex.getMessage());
            throw new IllegalStateException("Email could not be sent to the selected customer. Check SMTP configuration and try again.");
        }
    }

    private static String buildText(String fullName, String otp, String resetLink) {
        return """
                Hello %s,

                You requested a password reset for your AsteraLNX account.

                Your one-time password (OTP): %s
                This code expires in 30 minutes and can be used only once.

                Or open this link to reset your password:
                %s

                If you did not request this, you can ignore this email.

                — AsteraLNX
                """.formatted(fullName != null ? fullName : "User", otp, resetLink);
    }

    private static String buildHtml(String fullName, String otp, String resetLink) {
        String name = fullName != null ? fullName : "User";
        return """
                <div style="font-family:Inter,Arial,sans-serif;max-width:560px;margin:0 auto;padding:24px;color:#111827">
                  <div style="background:linear-gradient(135deg,#1a56db,#1e3a8a);color:#fff;padding:20px 24px;border-radius:12px 12px 0 0">
                    <strong style="font-size:18px">AsteraLNX</strong>
                    <div style="opacity:.85;font-size:13px;margin-top:4px">Password reset</div>
                  </div>
                  <div style="border:1px solid #e5e7eb;border-top:0;padding:24px;border-radius:0 0 12px 12px">
                    <p style="margin:0 0 12px">Hello <strong>%s</strong>,</p>
                    <p style="margin:0 0 16px;color:#4b5563">You requested a password reset for your AsteraLNX account.
                    Use the one-time password below. It expires in <strong>30 minutes</strong>.</p>
                    <div style="text-align:center;margin:24px 0">
                      <div style="display:inline-block;letter-spacing:8px;font-size:28px;font-weight:800;background:#f3f4f6;padding:12px 20px;border-radius:10px;border:1px dashed #cbd5e1">%s</div>
                    </div>
                    <p style="text-align:center;margin:0 0 20px">
                      <a href="%s" style="display:inline-block;background:#1a56db;color:#fff;text-decoration:none;padding:12px 22px;border-radius:8px;font-weight:600">Reset password</a>
                    </p>
                    <p style="font-size:12px;color:#6b7280;margin:0">Or paste this link into your browser:<br>
                    <a href="%s" style="color:#1a56db;word-break:break-all">%s</a></p>
                    <hr style="border:none;border-top:1px solid #e5e7eb;margin:20px 0">
                    <p style="font-size:12px;color:#9ca3af;margin:0">If you did not request this, you can safely ignore this email.</p>
                  </div>
                </div>
                """.formatted(name, otp, resetLink, resetLink, resetLink);
    }
}
