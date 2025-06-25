package login;

import jakarta.mail.*;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class EmailService {
	
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final String SMTP_PORT = "587";
	private static final String EMAIL_USERNAME = "datcv.24it@vku.udn.vn"; // Email của bạn
	private static final String EMAIL_PASSWORD = "jept nvnn jhba eiiv";    // App password
	
	public static String generateRandomPassword() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder password = new StringBuilder();
		Random random = new Random();
		
		for (int i = 0; i < 8; i++) {
			password.append(chars.charAt(random.nextInt(chars.length())));
		}
		return password.toString();
	}
	
	public static boolean sendNewPassword(String toEmail, String newPassword, String fullName) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", SMTP_HOST);
			props.put("mail.smtp.port", SMTP_PORT);
			
			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
				}
			});
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(EMAIL_USERNAME));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject("Mật khẩu mới - Hệ thống Internet Billing");
			
			String emailContent = "<html><body>" +
					"<h2>Mật khẩu mới của bạn</h2>" +
					"<p>Xin chào " + fullName + ",</p>" +
					"<p>Mật khẩu mới của bạn là: <strong>" + newPassword + "</strong></p>" +
					"<p>Vui lòng đăng nhập và đổi mật khẩu ngay sau khi nhận được email này.</p>" +
					"<p>Trân trọng,<br/>Hệ thống Internet Billing</p>" +
					"</body></html>";
			
			message.setContent(emailContent, "text/html; charset=utf-8");
			Transport.send(message);
			return true;
			
		} catch (MessagingException e) {
			System.err.println("Lỗi gửi email: " + e.getMessage());
			return false;
		}
	}
}
