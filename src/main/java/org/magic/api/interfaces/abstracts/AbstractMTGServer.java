package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGConstants;

public abstract class AbstractMTGServer extends AbstractMTGPlugin implements MTGServer {

	protected NumberFormat formatter = new DecimalFormat("#0.00");  
	
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
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.SERVER;
	}

}
