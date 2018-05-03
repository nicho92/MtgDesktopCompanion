package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MTGNotification;
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
	
	public void notify(String title, String msg) throws IOException
	{
		MTGNotification notif = new MTGNotification();
		notif.setMessage(msg);
		notif.setTitle(title);
		send(notif);
	}
	

	@Override
	public PLUGINS getType() {
		return PLUGINS.NOTIFIER;
	}

}
