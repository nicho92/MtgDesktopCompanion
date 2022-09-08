package org.magic.console;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

public abstract class AbstractResponse {
	
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	
	
	@Override
	public String toString() {
		return show();
	}
	
	public abstract String show();

}
