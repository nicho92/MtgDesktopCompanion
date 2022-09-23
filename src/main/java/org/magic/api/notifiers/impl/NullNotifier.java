package org.magic.api.notifiers.impl;

import java.io.IOException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

public class NullNotifier extends AbstractMTGNotifier {
	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.TEXT;
	}

	@Override
	public void send(MTGNotification notification) throws IOException {
		// do nothing
	}

	@Override
	public String getName() {
		return "Null";
	}


}
