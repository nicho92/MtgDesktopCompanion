package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.List;

import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractMagicPricesProvider extends AbstractMTGPlugin implements MTGPricesProvider {

	@Override
	public PLUGINS getType() {
		return PLUGINS.PRICER;
	}

	public AbstractMagicPricesProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "pricers");
		if (!confdir.exists())
			confdir.mkdir();

		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	
	@Override
	public void alertDetected(List<MagicPrice> p) {
		// do nothing

	}

}
