package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.services.MTGConstants;

public abstract class AbstractMTGNotifier extends AbstractMTGPlugin implements MTGNotifier {

	public AbstractMTGNotifier() {
		super();
		
		confdir = new File(MTGConstants.CONF_DIR, "notifiers");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	
	@Override
	public void send(String notification) throws IOException {
		send(new MTGNotification("",notification,MESSAGE_TYPE.INFO));
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.NOTIFIER;
	}

}
