package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.beans.enums.EnumMarketType;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.sorters.MagicPricesComparator;
import org.magic.services.MTGControler;

public abstract class AbstractPricesProvider extends AbstractMTGPlugin implements MTGPricesProvider {

	@Override
	public PLUGINS getType() {
		return PLUGINS.PRICER;
	}

	protected abstract List<MagicPrice> getLocalePrice(MagicCard card) throws IOException;


	@Override
	public Map<String, List<MagicPrice>> getPricesBySeller(List<MagicCard> cards) throws IOException {
		Map<String, List<MagicPrice>> map = new HashMap<>();

		for(MagicCard mc : cards)
		{
			notify(mc);
			List<MagicPrice> prices = getPrice(mc);

			for(MagicPrice mp : prices)
				map.computeIfAbsent(mp.getSeller(),v->new ArrayList<>()).add(mp);
		}
		return map;
	}


	@Override
	public EnumMarketType getMarket() {
		return EnumMarketType.US_MARKET;
	}


	@Override
	public MagicPrice getBestPrice(MagicCard card) {
		try {
			return getPrice(card).stream().min(new MagicPricesComparator()).orElse(null);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}


	private List<MagicPrice> retrieveMap(Map<MagicCard,Integer> map)
	{
			List<MagicPrice> ret = new ArrayList<>();

			map.entrySet().forEach(e->{
				try {
					MagicPrice p = getPrice(e.getKey()).stream().min(new MagicPricesComparator()).orElse(null);
					if(p!=null)
					{
						p.setMagicCard(e.getKey());
						p.setQty(e.getValue());
						p.setValue(p.getValue()*e.getValue());
						ret.add(p);
						notify(p);
					}
				} catch (IOException e1) {
					logger.error(e1);
				}
		});

		return ret;
	}

	@Override
	public List<MagicPrice> getPrice(MagicDeck d,boolean side) throws IOException {

		List<MagicPrice> ret = new ArrayList<>();
		ret.addAll(retrieveMap(d.getMain()));

			if(side)
				ret.addAll(retrieveMap(d.getSideBoard()));


		return ret;
	}


	@Override
	public Double getSuggestedPrice(MagicCard mc, boolean foil) {
		try {
			return getPrice(mc).stream().filter(mp->mp.isFoil()==foil && mp.getMagicCard()==mc).min(new MagicPricesComparator()).orElse(new MagicPrice()).getValue();
		} catch (Exception e) {
			logger.error(e);
			return 0.0;
		}
	}


	@Override
	public List<MagicPrice> getPrice(MagicCard card) throws IOException
	{
		return new ArrayList<>(getLocalePrice(card)
								.stream()
								.map(p->{
											if(MTGControler.getInstance().getCurrencyService().isEnable()) {
												p.setValue(MTGControler.getInstance().getCurrencyService().convertTo(p.getCurrency(), p.getValue()));
												p.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
												p.setMagicCard(card);
											}
											return p;
										}
								).toList());
	}


	@Override
	public void alertDetected(List<MagicPrice> p) {
		// do nothing

	}

}
