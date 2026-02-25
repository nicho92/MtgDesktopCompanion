package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.providers.MTGJsonPricerProvider;
import org.magic.services.providers.MTGJsonPricerProvider.VENDOR;

public class MkmPricer extends AbstractPricesProvider {

	@Override
	public String getName() {
		return "Mkm";
	}

	@Override
	protected synchronized List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {

		logger.debug("{} looking for prices for {} ",getName(),card);

		MTGJsonPricerProvider.getInstance().expirationDay(getInt("EXPIRE_FILE_DAYS"));

		return MTGJsonPricerProvider.getInstance().getPriceFor(card,VENDOR.CARDMARKET);
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MkmPricer.class.getResource("/icons/plugins/magiccardmarket.png"));
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("EXPIRE_FILE_DAYS",MTGProperty.newIntegerProperty("1","Number of day when the file will be updated",1,-1));
	}


}
