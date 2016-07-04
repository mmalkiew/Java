package pl.net.amg.smartcity.mail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import pl.com.bull.cmn.utils.DateUtils;
import pl.net.amg.smartcity.card.business.CardServiceBean;
import pl.net.amg.smartcity.card.domain.PCDSmartcityCard;
import pl.net.amg.smartcity.card.enums.CardStatusEnum;
import pl.net.amg.smartcity.common.utils.CommonUtils;
import pl.net.amg.smartcity.configuration.business.ConfigurationService;
import pl.net.amg.smartcity.configuration.domain.CNFNotificationParameters;
import pl.net.amg.smartcity.configuration.domain.CNFNotificationSendingResult;
import pl.net.amg.smartcity.configuration.domain.CNFNotifications;
import pl.net.amg.smartcity.configuration.enums.NotificationIdEnum;
import pl.net.amg.smartcity.customer.business.CustomerHelper;
import pl.net.amg.smartcity.customer.domain.CSTCustomerAccount;
import pl.net.amg.smartcity.customer.domain.CSTCustomerPerson;
import pl.net.amg.smartcity.customer.domain.CSTCustomerRequest;
import pl.net.amg.smartcity.customer.enums.CustomerStateEnum;
import pl.net.amg.smartcity.customer.enums.MailSendingStatusEnum;
import pl.net.amg.smartcity.customer.enums.ReceiveTypeEnum;
import pl.net.amg.smartcity.mail.cdm.CDMMailTransactionData;
import pl.net.amg.smartcity.salespoint.domain.SPOSalesPoint;
import pl.net.amg.smartcity.transaction.domain.TRNTransaction;
import pl.net.amg.smartcity.transaction.domain.TRNTransactionLine;
import pl.net.amg.smartcity.transaction.enums.TransactionClassEnum;

@Stateless(name = "MailService")
public class MailServiceBean implements MailService {

	private static final Logger log = Logger.getLogger(CardServiceBean.class);

	@PersistenceContext(unitName = "smartcity")
	EntityManager smartcity;

	@EJB
	ConfigurationService configurationService;


	/**
	 * Metoda ogolna do wyslania mail-a z powiadomieniem o wybranym id
	 * i lista argumentow
	 * @param email
	 * @param notificationId
	 * @param arguments
	 */
	private void sendNotificationEmail(String email, NotificationIdEnum notificationId, Map<String, String> arguments) {
		try {
			if (email != null) {
				CNFNotifications notification = configurationService.getNotification(notificationId);
				String description = EmailHelper.format(notification.getDescription(), arguments);
				Email emailMessage = new Email(email, notification.getName(), description);

				EmailHelper.sendMail(emailMessage);
			}
		} catch (Exception e) {
			log.error("[sendNotificationEmail] Wystapil problem z wysylka powiadomienia: " + e.getMessage());
			e.printStackTrace();
		}
	}


	@Override
	public void sendCardReadyNotification(PCDSmartcityCard dbCard) {
		CSTCustomerPerson person = dbCard.getPerson();
		CSTCustomerAccount account = person.getAccount();
		SPOSalesPoint point = dbCard.getSalesPoint();
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("receivePoint", point.getAddress());
		sendNotificationEmail(account.getEmail(), NotificationIdEnum.CARD_RECEPTION_READY, arguments);

	}


	public void sendRejectNotification(String email, String reason) {
			Map<String, String> arguments = new HashMap<String, String>();
			arguments.put("rejectDesc", reason);
			sendNotificationEmail(email, NotificationIdEnum.REQUEST_REJECTION, arguments);
		
	}
	
	

