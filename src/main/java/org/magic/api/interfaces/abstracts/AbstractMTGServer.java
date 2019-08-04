package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.ReportNotificationManager;

public abstract class AbstractMTGServer extends AbstractMTGPlugin implements MTGServer {

	protected ReportNotificationManager notifFormater;
	
	public AbstractMTGServer() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "servers");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
		
		notifFormater = new ReportNotificationManager();
		
	}

	
	@Override
	public PLUGINS getType() {
		return PLUGINS.SERVER;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
}
