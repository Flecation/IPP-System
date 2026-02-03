package IPPSystem.OTP;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    private final Session mailSession;
    private final String fromEmail;

    public EmailService(String host, int port, String username, String password, boolean startTls) {
        this.fromEmail = username;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        // ✅ TLS settings
        props.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        props.put("mail.smtp.starttls.required", "true");

        // ✅ helps with Gmail TLS handshake on some JDKs
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", host);

        // timeouts (optional)
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        this.mailSession = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        this.mailSession.setDebug(true); // ✅ keep for now, remove later
    }


    public void sendOtp(String toEmail, String otp) throws MessagingException {
        Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(fromEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject("IPP System - Password Reset Code");
        msg.setText(
                "Your password reset code is: " + otp + "\n\n" +
                        "This code expires in 5 minutes.\n" +
                        "If you didn't request this, please ignore this email."
        );
        Transport.send(msg);
    }
}
