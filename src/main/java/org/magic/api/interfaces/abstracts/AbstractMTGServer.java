package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGConstants;

public abstract class AbstractMTGServer extends AbstractMTGPlugin implements MTGServer {

	public AbstractMTGServer() {
		confdir = new File(MTGConstants.CONF_DIR, "servers");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.SERVER;
	}
	
}
