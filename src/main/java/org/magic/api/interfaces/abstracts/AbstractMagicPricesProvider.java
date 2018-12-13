package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

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
	
	protected abstract List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException;
	
	
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws IOException
	{
		return getLocalePrice(me, card)
								.stream()
								.map(p->{
											p.setValue(MTGControler.getInstance().getCurrencyService().convertTo(p.getCurrency(), p.getValue()));
											p.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
											return p;
										}
								).collect(Collectors.toList());
	}
	
	
	@Override
	public void alertDetected(List<MagicPrice> p) {
		// do nothing

	}

}
