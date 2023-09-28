package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.providers.MTGJsonPricerProvider;

public class MkmPricer extends AbstractPricesProvider {

	@Override
	public String getName() {
		return "Mkm";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {

		logger.debug("{} looking for prices for {} ",getName(),card);

		MTGJsonPricerProvider.getInstance().expirationDay(getInt("EXPIRE_FILE_DAYS"));

		return MTGJsonPricerProvider.getInstance().getPriceFor(card);
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MkmPricer.class.getResource("/icons/plugins/magiccardmarket.png"));
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("EXPIRE_FILE_DAYS","1");
	}


}
