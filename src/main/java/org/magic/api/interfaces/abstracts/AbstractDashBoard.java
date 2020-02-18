package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.Packaging;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.CollectionEvaluator;
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
		try {
			evaluator = new CollectionEvaluator();
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
	
	@Override
	public String[] getDominanceFilters() {
		return new String[] { "" };
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public HistoryPrice<MagicCard> getPriceVariation(MagicCard mc, MagicEdition ed) throws IOException {
		HistoryPrice<MagicCard> var = getOnlinePricesVariation(mc, ed);
		
		if(MTGControler.getInstance().getCurrencyService().isEnable() && var.getCurrency()!=MTGControler.getInstance().getCurrencyService().getCurrentCurrency())
		{
			var.entrySet().forEach(e->e.setValue(MTGControler.getInstance().getCurrencyService().convertTo(var.getCurrency(), e.getValue())));
			var.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
				
		}
		return var;
	}
	
	@Override
	public HistoryPrice<Packaging> getPriceVariation(Packaging packaging) throws IOException {
		HistoryPrice<Packaging> var = getOnlinePricesVariation(packaging);
		
		if(MTGControler.getInstance().getCurrencyService().isEnable() && var.getCurrency()!=MTGControler.getInstance().getCurrencyService().getCurrentCurrency())
		{
			var.entrySet().forEach(e->e.setValue(MTGControler.getInstance().getCurrencyService().convertTo(var.getCurrency(), e.getValue())));
			var.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
				
		}
		return var;
	}

	@Override
	public synchronized EditionsShakers getShakesForEdition(MagicEdition edition) throws IOException {
		
		Date c = evaluator.getCacheDate(edition);
		Date d = new Date();
		
		logger.trace(edition + " cache : " + c);
		
		if(c==null || !DateUtils.isSameDay(c, d))
		{
			logger.debug(edition + " not in cache.Loading it");
			evaluator.initCache(edition,getOnlineShakesForEdition(edition));	
		}
		EditionsShakers ret = evaluator.loadFromCache(edition);
		
		
		convert(ret.getShakes());
		
		
		return ret;
	}


	public List<CardShake> getShakerFor(MagicFormat.FORMATS format)  throws IOException
	{
		List<CardShake> ret = getOnlineShakerFor(format);
		convert(ret);
		
		return ret;
		
		
	}
	
	
	protected abstract HistoryPrice<Packaging> getOnlinePricesVariation(Packaging packaging) throws IOException;
	protected abstract List<CardShake> getOnlineShakerFor(MagicFormat.FORMATS gameFormat) throws IOException;
	protected abstract EditionsShakers getOnlineShakesForEdition(MagicEdition ed) throws IOException;
	protected abstract HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc,MagicEdition ed) throws IOException;
	
	
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
