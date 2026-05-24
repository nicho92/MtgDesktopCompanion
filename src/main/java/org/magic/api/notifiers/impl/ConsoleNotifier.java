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
			case ERROR -> logger.error(notification.getMessage());
			case INFO -> logger.info(notification.getMessage());
			case WARNING -> logger.warn(notification.getMessage());
			default -> logger.debug(notification.getMessage());
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
