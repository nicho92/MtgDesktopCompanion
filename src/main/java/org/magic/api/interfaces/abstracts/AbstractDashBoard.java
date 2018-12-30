package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public abstract class AbstractDashBoard extends AbstractMTGPlugin implements MTGDashBoard {

	protected CollectionEvaluator evaluator;
	 
	@Override
	public Currency getCurrency() {
		return Currency.getInstance("USD");
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DASHBOARD;
	}

	public AbstractDashBoard() {
		super();
		try {
			evaluator = new CollectionEvaluator();
		} catch (IOException e) {
			logger.error(e);
		}
		confdir = new File(MTGConstants.CONF_DIR, "dashboards");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	
	@Override
	public String[] getDominanceFilters() {
		return new String[] { "" };
	}
	
	
	@Override
	public CardPriceVariations getPriceVariation(MagicCard mc, MagicEdition ed) throws IOException {
		CardPriceVariations var = getOnlinePricesVariation(mc, ed);
		
		if(MTGControler.getInstance().getCurrencyService().isEnable() && var.getCurrency()!=MTGControler.getInstance().getCurrencyService().getCurrentCurrency())
		{
			var.entrySet().forEach(e->e.setValue(MTGControler.getInstance().getCurrencyService().convertTo(var.getCurrency(), e.getValue())));
			var.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
				
		}
		return var;
	}
	
	
	@Override
	public List<CardShake> getShakesForEdition(MagicEdition edition) throws IOException {
		
		Date c = evaluator.getCacheDate(edition);
		Date d = new Date();
		
		logger.trace(edition + " cache : " + c);
		
		if(c==null || !DateUtils.isSameDay(c, d))
		{
			logger.debug(edition + " not in cache.Loading it");
			evaluator.initCache(edition,getOnlineShakesForEdition(edition));	
		}
		List<CardShake> ret = evaluator.loadFromCache(edition);
		
		
		convert(ret);
		
		
		return ret;
	}


	public List<CardShake> getShakerFor(MTGFormat format)  throws IOException
	{
		List<CardShake> ret = getOnlineShakerFor(format);
		convert(ret);
		
		return ret;
		
		
	}
	
	protected abstract List<CardShake> getOnlineShakerFor(MTGFormat gameFormat) throws IOException;
	protected abstract List<CardShake> getOnlineShakesForEdition(MagicEdition ed) throws IOException;
	protected abstract CardPriceVariations getOnlinePricesVariation(MagicCard mc,MagicEdition ed) throws IOException;
	
	
	public static void convert(List<CardShake> ret)
	{
		ret.forEach(cs->{
					if(MTGControler.getInstance().getCurrencyService().isEnable() && cs.getCurrency()!=MTGControler.getInstance().getCurrencyService().getCurrentCurrency())
					{
						cs.setPrice(MTGControler.getInstance().getCurrencyService().convertTo(cs.getCurrency(), cs.getPrice()));
						cs.setPriceDayChange(MTGControler.getInstance().getCurrencyService().convertTo(cs.getCurrency(), cs.getPriceDayChange()));
						cs.setPriceWeekChange(MTGControler.getInstance().getCurrencyService().convertTo(cs.getCurrency(), cs.getPriceWeekChange()));	
					}
			
		});
				
	}
	
}
