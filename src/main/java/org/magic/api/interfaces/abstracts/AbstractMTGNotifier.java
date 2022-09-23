package org.magic.api.interfaces.abstracts;

import java.io.IOException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGNotifier;

public abstract class AbstractMTGNotifier extends AbstractMTGPlugin implements MTGNotifier {

	@Override
	public void send(String notification) throws IOException {
		send(new MTGNotification("",notification,MESSAGE_TYPE.INFO));
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.NOTIFIER;
	}

	@Override
	public boolean isExternal() {
		return false;
	}

}
