package org.magic.api.notifiers.impl;

import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

import dorkbox.notify.Notify;
import dorkbox.notify.Theme;

public class DorkBox extends AbstractMTGNotifier {

	@Override
	public void send(MTGNotification notification) throws IOException {
		
		Notify n = Notify.Companion.create()
									.title(notification.getTitle())
									.text(notification.getMessage());



	    if(getBoolean("DARK"))
	    	n.setTheme(Theme.Companion.getDefaultDark());

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
		return Notify.version;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("DARK", "true");
	}
}
