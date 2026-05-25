package org.magic.api.notifiers.impl;

import dorkbox.notify.Notify;
import dorkbox.notify.Theme;
import java.io.IOException;
import java.util.Map;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

public class DorkBox extends AbstractMTGNotifier {

	@Override
	public void send(MTGNotification notification) throws IOException {

		Notify n = Notify.Companion.create().title(notification.getTitle()).text(notification.getMessage());

		if (getBoolean("DARK"))
			n.setTheme(Theme.Companion.getDefaultDark());

		switch (notification.getType()) {
			case WARNING -> n.showWarning();
			case ERROR -> n.showError();
			case INFO -> n.showInformation();
			case NONE -> {
				// do nothing
			}
			default -> n.showInformation();
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
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("DARK", MTGProperty.newBooleanProperty(FALSE, "use dark mode"));
	}
}
