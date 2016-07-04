package pl.net.amg.smartcity.mail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import pl.net.amg.smartcity.card.domain.PCDSmartcityCard;
import pl.net.amg.smartcity.configuration.enums.NotificationIdEnum;
import pl.net.amg.smartcity.customer.domain.CSTCustomerRequest;
import pl.net.amg.smartcity.mail.cdm.CDMMailTransactionData;
import pl.net.amg.smartcity.transaction.domain.TRNTransaction;

@Local
public interface MailService {

	/**
	 * Karta gotowa do odbioru
	 * 
	 * @param dbCard
	 */
	void sendCardReadyNotification(PCDSmartcityCard dbCard);

	/**
	 * Odrzucenie Wniosku
	 * 
	 * @param email
	 * @param reason
	 */
	void sendRejectNotification(String email, String reason);

	/**
	 * Utworzenie Nowego Konta
	 * 
	 * @param request
	 * @param firstPassword
	 * @throws Exception
	 */
	void sendNewCustomerNotification(CSTCustomerRequest request, String firstPassword);

	/**
	 * Potwierdzenie rejestracji
	 * 
	 * @param request
	 */
	void sendTicketPurchaseNotification(CSTCustomerRequest request);

	/**
	 * Potwierdzenie adresu email
	 * 
	 * @param email
	 * @param uuid
	 */
	void sendEmailConfirmationNotification(String email, String uuid);

	/**
	 * Potwierdzenia zmiany adresu email
	 * 
	 * @param tokenValue
	 * @param emailAddress
	 */
	void sendChangeEmailNotification(String tokenValue, String emailAddress);

	/**
	 * Przypomnienie o koncu waznosci ulgi
	 * 
	 * @param email
	 * @param expirationDate
	 * @return
	 */
	boolean sendEndOfClientGroupValidityNotification(String email, Date expirationDate);

	/**
	 * Przypomnienie o końcu ważności biletu okresowego
	 * 
	 * @param email
	 */
	Long sendPeriodTicketEndNotificationToCustomers(Long loid, Calendar calendarNotificationNumDays) throws Exception;

	/**
	 * Generacja nowego hasła
	 * 
	 * @param token
	 * @param emailAddress
	 */
	void sendResetPasswordNotification(String token, String emailAddress);

	/**
	 * Powodzenie transakcji zakupu produktów
	 * 
	 * @param reqList
	 * @throws Exception 
	 */
	void sendExternalTransactionSuccess(List<TRNTransaction> transactions) throws Exception;

	/**
	 * Niepowodzenie transakcji zakupu produktów
	 * 
	 * @param reqList
	 * @throws Exception 
	 */
	void sendExternalTransactionFailed(List<TRNTransaction> transactions) throws Exception;

	/**
	 * Anulowanie transakcji zakupu produktów
	 * 
	 * @param reqList
	 * @throws Exception 
	 */
	void sendExternalTransactionCancelled(List<TRNTransaction> transactions) throws Exception;

	/**
	 * Utworzenie Nowego Konta oraz aktywacja aplikacji iMKA
	 * 
	 * @param request
	 * @param firstPassword
	 */
	void sendNewApplicationAndAccountNotification(CSTCustomerRequest request, String firstPassword);

	/**
	 * Aktywacja aplikacji iMKA
	 * 
	 * @param request
	 */
	void sendNewApplicationNotification(CSTCustomerRequest request);

	/**
	 * Karta przekazana do wysyłki
	 * 
	 * @param dbCard
	 */
	void sendCardSentNotification(PCDSmartcityCard dbCard);

	/**
	 * Karta z dodatkową opłatą
	 * 
	 * @param email
	 * @param depositValue
	 */
	void sendCardWithExtraDepositNotification(String email, BigDecimal depositValue);

	/**
	 * Dodanie aplikacji iMKA
	 * 
	 * @param email
	 * @param applicationName
	 */
	void sendApplicationAuthorizationNotification(String email, String applicationName);

	/**
	 * Usunięcie aplikacji iMKA
	 * 
	 * @param email
	 * @param applicationName
	 */
	void sendApplicationRemovalNotification(String email, String applicationName);
	


}