	public void sendNewCustomerNotification(CSTCustomerRequest request, String firstPassword) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("accountNumber", request.getPerson().getAccount().getId());
		arguments.put("password", firstPassword);
		sendNotificationEmail(request.getEmail(), NotificationIdEnum.CUSTOMER_AND_ACCOUNT_CREATED, arguments);

	}

	@Override
	public void sendTicketPurchaseNotification(CSTCustomerRequest request) {
		Map<String, String> arguments = new HashMap<String, String>();
			if (ReceiveTypeEnum.POSTAL_RECEIVE.name().equals(request.getReceiveType())) {
				CNFNotificationParameters notParams = configurationService.getNotificationParameters(new Date());
				arguments.put("ticketActivationAway", "" + notParams.getTicketActivationAway());
				sendNotificationEmail(request.getEmail(), NotificationIdEnum.REGISTRATION_WITH_TICKET_PURCHASE,
						arguments);
			} else {
				CNFNotificationParameters notParams = configurationService.getNotificationParameters(new Date());
				arguments.put("ticketActivationPOK", "" + notParams.getTicketActivationPOK());
				sendNotificationEmail(request.getEmail(), NotificationIdEnum.TICKET_PURCHASE, arguments);
			}
	}

	public void sendEmailConfirmationNotification(String email, String uuid) {

		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("activationUrl", configurationService.getESBParameters(new Date())
				.getEmailAddresConfirmationUrl());
		arguments.put("registrationId", uuid);
		sendNotificationEmail(email, NotificationIdEnum.EMAIL_CONFIRMATION, arguments);
	}
	
	@Override
	public void sendChangeEmailNotification(String token, String emailAddress) {
		Map<String, String> arguments = new HashMap<String, String>();
        arguments.put("activationUrl", configurationService.getESBParameters(new Date()).getEmailAddresConfirmationUrl());
        arguments.put("registrationId", token);

        sendNotificationEmail(emailAddress, NotificationIdEnum.CHANGED_EMAIL_CONFIRMATION, arguments);

	}
	
	public boolean sendEndOfClientGroupValidityNotification(String email,  Date expirationDate){	
		boolean messagedSended = true;
        try {
            String expirationDateFormatted = DateUtils.formatDate(expirationDate);
            Map<String, String> arguments = new HashMap<String, String>();
    		arguments.put("notificationPeriod", expirationDateFormatted);	
    		sendNotificationEmail(email, NotificationIdEnum.END_OF_CLIENT_GROUP_VALIDITY_MESSAGE, arguments);

        } catch (Exception ex) {
            messagedSended = false;
        }

        return messagedSended;
	}
	
	  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	    public Long sendPeriodTicketEndNotificationToCustomers(Long loid,
	            Calendar calendarNotificationNumDays) throws Exception {
	        Long lastLoid = null;
	        log.info("[notificationPeriodTicketEndTask] loid: " + loid);
	        List<TRNTransactionLine> lines = getCardProductsToNotificationIterable(loid, 100, calendarNotificationNumDays);
	        if (lines.size() > 0) {
	            lastLoid = lines.get(lines.size() - 1).getLoid();
	        }       
	        log.info("[notificationPeriodTicketEndTask] lastLoid: " + lastLoid);
	        lines.stream().filter((line) -> !(line.getParent() == null
	                || line.getParent().getTransactionCard() == null
	                || line.getParent().getTransactionCard().getPerson() == null)).forEach((line) -> {
	                	
		            PCDSmartcityCard card = line.getParent().getTransactionCard();
		            CSTCustomerPerson person = card.getPerson();
		            Date validityDate = line.getBoughtProduct().getValidationTime();
		            /*
		             * Sprawdzamy czy uzytkwonik nadal chce otrzymywac powiadomienie
		             * o konczacym sie okresie waznosci biletu okresowego
		             */
		            if (Boolean.TRUE.equals(person.getServiceNotification())) {
	                    try {
	                        log.info("Klient: " + person.getName() + " "
	                                + person.getSurname());
	                        log.info("Aktywna karta: " + card.getCardId());
	                        log.info("Bilet: "
	                                + line.getProduct().getProductType().getDescription());
	                        log.info(line.getProduct().getAttributes().getDescription());
	                        log.info(line.getProduct().getZoneGroup().getDescription());
	                        log.info(line.getProduct().getTariff().getOperator().getName());
	                        log.info(line.getBoughtProduct().getActivationDate());
	                        log.info(validityDate);
	
	                        boolean messageSended = sendEndOfPeriodTicketValidityNotification(person
	                                        .getAccount().getEmail(), line.getProduct()
	                                        .getProductType().getDescription(), line.getProduct()
	                                        .getAttributes().getDescription(), line.getProduct()
	                                        .getZoneGroup().getDescription(), line.getProduct()
	                                .getTariff().getOperator().getName(),
	                                line.getBoughtProduct().getActivationDate(), validityDate);
	                        if (messageSended) {
	                            line.getBoughtProduct().setSendNotificationFlag(true);
	                            // wyslano powiadomienie o koncu waznosci danego biletu
	                        } else {
	                            line.getBoughtProduct().setSendNotificationFlag(false); 
	                            // nie wyslano powiadomienia o koncu waznosci danego biletu
	                        }

	                    } catch (Exception e) {
	                        log.error(e, e);
	                    }
		            }
	            });

	        return lastLoid;
	    }


	    private List<TRNTransactionLine> getCardProductsToNotificationIterable(Long loid, int max, Calendar calendarNotificationNumDays) {

	        List<TRNTransactionLine> result = new ArrayList<TRNTransactionLine>();
	        String queryStr0 = "select line from TRNTransactionLine line join line.boughtProduct as bp "
	                + " where line.parent.transactionCard.status = :cardStatus and line.parent.transactionStatus IN('CONFIRMED','INVOICED') and line.parent.reclamation is null and "
	                + " line.parent.transactionCard.person is not null and line.parent.transactionCard.person.state= :customerState and "
	                + " line.parent.transactionCard.person.account is not null and line.parent.transactionCard.person.account.email is not null and "
	                + " bp.validationTime <= :stopDate and bp.validationTime >= :startDate "
	                + " and (bp.sendNotificationFlag is NULL or bp.sendNotificationFlag = 'false') and line.loid > :loid "
	                + " order by line.loid";

	        Query query0 = smartcity.createQuery(queryStr0).setMaxResults(max);
	        query0.setParameter("cardStatus", CardStatusEnum.ACTIVE_CARD);
	        query0.setParameter("customerState", CustomerStateEnum.ACTIVE.name());
	        query0.setParameter("loid", loid);
	        query0.setParameter("startDate", new Date());
	        query0.setParameter("stopDate", calendarNotificationNumDays.getTime());
	        try {
	            log.info("[getCardProductsToNotificationIterable] - start");
	            result = query0.getResultList();
	            log.info("[getCardProductsToNotificationIterable] - stop");

	        } catch (Exception ex) {
	            log.error(ex.getMessage());
	        }

	        return result;
	    }

	
	    
	private boolean sendEndOfPeriodTicketValidityNotification(String email, String typeDescription,
			String attributeDescription, String zoneGroupDescription, String operatorName, Date activationDate,
			Date expirationDate) {
		boolean messagedSended = true;
		try {
			String expirationDateFormatted = DateUtils.formatDate(expirationDate);
			String activationDateFormatted = DateUtils.formatDate(activationDate);
			Map<String, String> arguments = new HashMap<String, String>();
			arguments.put("notificationPeriod", expirationDateFormatted);
			arguments.put("activationDate", activationDateFormatted);
			arguments.put("typeDescription", typeDescription);
			arguments.put("attributeDescription", attributeDescription);
			arguments.put("zoneGroupDescription", zoneGroupDescription);
			arguments.put("operatorName", operatorName);
			sendNotificationEmail(email, NotificationIdEnum.END_OF_PERIOD_TICKET_VALIDITY_MESSAGE, arguments);
		} catch (Exception ex) {
			messagedSended = false;
		}
		return messagedSended;
	}
	
	/* (non-Javadoc)
	 * @see pl.net.amg.smartcity.mail.MailService#sendResetPasswordNotification(java.lang.String, java.lang.String)
	 */
	@Override
	public void sendResetPasswordNotification(String token, String emailAddress) {
		Map<String, String> arguments = new HashMap<>();
		arguments.put("reset.password.link", configurationService.getESBParameters(new Date()).getResetPasswordUrl());
		arguments.put("reset.password.token", token);

		sendNotificationEmail(emailAddress, NotificationIdEnum.RESET_PASSWORD, arguments);
	}
	
	 /**
     * Wyslij maila o wykonanej transakcji zakupu biletów notificationId: 1.
     * Powodzenie transakacji zakupu biletów 2. Niepowodzenie transakcji zakupu
     * biletów 3. Anulowanie transakcji zakupu biletów
     */
	private void sendExternalTransactionNotifications(List<TRNTransaction> salesTransactionList,
			NotificationIdEnum notificationId) throws Exception {
		try {
			List<CDMMailTransactionData> reqList = new ArrayList<CDMMailTransactionData>();
			salesTransactionList
					.stream()
					.filter(t -> t.getTransactionLines() != null && !t.getTransactionLines().isEmpty()
							&& t.getTransactionLines().size() > 0 && t.getPerson() != null
							&& t.getPerson().getAccount() != null && t.getPerson().getAccount().getEmail() != null)
					.forEach(
							(salesTransaction) -> {

								List<CDMMailTransactionData> mailDataList = new ArrayList<CDMMailTransactionData>();

								String email = salesTransaction.getPerson().getAccount().getEmail();
								Long accountLoid = salesTransaction.getPerson().getAccount().getLoid();
								String transactionValue = salesTransaction.getGrossAmount().toString();
								String transactionStatus = CommonUtils.getTransactionStatusDescription(salesTransaction
										.getTransactionStatus());
								String transactionClass = salesTransaction.getTransactionClass();

								salesTransaction.getTransactionLines()
										.forEach(
												tl -> {
													CDMMailTransactionData mailData = new CDMMailTransactionData();
													mailData.setEmail(email);
													mailData.setTransactionValue(transactionValue);
													mailData.setTransactionStatus(transactionStatus);
													mailData.setAccountLoid(accountLoid);
													mailData.setTransactionClass(transactionClass);

													String typeDescription = "";
													String attributeDescription = "";
													String zoneGroupDescription = "";
													String operatorName = "";
													String activationDate = "";
													String expirationDate = "";
													Integer loadPoints = null;

													if (TransactionClassEnum.Sales.name().equals(transactionClass)) {

														typeDescription = tl.getProduct().getProductType()
																.getDescription();
														attributeDescription = tl.getProduct().getAttributes()
																.getDescription();
														zoneGroupDescription = tl.getProduct().getZoneGroup()
																.getDescription();
														activationDate = DateUtils.formatDate(tl.getBoughtProduct()
																.getActivationDate());
														expirationDate = DateUtils.formatDate(tl.getBoughtProduct()
																.getValidationTime());
														operatorName = tl.getOperator().getName();

														mailData.setTypeDescription(typeDescription);
														mailData.setAttributeDescription(attributeDescription);
														mailData.setZoneGroupDescription(zoneGroupDescription);
														mailData.setActivationDate(activationDate);
														mailData.setExpirationDate(expirationDate);
														mailData.setOperatorName(operatorName);
														mailDataList.add(mailData);

													} else if (TransactionClassEnum.LoadPoints.name().equals(
															transactionClass)) {
														loadPoints = tl.getLineSumm().intValue();
														operatorName = tl.getOperator().getName();

														mailData.setLoadPoints(loadPoints);
														mailData.setOperatorName(operatorName);
														mailDataList.add(mailData);
													}

												});

								if (mailDataList.size() > 0) {
									reqList.addAll(mailDataList);
								}

							});

			if (reqList != null && !reqList.isEmpty() && reqList.size() > 0) {
				notifyExternalTransaction(reqList, notificationId);
			} else {
				log.error("[sendSalesTransactionNotification]: " + "Empty transaction list");
			}
		} catch (Exception ex) {

		}
	}
  

	private void notifyExternalTransaction(List<CDMMailTransactionData> reqList, NotificationIdEnum notificationId) {
		boolean mailSent = true;
		Long account = reqList.get(0).getAccountLoid();
		try {
			String email = reqList.get(0).getEmail();
			if(email == null){
				return;
			}
			CNFNotifications notification = configurationService.getNotification(notificationId);
			String description = notification.getDescription();

			String text = "";
			int startIndex = 0;
			int stopIndex = description.length();

			int salesStartIndex = 0;
			int salesStopIndex = description.length();

			int loadPointsStartIndex = 0;
			int loadPointsStopIndex = description.length();

			String start = "$(".concat(CommonUtils.getProperty("start.eMailSender")).concat(")");
			String stop = "$(".concat(CommonUtils.getProperty("stop.eMailSender")).concat(")");
			String salesStart = "$(".concat(CommonUtils.getProperty("salesStart.eMailSender")).concat(")");
			String salesStop = "$(".concat(CommonUtils.getProperty("salesStop.eMailSender")).concat(")");
			String loadPointsStart = "$(".concat(CommonUtils.getProperty("loadPointsStart.eMailSender")).concat(")");
			String loadPointsStop = "$(".concat(CommonUtils.getProperty("loadPointsStop.eMailSender")).concat(")");
			String counter = CommonUtils.getProperty("counter.eMailSender");

			startIndex = description.indexOf(start);
			stopIndex = description.indexOf(stop);
			salesStartIndex = description.indexOf(salesStart);
			salesStopIndex = description.indexOf(salesStop);
			loadPointsStartIndex = description.indexOf(loadPointsStart);
			loadPointsStopIndex = description.indexOf(loadPointsStop);

			String inLoopTextPrefix = description.substring(startIndex + start.length(), salesStartIndex);
			String inLoopTextPostfix = description.substring(loadPointsStopIndex + loadPointsStop.length(),
					stopIndex);
			for (int i = 0; i < reqList.size(); i++) {
				Map<String, String> arguments = new HashMap<String, String>();
				arguments.put(counter, String.valueOf(i + 1));

				arguments.put("transactionValue", reqList.get(i).getTransactionValue()); 
				arguments.put("transactionStatus", reqList.get(i).getTransactionStatus());
				arguments.put("operatorName", reqList.get(i).getOperatorName()); 
				text = text.concat(EmailHelper.format(inLoopTextPrefix, arguments));
				if (TransactionClassEnum.Sales.name().equals(reqList.get(i).getTransactionClass())) {
					String inLoopSalesText = description.substring(salesStartIndex + salesStart.length(),
							salesStopIndex);
					arguments.put("notificationPeriod", reqList.get(i).getExpirationDate()); 
					arguments.put("typeDescription", reqList.get(i).getTypeDescription()); 
					arguments.put("attributeDescription", reqList.get(i).getAttributeDescription()); 
					arguments.put("zoneGroupDescription", reqList.get(i).getZoneGroupDescription()); 
					arguments.put("activationDate", reqList.get(i).getActivationDate()); 

					text = text.concat(EmailHelper.format(inLoopSalesText, arguments));
				} else if (TransactionClassEnum.LoadPoints.name().equals(reqList.get(i).getTransactionClass())) {
					String inLoopLoadPointsText = description.substring(
							loadPointsStartIndex + loadPointsStart.length(), loadPointsStopIndex);
					arguments.put("loadPoints", reqList.get(i).getLoadPoints().toString()); 
					text = text.concat(EmailHelper.format(inLoopLoadPointsText, arguments));
				}
				text = text.concat(EmailHelper.format(inLoopTextPostfix, arguments));
			}

			String beforeLoopText = description.substring(0, startIndex);
			String afterLoopText = description.substring(stopIndex + stop.length());

			String content = beforeLoopText.concat(text).concat(afterLoopText); 

			Email emailMessage = new Email(email, notification.getName(), content);
			EmailHelper.sendMail(emailMessage);

		} catch (Exception exc) {
			log.error(exc.getMessage());
			mailSent = false;
		}
		saveNotificationSendingResult(account, notificationId, mailSent);
	}
	
	private CNFNotificationSendingResult saveNotificationSendingResult(Long accountLoid, NotificationIdEnum notificationId, boolean sendingFlag){
		CNFNotificationSendingResult result = new CNFNotificationSendingResult();
		CSTCustomerAccount account = smartcity.find(CSTCustomerAccount.class, accountLoid);
		result.setAccount(account);
		result.setEmail(account.getEmail());
		result.setDate(new Date());
		result.setNotificationId(notificationId);
		MailSendingStatusEnum status = MailSendingStatusEnum.NOT_SENT;
		if(sendingFlag){
			status = MailSendingStatusEnum.SENT;
		}
		result.setStatus(status);
		
		result = smartcity.merge(result);
		return result;
	}
      
	
	public void sendExternalTransactionSuccess(List<TRNTransaction> transactions) throws Exception{	
		sendExternalTransactionNotifications(transactions, NotificationIdEnum.EXTERNAL_TRANSACTION_SUCCESS);
	}
	
	public void sendExternalTransactionFailed(List<TRNTransaction> transactions) throws Exception{
		sendExternalTransactionNotifications(transactions, NotificationIdEnum.EXTERNAL_TRANSACTION_FAILED);
	}
	
	public void sendExternalTransactionCancelled(List<TRNTransaction> transactions) throws Exception{
		sendExternalTransactionNotifications(transactions, NotificationIdEnum.EXTERNAL_TRANSACTION_CANCELLED);
	}
	
	
	/* (non-Javadoc)
	 * @see pl.net.amg.smartcity.mail.MailService#sendNewApplicationAndAccountNotification(pl.net.amg.smartcity.customer.domain.CSTCustomerRequest, java.lang.String)
	 */
	@Override
	public void sendNewApplicationAndAccountNotification(CSTCustomerRequest request, String firstPassword) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("PIN", request.getPinNumber());
		arguments.put("accountNumber", request.getPerson().getAccount().getId());
		arguments.put("password", firstPassword);
		sendNotificationEmail(request.getEmail(), NotificationIdEnum.APPLICATION_AND_ACCOUNT_CREATED, arguments);

	}
	

	/* (non-Javadoc)
	 * @see pl.net.amg.smartcity.mail.MailService#sendNewApplicationNotification(pl.net.amg.smartcity.customer.domain.CSTCustomerRequest)
	 */
	@Override
	public void sendNewApplicationNotification(CSTCustomerRequest request) {
		sendNotificationEmail(request.getEmail(), NotificationIdEnum.APPLICATION_CREATED, new HashMap<String, String>());

	}

	@Override
	public void sendCardSentNotification(PCDSmartcityCard dbCard ) {
		CSTCustomerPerson person = dbCard.getPerson();
		CSTCustomerAccount account = person.getAccount();
		Map<String, String> arguments = new HashMap<String, String>();

		String address = CustomerHelper.convertCSTMailAddressToString(person.getMailAddress());
		arguments.put("address", address);
		CNFNotificationParameters notParams = configurationService.getNotificationParameters(new Date());
		arguments.put("sendTime", "" + notParams.getAwaitPeriod());
		sendNotificationEmail(account.getEmail(), NotificationIdEnum.CARD_SENT, arguments);

	}


	@Override
	public void sendCardWithExtraDepositNotification(String email, BigDecimal depositValue) {
		if(depositValue != null){
			Map<String, String> arguments = new HashMap<String, String>();
			arguments.put("costDuplicate", depositValue.toString());	
			sendNotificationEmail(email, NotificationIdEnum.CARD_WITH_EXTRA_DEPOSIT, arguments);
		}
	}


	@Override
	public void sendApplicationAuthorizationNotification(String email, String applicationName) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("applicationName", applicationName);	
		sendNotificationEmail(email, NotificationIdEnum.APPLICATION_AUTHORIZATION, arguments);
		
	}


	@Override
	public void sendApplicationRemovalNotification(String email, String applicationName) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("applicationName", applicationName);	
		sendNotificationEmail(email, NotificationIdEnum.APPLICATION_REMOVAL, arguments);
		
	}
	
}
