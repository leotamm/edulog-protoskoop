package ee.protoskoop.gwt.edulog.server;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ee.protoskoop.gwt.edulog.shared.User;

public class SendEmailTLS {

	public static boolean sendNewPassword(User user) {

		boolean reply = false;

		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(prop,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Configuration.EMAIL_USER, Configuration.EMAIL_PASS);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Configuration.EMAIL_USER));
			message.setRecipients(
					Message.RecipientType.TO,
					InternetAddress.parse(user.getEmail())
					);
			message.setSubject("Resetting Edulog password");
			message.setText("Dear Edulog user," +
					"n\n your new password is: " + user.getPassword() + 
					"\n\n Try logging in again!" +
					"\n\n This is an automated message - please do not reply");

			Transport.send(message);

			reply = true;

		} catch (MessagingException e) { e.printStackTrace(); }

		return reply;

	}

}
