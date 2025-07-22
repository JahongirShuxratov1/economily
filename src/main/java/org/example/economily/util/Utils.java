package org.example.economily.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class Utils {

    private final JavaMailSender mailSender;

    public boolean checkEmail(String email) {
        if (email == null || email.isEmpty())
            return false;
        return email.contains("@");
    }

    public String getCode() {
        SecureRandom random = new SecureRandom();
        int randomCode = 100000 + random.nextInt(900000); // Generates a number between 10000 and 99999
        return String.valueOf(randomCode);
    }

    public boolean sendCodeToMail(String mail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("xalqaroshartnomalaruz@gmail.com");
            helper.setTo(mail);
            helper.setSubject("🔐 Economily APP – Tasdiqlash kodi");

            String htmlContent = """
                        <html>
                            <body style="margin: 0; padding: 0; background: #f4f4f4; font-family: Arial, sans-serif;">
                                <div style="max-width: 600px; margin: 40px auto; padding: 30px; background: white; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);">
                                    <h2 style="text-align: center; color: #333;">Tasdiqlash uchun kod</h2>
                                    <p style="text-align: center; color: #666;">Quyidagi kodni ilovada tasdiqlash uchun kiriting:</p>
                                    <div style="margin: 30px auto; width: fit-content; background: linear-gradient(to right, #4facfe, #00f2fe); padding: 20px 40px; border-radius: 12px; box-shadow: 0 0 10px rgba(0,0,0,0.15);">
                                        <span style="font-size: 36px; font-weight: bold; color: white; letter-spacing: 5px;">%s</span>
                                    </div>
                                    <p style="text-align: center; color: #999; font-size: 14px;">Agar bu siz bo‘lmasangiz, bu xabarni e’tiborsiz qoldiring.</p>
                                </div>
                            </body>
                        </html>
                    """.formatted(code);

            helper.setText(htmlContent, true); // HTML formatda yuborish
            mailSender.send(message);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

}
