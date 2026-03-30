package com.coffee.app.service.impl;

import com.coffee.app.domain.PasswordResetToken;
import com.coffee.app.domain.User;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.repository.PasswordResetTokenRepository;
import com.coffee.app.repository.UserRepository;
import com.coffee.app.service.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

  private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm");
  private static final ZoneId PP_ZONE = ZoneId.of("Asia/Phnom_Penh");

  private final UserRepository userRepository;
  private final PasswordResetTokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JavaMailSender mailSender;

  @Value("${spring.mail.username:}")
  private String fromEmail;

  @Value("${spring.mail.password:}")
  private String mailPassword;

  @Value("${telegram.bot-token:}")
  private String telegramBotToken;

  @Value("${telegram.chat-id:}")
  private String telegramChatId;

  public PasswordResetServiceImpl(UserRepository userRepository,
      PasswordResetTokenRepository tokenRepository,
      PasswordEncoder passwordEncoder,
      JavaMailSender mailSender) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.mailSender = mailSender;
  }

  @Override
  @Transactional
  public void requestReset(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));

    // Remove any previous unused tokens for this email
    tokenRepository.deleteByEmail(email);

    // Generate secure 6-digit OTP
    String otp = String.format("%06d", new Random().nextInt(1_000_000));

    PasswordResetToken token = new PasswordResetToken();
    token.setEmail(email);
    token.setOtp(otp);
    token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
    tokenRepository.save(token);

    String name = user.getGivenName() != null ? user.getGivenName() : user.getUsername();

    // Try email first; fall back to Telegram; then log-only
    boolean sent = false;

    if (isEmailConfigured()) {
      sent = trySendEmail(email, name, otp);
    }

    if (!sent) {
      sent = trySendTelegram(email, name, otp);
    }

    if (!sent) {
      // Last resort: log so admin can relay it manually
      log.warn("⚠️  OTP delivery not configured — OTP for {} is: {}", email, otp);
      log.warn("    Set MAIL_PASSWORD in deployment/.env to enable email delivery.");
    }
  }

  @Override
  @Transactional
  public void resetPassword(String otp, String newPassword) {
    PasswordResetToken token = tokenRepository.findByOtpAndUsedFalse(otp)
        .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP."));

    if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("OTP has expired. Please request a new one.");
    }

    User user = userRepository.findByEmail(token.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("Account not found."));

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    token.setUsed(true);
    tokenRepository.save(token);

    log.info("Password reset successfully for: {}", token.getEmail());
  }

  // ── Helpers ──────────────────────────────────────────────────────────────

  private boolean isEmailConfigured() {
    return fromEmail != null && !fromEmail.isBlank()
        && mailPassword != null && !mailPassword.isBlank();
  }

  private boolean trySendEmail(String toEmail, String name, String otp) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(fromEmail, "SalSee Coffee ☕");
      helper.setTo(toEmail);
      helper.setSubject("Your Password Reset Code — " + otp);
      helper.setText(buildHtmlEmail(name, otp), true);
      mailSender.send(message);
      log.info("OTP email sent to: {}", toEmail);
      return true;
    } catch (MessagingException | java.io.UnsupportedEncodingException ex) {
      log.error("Email send failed for {}: {}", toEmail, ex.getMessage());
      return false;
    } catch (Exception ex) {
      log.error("Unexpected email error for {}: {}", toEmail, ex.getMessage());
      return false;
    }
  }

  private boolean trySendTelegram(String toEmail, String name, String otp) {
    if (telegramBotToken == null || telegramBotToken.isBlank()
        || telegramChatId == null || telegramChatId.isBlank()) {
      return false;
    }
    try {
      String text = "🔐 <b>Password Reset OTP</b>\n"
          + "▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n"
          + "👤 <b>User:</b> " + name + "\n"
          + "📧 <b>Email:</b> <code>" + toEmail + "</code>\n\n"
          + "🔢 <b>OTP Code:</b>\n"
          + "   <code>" + otp + "</code>\n\n"
          + "⏱ Valid for <b>10 minutes</b>\n"
          + "▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁\n"
          + "Please relay this code to the user.";

      String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";
      Map<String, String> body = new LinkedHashMap<>();
      body.put("chat_id", telegramChatId);
      body.put("text", text);
      body.put("parse_mode", "HTML");

      RestClient.create().post().uri(url)
          .header("Content-Type", "application/json")
          .body(body)
          .retrieve()
          .toBodilessEntity();

      log.info("OTP sent via Telegram for: {}", toEmail);
      return true;
    } catch (Exception ex) {
      log.error("Telegram OTP send failed: {}", ex.getMessage());
      return false;
    }
  }

  private String buildHtmlEmail(String name, String otp) {
    String now = ZonedDateTime.now(PP_ZONE).format(TIME_FMT);
    String otpDisplay = String.join(" &nbsp;", otp.split(""));
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <title>Password Reset</title>
        </head>
        <body style="margin:0;padding:0;background:#0f0f0f;font-family:'Segoe UI',Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#0f0f0f;padding:40px 20px;">
            <tr><td align="center">
              <table width="480" cellpadding="0" cellspacing="0"
                     style="background:#1a1a1a;border-radius:20px;border:1px solid #2a2a2a;overflow:hidden;">

                <!-- Header -->
                <tr>
                  <td style="background:linear-gradient(135deg,#b45309,#d97706);padding:32px;text-align:center;">
                    <p style="margin:0;font-size:28px;">☕</p>
                    <h1 style="margin:8px 0 0;color:#fff;font-size:22px;font-weight:800;letter-spacing:-0.5px;">
                      SalSee Coffee
                    </h1>
                    <p style="margin:4px 0 0;color:rgba(255,255,255,0.8);font-size:13px;">Password Reset Request</p>
                  </td>
                </tr>

                <!-- Body -->
                <tr>
                  <td style="padding:36px 40px 24px;">
                    <p style="margin:0 0 8px;color:#aaa;font-size:13px;">Hi <strong style="color:#fff;">%s</strong>,</p>
                    <p style="margin:0 0 28px;color:#888;font-size:14px;line-height:1.6;">
                      We received a request to reset your password. Use the code below — it's valid for
                      <strong style="color:#d97706;">10 minutes</strong>.
                    </p>

                    <!-- OTP Box -->
                    <div style="background:#111;border:2px solid #d97706;border-radius:16px;padding:28px 20px;text-align:center;margin-bottom:28px;">
                      <p style="margin:0 0 6px;color:#888;font-size:11px;letter-spacing:2px;text-transform:uppercase;">
                        Your OTP Code
                      </p>
                      <p style="margin:0;font-size:42px;font-weight:900;letter-spacing:12px;color:#f59e0b;font-family:monospace;">
                        %s
                      </p>
                    </div>

                    <!-- Steps -->
                    <ol style="margin:0 0 24px;padding-left:20px;color:#888;font-size:13px;line-height:2;">
                      <li>Go to <a href="http://localhost:3000/reset-password" style="color:#d97706;">Reset Password page</a></li>
                      <li>Enter the 6-digit code above</li>
                      <li>Choose a new password</li>
                    </ol>

                    <div style="background:#1f1f1f;border-radius:12px;padding:14px 16px;margin-bottom:24px;">
                      <p style="margin:0;color:#666;font-size:12px;">
                        ⚠️ &nbsp;If you didn't request this, you can safely ignore this email.
                        Your password won't change.
                      </p>
                    </div>
                  </td>
                </tr>

                <!-- Footer -->
                <tr>
                  <td style="border-top:1px solid #2a2a2a;padding:20px 40px;text-align:center;">
                    <p style="margin:0;color:#555;font-size:11px;">
                      Requested at %s &middot; SalSee Coffee Shop
                    </p>
                  </td>
                </tr>

              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """
        .formatted(name, otpDisplay, now);
  }
}
