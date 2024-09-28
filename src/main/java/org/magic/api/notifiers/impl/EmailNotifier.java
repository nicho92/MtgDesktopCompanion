package org.magic.api.notifiers.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;
import org.magic.services.MTGConstants;

public class EmailNotifier extends AbstractMTGNotifier{

	private static final String SMTP_PASS = "SMTP_PASS";
	private static final String SMTP_LOGIN = "SMTP_LOGIN";


	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.HTML;
	}

	@Override
	public String getName() {
		return MTGConstants.EMAIL_NOTIFIER_NAME;
	}

	@Override
	public boolean isExternal() {
		return true;
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of(
								"SMTP", new MTGProperty("smtp.server.com","the smtp server name or ip"),
								"PORT", MTGProperty.newIntegerProperty("25","the smtp port",25,-1),
								"FROM", new MTGProperty("me@server.com","the sender email."),
								"SEND_TO", new MTGProperty("you@server.com","the destination mail for notification"),
								SMTP_LOGIN, new MTGProperty("login","allowed user for the smtp server"),
								SMTP_PASS, new MTGProperty("password","password for the authentication"),
								"SSL", MTGProperty.newBooleanProperty("true","the smtp server use SSL"));
	}


	public void send(String sendMail, MTGNotification notification) throws IOException {

		if(StringUtils.isEmpty(sendMail))
		{
			logger.warn("No email filled");
			return;
		}

		HtmlEmail email;
		try {

			email = new HtmlEmail();
			email.setHtmlMsg("<html>"+notification.getMessage()+"</html>");
			email.setHostName(getString("SMTP"));
			email.setSmtpPort(getInt("PORT"));
			email.setAuthenticator(new DefaultAuthenticator(getString(SMTP_LOGIN), getString(SMTP_PASS)));
			email.setSSLOnConnect(getBoolean("SSL"));
			email.setFrom(getString("FROM"));
			email.setSubject(notification.getTitle());
			email.setTextMsg(notification.getMessage());
			email.addTo(sendMail);
			email.send();

		}catch(EmailException ex)
		{
			throw new IOException(ex);
		}

	}


	@Override
	public void send(MTGNotification notification) throws IOException {
		HtmlEmail email;
		try {

			email = new HtmlEmail();
			email.setHtmlMsg("<html>"+notification.getMessage()+"</html>");
			email.setHostName(getString("SMTP"));
			email.setSmtpPort(getInt("PORT"));
			email.setAuthenticator(new DefaultAuthenticator(getString(SMTP_LOGIN), getString(SMTP_PASS)));
			email.setSSLOnConnect(getBoolean("SSL"));
			email.setFrom(getString("FROM"));
			email.setSubject(notification.getTitle());
			email.setTextMsg(notification.getMessage());
			for(String to : getArray("SEND_TO"))
				email.addTo(to);
			email.send();

		}catch(EmailException ex)
		{
			throw new IOException(ex);
		}

	}


}
