package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGSealedProduct;
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

	protected AbstractDashBoard() {
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
	public Double getSuggestedPrice(MTGCard mc, boolean foil) {
		try {

			if(!foil) {
				EditionsShakers c = getShakesForEdition(mc.getEdition());
				return c.getShakeFor(mc,foil).getPrice().doubleValue();
			}
			else
			{
			return getPriceVariation(mc,foil).getLastValue();
			}

		} catch (NullPointerException _) {
			logger.debug("no card found for {} foil={}",mc,foil );
			return 0.0;
		} catch (IOException e) {
			logger.error(e);
			return 0.0;
		}

	}


	@Override
	public HistoryPrice<MTGCard> getPriceVariation(MTGCard mc, boolean foil) throws IOException {
		var varh = getOnlinePricesVariation(mc, foil);

		if(MTGControler.getInstance().getCurrencyService().isEnable() && varh.getCurrency()!=MTGControler.getInstance().getCurrencyService().getCurrentCurrency())
		{
			varh.entrySet().forEach(e->e.setValue(MTGControler.getInstance().getCurrencyService().convertTo(varh.getCurrency(), e.getValue())));
			varh.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());

		}
		return varh;
	}


	@Override
	public HistoryPrice<MTGEdition> getPriceVariation(MTGEdition ed) throws IOException {
		var varh = getOnlinePricesVariation(ed);

		if(MTGControler.getInstance().getCurrencyService().isEnable() && varh.getCurrency()!=MTGControler.getInstance().getCurrencyService().getCurrentCurrency())
		{
			varh.entrySet().forEach(e->e.setValue(MTGControler.getInstance().getCurrencyService().convertTo(varh.getCurrency(), e.getValue())));
			varh.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());

		}
		return varh;
	}


	@Override
	public HistoryPrice<MTGSealedProduct> getPriceVariation(MTGSealedProduct packaging) throws IOException {
		var varh = getOnlinePricesVariation(packaging);

		if(MTGControler.getInstance().getCurrencyService().isEnable() && varh.getCurrency()!=MTGControler.getInstance().getCurrencyService().getCurrentCurrency())
		{
			varh.entrySet().forEach(e->e.setValue(MTGControler.getInstance().getCurrencyService().convertTo(varh.getCurrency(), e.getValue())));
			varh.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());

		}
		return varh;
	}

	@Override
	public synchronized EditionsShakers getShakesForEdition(MTGEdition edition) throws IOException {

		var c = evaluator.getCacheDate(edition);
		var d = new Date();

		logger.trace("{} cache : {}",edition,c);

		if(c==null || !DateUtils.isSameDay(c, d))
		{
			logger.debug("{} not in cache.Loading it",edition);
			evaluator.initCache(edition,getOnlineShakesForEdition(edition));
		}
		EditionsShakers ret = evaluator.loadFromCache(edition);


		convert(ret.getShakes());


		return ret;
	}


	@Override
	public List<CardShake> getShakerFor(MTGFormat.FORMATS format)  throws IOException
	{
		List<CardShake> ret = getOnlineShakerFor(format);
		convert(ret);
		return ret;
	}


	protected abstract HistoryPrice<MTGSealedProduct> getOnlinePricesVariation(MTGSealedProduct packaging) throws IOException;
	protected abstract List<CardShake> getOnlineShakerFor(MTGFormat.FORMATS gameFormat) throws IOException;
	protected abstract EditionsShakers getOnlineShakesForEdition(MTGEdition ed) throws IOException;
	protected abstract HistoryPrice<MTGCard> getOnlinePricesVariation(MTGCard mc,boolean foil) throws IOException;
	protected abstract HistoryPrice<MTGEdition> getOnlinePricesVariation(MTGEdition ed) throws IOException;


	public static void convert(List<CardShake> ret)
	{
		ret.forEach(cs->{
					if(MTGControler.getInstance().getCurrencyService().isEnable() && cs.getCurrency()!=MTGControler.getInstance().getCurrencyService().getCurrentCurrency())
					{
						cs.setPrice(MTGControler.getInstance().getCurrencyService().convertTo(cs.getCurrency(), cs.getPrice().doubleValue()));
						cs.setPriceDayChange(MTGControler.getInstance().getCurrencyService().convertTo(cs.getCurrency(), cs.getPriceDayChange()));
						cs.setPriceWeekChange(MTGControler.getInstance().getCurrencyService().convertTo(cs.getCurrency(), cs.getPriceWeekChange()));
						cs.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
					}

		});

	}

}
