package pl.net.amg.smartcity.mail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

/**
 * Klasa reprezentuje wiadomosc email.
 * 
 * @author mmalkiew
 *
 */
public class Email {

	private String from = "";
	private List<String> to = new ArrayList<>();
	private String subject = "";
	private String body = "";
	private boolean html = false;
	private List<MimeBodyPart> bodyParts = new ArrayList<>();

	/**
	 * Podstawowy konstruktor. Nalezy ustawic zmianne.
	 */
	public Email() {
	}

	/**
	 * Konstruktor ustawiajacy minimalna liczbe zmiannych potrzebnych do
	 * wyslania email w mka.
	 *
	 * @param to Adres odbiorcy
	 * @param body Tresc mail'a
	 * @param subject Temat wiadomosci
	 */
	public Email(String to, String subject, String body) {
		this.to.add(to);
		this.subject = subject;
		this.body = body;
	}

	/**
	 * Konstruktor ustawiajacy minimalna liczbe zmiannych potrzebnych do
	 * wyslania email.
	 *
	 * @param from Adres nadawcy
	 * @param to Adres odbiorcy
	 * @param body Tresc mail'a
	 * @param subject Temat wiadomosci
	 */
	public Email(String from, String to, String subject, String body) {
		this.from = from;
		this.to.add(to);
		this.subject = subject;
		this.body = body;
	}

	/**
	 * Pobiera zawartosc wiadomosci
	 *
	 * @return
	 * @throws MessagingException
	 */
	public MimeBodyPart getContentBodyPart() throws MessagingException {
		MimeBodyPart contentBodyPart = new MimeBodyPart();
		if (html) {
			contentBodyPart.setHeader("Content-Type", "text/html");
			contentBodyPart.setContent(body, "text/html; charset=ISO-8859-2");
		} else {
			contentBodyPart.setContent(body, "text/plain; charset=ISO-8859-2");
		}

		return contentBodyPart;
	}

	/**
	 * Dodaje wszystkie podane adresy na wejsciu do listy
	 *
	 * @param address
	 */
	public void addTo(String... address) {
		to.addAll(java.util.Arrays.asList(address));
	}

	/**
	 * Dodaje przygotowana liste adresow odbiorcow wiadomosci do listy
	 * konkretnych odbiorcow
	 *
	 * @param addresses Lista odbiorcow
	 */
	public void addTo(List<String> addresses) {
		to.addAll(addresses);
	}
	
	/**
	 * Metoda pozwala na dodanie wielu zalacznikow za jednym razem.
	 *
	 * @param attachment Zalaczniki
	 * @return Lista wszystkich zalacznikow.
	 */
	public List<EmailAttachment> addEmailAttachments(EmailAttachment... attachment) {
		List<EmailAttachment> failedAttachments = new ArrayList<>();
		for (EmailAttachment emailAttachment : attachment) {
			try {
				boolean success = addEmailAttachment(emailAttachment);
				if (!success) {
					failedAttachments.add(emailAttachment);
				}
			} 
			catch (Exception ex) {
				failedAttachments.add(emailAttachment);
			}
		}
		return failedAttachments;
	}

	/**
	 * Dodaje zalacznik jako body part. Metoda sprawdza czy zalacznik posiada juz 
	 * zrodlo (strumien bajtow).
	 *
	 * @param attachment {@link EmailAttachment}
	 * @return true/false w zaleznosci czy udalo sie dodac zalacznik czy tez nie
	 * @throws MessagingException
	 */
	public boolean addEmailAttachment(EmailAttachment attachment) throws MessagingException, FileNotFoundException, IOException, Exception {
		if (attachment.getBodyPart() == null) {
			boolean success = attachment.generateMimeBodyPart();
			if (!success) {
				return false;
			}
		}
		
		bodyParts.add(attachment.getBodyPart());
		return true;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<MimeBodyPart> getBodyParts() {
		return bodyParts;
	}

	public void setBodyParts(List<MimeBodyPart> bodyParts) {
		this.bodyParts = bodyParts;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}
}
