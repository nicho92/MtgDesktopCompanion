package org.magic.api.notifiers.impl;

import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

import dorkbox.notify.Notify;

public class DorkBox extends AbstractMTGNotifier {

	@Override
	public void send(MTGNotification notification) throws IOException {
		Notify n = Notify.create()
	      .title(notification.getTitle())
	      .text(notification.getMessage());



	    if(getBoolean("DARK"))
	    	n.darkStyle();

		switch (notification.getType())
		{
			case WARNING : n.showWarning();break;
			case ERROR : n.showError();break;
			case INFO : n.showInformation();break;
			case NONE: break;
			default:n.showInformation();break;
		}


	}

	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.TEXT;
	}

	@Override
	public String getName() {
		return "DorkBox";
	}

	@Override
	public String getVersion() {
		return Notify.getVersion();
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("DARK", "true");
	}
}
