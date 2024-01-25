package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.sorters.MagicPricesComparator;
import org.magic.services.MTGControler;

public abstract class AbstractPricesProvider extends AbstractMTGPlugin implements MTGPricesProvider {

	@Override
	public PLUGINS getType() {
		return PLUGINS.PRICER;
	}

	protected abstract List<MTGPrice> getLocalePrice(MTGCard card) throws IOException;


	@Override
	public Map<String, List<MTGPrice>> getPricesBySeller(List<MTGCard> cards) throws IOException {
		Map<String, List<MTGPrice>> map = new HashMap<>();

		for(MTGCard mc : cards)
		{
			notify(mc);
			List<MTGPrice> prices = getPrice(mc);

			for(MTGPrice mp : prices)
				map.computeIfAbsent(mp.getSeller(),v->new ArrayList<>()).add(mp);
		}
		return map;
	}

	@Override
	public MTGPrice getBestPrice(MTGCard card) {
		try {
			return getPrice(card).stream().min(new MagicPricesComparator()).orElse(null);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}


	private List<MTGPrice> retrieveMap(Map<MTGCard,Integer> map)
	{
			List<MTGPrice> ret = new ArrayList<>();

			map.entrySet().forEach(e->{
				try {
					MTGPrice p = getPrice(e.getKey()).stream().min(new MagicPricesComparator()).orElse(null);
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
	public List<MTGPrice> getPrice(MTGDeck d,boolean side) throws IOException {

		List<MTGPrice> ret = new ArrayList<>();
		ret.addAll(retrieveMap(d.getMain()));

			if(side)
				ret.addAll(retrieveMap(d.getSideBoard()));


		return ret;
	}


	@Override
	public Double getSuggestedPrice(MTGCard mc, boolean foil) {
		try {
			return getPrice(mc).stream().filter(mp->mp.isFoil()==foil && mp.getMagicCard()==mc).min(new MagicPricesComparator()).orElse(new MTGPrice()).getValue();
		} catch (Exception e) {
			logger.error(e);
			return 0.0;
		}
	}


	@Override
	public List<MTGPrice> getPrice(MTGCard card) throws IOException
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
	public void alertDetected(List<MTGPrice> p) {
		// do nothing

	}

}
