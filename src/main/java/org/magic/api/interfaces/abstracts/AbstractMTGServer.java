package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGServer;
import org.magic.services.ReportNotificationManager;

public abstract class AbstractMTGServer extends AbstractMTGPlugin implements MTGServer {

	protected ReportNotificationManager notifFormater;
	
	public AbstractMTGServer() {
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
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
