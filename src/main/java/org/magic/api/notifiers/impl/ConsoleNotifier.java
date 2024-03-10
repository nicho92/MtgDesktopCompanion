package org.magic.api.notifiers.impl;

import java.io.IOException;

import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

public class ConsoleNotifier extends AbstractMTGNotifier {

	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.TEXT;
	}

	@Override
	public void send(MTGNotification notification) throws IOException {
		switch (notification.getType()) {
		case ERROR : logger.error(notification.getMessage());break;
		case INFO : logger.info(notification.getMessage());break;
		case WARNING : logger.warn(notification.getMessage());break;
		default : logger.debug(notification.getMessage());break;
		}
	}

	@Override
	public String getName() {
		return "Console";
	}
	
	@Override
	public String getVersion() {
		
		return org.apache.logging.log4j.core.Layout.class.getPackage().getImplementationVersion();
	}

}
