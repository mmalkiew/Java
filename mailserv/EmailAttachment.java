package pl.net.amg.smartcity.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

/**
 * Klasa reprezentuje zalacznik do wiadomosci email.
 * 
 * @author mmalkiew
 *
 */
public class EmailAttachment {

	/**
	 * Zalacznik jako plik
	 */
	private File file;
	
	
	/**
	 * Zalacznik jako URL
	 */
	private URL url;
	
	/**
	 * Zalacznik jako strumien bajtow
	 */
	private byte[] fileBytes;
	
	/**
	 * Nazwa pliku zalacznika
	 */
	private String fileName;
	
	/**
	 * Nazwa zalacznika
	 */
	private String attachmentName;
	
	/**
	 * Rozszerzenie zalacznika
	 */
	private String extension;
	
	/**
	 * Pelna nazwa zalacznika
	 */
	private String fullFileName;
	
	private MimeBodyPart bodyPart;
	/**
	 * Wykorzystywane podczas generowania body partow
	 */
	private String contentId;
	private String contentType;
	private String disposition;

	public EmailAttachment() {
		contentType = "application/octet-stream";
		disposition = Message.ATTACHMENT;
	}
	
	/**
	 * Konstruktor z minimalna liczba parametrow jakie nalezy przekazac aby poprawnie utworzyc zalcznik.
	 *
	 * @param fileBytes strumien bajtow zalacznik
	 * @param fullFileName Pelna nazwa zalacznika
	 */
	public EmailAttachment(byte[] fileBytes, String fullFileName) {
		this.fileBytes = fileBytes;
		this.fullFileName = fullFileName;
		this.contentType = "application/octet-stream";
		this.disposition = Message.ATTACHMENT;
	}

	/**
	 * Konstruktor z minimalna liczba parametrow jakie nalezy przekazac aby poprawnie utworzyc zalcznik.
	 * Dodatkowo mozemy zdefiniowac sobie typ zalacznik i sposob jego prezentacji.
	 *
	 * @param fileBytes
	 * @param fullFileName example: "document.txt"
	 */
	public EmailAttachment(byte[] fileBytes, String fullFileName, String contentType, String disposition) {
		this.fileBytes = fileBytes;
		this.fullFileName = fullFileName;
		this.contentType = contentType;
		this.disposition = disposition;
	}
	
	/**
	 * Konstruktor z minimalna liczba parametrow jakie nalezy przekazac aby poprawnie utworzyc zalcznik.
	 * 
	 * @param file Plik jako zalacznik
	 */
	public EmailAttachment(File file) {
		this.file = file;
		this.contentType = "application/octet-stream";
		this.disposition = Message.ATTACHMENT;
	}

	/**
	 * Konstruktor z minimalna liczba parametrow jakie nalezy przekazac aby poprawnie utworzyc zalcznik.
	 * Dodatkowo mozemy zdefiniowac sobie typ zalacznik i sposob jego prezentacji.
	 *
	 * @param file Plik jako zalacznik
	 */
	public EmailAttachment(File file, String contentType, String disposition) {
		this.file = file;
		this.contentType = contentType;
		this.disposition = disposition;
	}
	
	/**
	 * Konstruktor z minimalna liczba parametrow jakie nalezy przekazac aby poprawnie utworzyc zalcznik.
	 * 
	 * @param url URL
	 */
	public EmailAttachment(URL url) {
		this.url = url;
		this.contentType = "application/octet-stream";
		this.disposition = Message.ATTACHMENT;
	}


	/**
	 * Konstruktor z minimalna liczba parametrow jakie nalezy przekazac aby poprawnie utworzyc zalcznik.
	 * Dodatkowo mozemy zdefiniowac sobie typ zalacznik i sposob jego prezentacji.
	 * 
	 * @param url
	 * @param contentType
	 * @param disposition
	 */
	public EmailAttachment(URL url, String contentType, String disposition) {
		this.url = url;
		this.contentType = contentType;
		this.disposition = disposition;
	}


