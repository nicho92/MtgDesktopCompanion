package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractMagicPricesProvider extends AbstractMTGPlugin implements MTGPricesProvider {

	@Override
	public PLUGINS getType() {
		return PLUGINS.PRICER;
	}
	
	public AbstractMagicPricesProvider() {
		confdir = new File(MTGConstants.CONF_DIR, "pricers");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
}
