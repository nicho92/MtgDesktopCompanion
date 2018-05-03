package org.magic.api.notifiers.impl;

import java.io.IOException;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.magic.api.beans.MTGNotification;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

public class EmailNotifier extends AbstractMTGNotifier{

	@Override
	public String getName() {
		return "Email";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty("SMTP", "smtp.server.com");
		setProperty("PORT", "25");
		setProperty("FROM", "me@server.com");
		setProperty("SEND_TO", "you@server.com");
		setProperty("SMTP_LOGIN", "login");
		setProperty("SMTP_PASS", "password");
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public void send(MTGNotification notification) throws IOException {
		
		try {
		Email email = new SimpleEmail();
			email.setHostName(getString("SMTP"));
			email.setSmtpPort(getInt("PORT"));
			email.setAuthenticator(new DefaultAuthenticator(getString("SMTP_LOGIN"), getString("SMTP_PASS")));
			email.setSSLOnConnect(true);
			email.setFrom(getString("FROM"));
			email.setSubject(notification.getTitle());
			email.setMsg(notification.getMessage());
			for(String to : getString("SEND_TO").split(","))
				email.addTo(to);
			email.send();
			
		}catch(EmailException ex)
		{
			throw new IOException(ex);
		}
		
	}

	
	
	
	
	
}
