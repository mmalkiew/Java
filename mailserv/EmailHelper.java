package pl.net.amg.smartcity.mail;

import java.util.Date;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

/**
 * Klasa narzedziowa do wysylania wiadomosci email.
 * 
 * @author mmalkiew
 *
 */
public class EmailHelper {

	public static final Logger log = Logger.getLogger(EmailHelper.class.getName());

	public static final String EMAIL_SESSION_JNDI_PATH = "java:jboss/mail/mka-mail";

	/**
	 * Wysylanie wiadomosci email w biezacej sesji.
	 *
	 * @param session
	 * @param email
	 *            {@link Email}
	 * @throws Exception
	 */
	public static void sendMail(Email email) throws Exception {
		Session session = getEmailSession();
		Message message = new MimeMessage(session);

		// przekazanie listy odbiorcow
		for (String address : email.getTo()) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					address));
		}

		message.setSubject(email.getSubject());
		message.setSentDate(new Date());
		// Set content
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(email.getContentBodyPart());

		for (MimeBodyPart mimeBodyPart : email.getBodyParts()) {
			multipart.addBodyPart(mimeBodyPart);
		}
		message.setContent(multipart);
		// wyslanie wiadomosci
		Transport.send(message);
	}

	/**
	 * Tworzenie obiektu javax.mail.Session
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Session getEmailSession() throws Exception {
		InitialContext context = new InitialContext();
		return (Session) context.lookup(EMAIL_SESSION_JNDI_PATH);
	}
	
	 public static String format(String content, Map<String, String> arguments) {
	        StringBuilder formattedContent = new StringBuilder(content);

	        for (Map.Entry<String, String> mapEntry : arguments.entrySet()) {
	            String method = "$(" + mapEntry.getKey() + ")";
	            int position;
	            if (mapEntry.getValue() != null && (position = formattedContent.indexOf(method)) > -1) {
	                formattedContent.replace(position, position + method.length(), mapEntry.getValue());
	            }
	        }
	        return formattedContent.toString();
	    }
}