	/**
	 * Generuje MimeBodyPart dla zalacznika.
	 * 
	 * @return
	 * @throws MessagingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean generateMimeBodyPart() throws MessagingException, FileNotFoundException, IOException, Exception {
		DataSource source;
		if (bodyPart != null) {
			return true;
		}
		
		bodyPart = new MimeBodyPart();
		if (file != null) {
			source = getFileBodyPart();
			bodyPart.setDisposition(disposition);
		} 
		else if (fileBytes != null) {
			source = getByteArrayBodyPart();
		} 
		else if (url != null) {
			source = getURLBodyPart();
			bodyPart.setDisposition(disposition);
		} 
		else {
			bodyPart = null; 
			return false;
		}
		
		bodyPart.setDataHandler(new DataHandler(source));
		bodyPart.setFileName(getAttachmentName());
		
		if (contentId != null) {
			bodyPart.setContentID(contentId);
		}
		
		return true;
	}

	/**
	 * Zwraca FileDataSource dla pliku
	 *
	 * @return
	 */
	private DataSource getFileBodyPart() {
		return new FileDataSource(file) {

			@Override
			public String getContentType() {
				return contentType;
			}
		};

	}

	/**
	 * Zwraca ByteArrayDataSource dla zdefiniowanego strumienia bajtow oraz sposobie prezentacji
	 *
	 * @return
	 */
	private DataSource getByteArrayBodyPart() {
		return new ByteArrayDataSource(fileBytes, disposition) {

			@Override
			public String getContentType() {
				return contentType;
			}
		};
	}

	/**
	 * Zwraca URLDataSource dla podabego URla okreslajacego zalacznik.
	 *
	 * @return
	 */
	private DataSource getURLBodyPart() {
		return new URLDataSource(url) {

			@Override
			public String getContentType() {
				return contentType;
			}
		};
	}

	/**
	 * Zwraca wielkosc zalacznika.
	 *
	 * @return
	 */
	public long getSize() {
		if (fileBytes != null) {
			return fileBytes.length;
		} else if (file != null) {
			return file.length();
		}
		return -1;
	}

	/**
	 * Zwraca nazwe zalacznika.
	 * Jezeli nazwa zalacznika jest nullem to jest generowana
	 *
	 * @return Nazwa zalacznika
	 */
	public String getFileName() {
		if (fileName == null) {
			generateFileInfo();
		}
		return fileName;
	}

	/**
	 * Pobiera rozszerzenie zalaczniaka
	 *
	 * @return rozszerzenie zalacznika
	 */
	public String getExtension() {
		if (extension == null) {
			generateFileInfo();
		}
		return extension;
	}

	/**
	 * Zwraca pelna nazwe zalacznika.
	 *
	 * @return the fullFilename
	 */
	public String getFullFileName() {
		if (fullFileName == null) {
			if (file != null) {
				fullFileName = file.getName();
			} else if (url != null) {
				fullFileName = url.getFile().substring(1);
			} else {
				fullFileName = getFileName() + "." + getExtension();
			}
		}
		return fullFileName;
	}

	/**
	 * Generuje informacje w opraciu na plik zalacznika albo jego pelna nazwe.
	 * Jezeli plik jest nullem to pelna nazwa pliku bedzie wykorzystana. 
	 * Jezeli pelna nazwa pliku jest nullem to nic nie zostanie ustawione.
	 */
	public void generateFileInfo() {
		String name;
		if (file != null) {
			name = file.getName();
		} 
		else if (fullFileName != null) {
			name = fullFileName;
		} 
		else {
			return;
		}
		int dot = name.lastIndexOf(".");
		fileName = name.substring(0, dot);
		extension = name.substring(dot + 1);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}

	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}

	public MimeBodyPart getBodyPart() {
		return bodyPart;
	}

	public void setBodyPart(MimeBodyPart bodyPart) {
		this.bodyPart = bodyPart;
	}

	public String getAttachmentName() {
		if (attachmentName == null) {
			attachmentName = getFullFileName();
		}
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
}
