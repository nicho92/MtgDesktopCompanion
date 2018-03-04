package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGShopper;
import org.magic.services.MTGConstants;

public abstract class AbstractMagicShopper extends AbstractMTGPlugin implements MTGShopper {
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.SHOPPER;
	}
	
	public AbstractMagicShopper() {
		confdir = new File(MTGConstants.CONF_DIR, "shoppers");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
}
