package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.api.mtgstock.modele.CardSet;
import org.api.mtgstock.modele.FullPrint;
import org.api.mtgstock.modele.Interest;
import org.api.mtgstock.modele.Played;
import org.api.mtgstock.modele.Print;
import org.api.mtgstock.modele.SealedProduct;
import org.api.mtgstock.modele.SearchResult;
import org.api.mtgstock.modele.URLCallInfo;
import org.api.mtgstock.services.AnalyticsService;
import org.api.mtgstock.services.CardsService;
import org.api.mtgstock.services.InterestsService;
import org.api.mtgstock.services.PriceService;
import org.api.mtgstock.services.URLCallListener;
import org.api.mtgstock.tools.MTGStockConstants;
import org.api.mtgstock.tools.MTGStockConstants.FORMAT;
import org.api.mtgstock.tools.MTGStockConstants.PRICES;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.beans.enums.EnumMarketType;
import org.magic.api.beans.enums.MTGCardVariation;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.TechnicalServiceManager;

public class MTGStockDashBoard extends AbstractDashBoard {

	private static final String GET_FOIL = "GET_FOIL";
	private static final String PRICE_VALUE = "AVERAGE_MARKET";
	private static final String INTEREST_TYPE = "INTEREST_TYPE";
	private CardsService cardService;
	private InterestsService interestService;
	private PriceService pricesService;
	private AnalyticsService analyticService;
	
	@Override
	public EnumMarketType getMarket() {
		return EnumMarketType.US_MARKET;
	}
	
	@Override
	public String getVersion() {
		return MTGStockConstants.VERSION;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	public MTGStockDashBoard() {
		cardService = new CardsService();
		interestService = new InterestsService();
		pricesService = new PriceService();
		analyticService = new AnalyticsService();
		
		URLCallListener urlNotifier = (URLCallInfo callInfo)->{
				
				var netinfo = new NetworkInfo();
				netinfo.setStart(callInfo.getStart());				
				netinfo.setEnd(callInfo.getEnd());
				netinfo.setRequest(callInfo.getRequest());
				netinfo.setReponse(callInfo.getResponse());

				TechnicalServiceManager.inst().store(netinfo);
		};
		
		
		cardService.setListener(urlNotifier);
		interestService.setListener(urlNotifier);
		pricesService.setListener(urlNotifier);
		analyticService.setListener(urlNotifier);
		
	}
	

	@Override
	public List<CardDominance> getBestCards(FORMATS f, String filter) throws IOException {
		List<CardDominance> ret = new ArrayList<>();
		
		var i=1;
		for(Played p : analyticService.getMostPlayedCard(FORMAT.valueOf(f.name()))) {
			var cd = new CardDominance();
			cd.setCardName(p.getName());
			cd.setPlayers(p.getQuantity());
			cd.setPosition(i++);			
			ret.add(cd);
		}
		return ret;
	}

	private SealedProduct guess(List<SealedProduct> products,MTGSealedProduct packaging)
	{
		List<SealedProduct> ret = new ArrayList<>();

		switch(packaging.getTypeProduct())
		{
			case BOOSTER: ret.addAll(products.stream().filter(SealedProduct::isBooster).toList()); break;
			case BOX: 	  ret.addAll(products.stream().filter(cs->cs.isBox() && !cs.isCase()).toList()); break;
			case BUNDLE:  ret.addAll(products.stream().filter(SealedProduct::isBundle).toList()); break;
			case FATPACK: ret.addAll(products.stream().filter(SealedProduct::isFatPack).toList()); break;
			case STARTER: ret.addAll(products.stream().filter(SealedProduct::isStarter).toList()); break;
			case PRERELEASEPACK:ret.addAll(products.stream().filter(SealedProduct::isPrerelease).toList()); break;
			case CONSTRUCTPACK:ret.addAll(products.stream().filter(sp-> sp.isIntroPack()|| sp.isPlaneswalkerDeck()).toList()); break;
			case CHALLENGERDECK : ret.addAll(products.stream().filter(SealedProduct::isChallengerDeck).toList()); break;
			default:break;
		}
		
		logger.debug("found " + ret);
		
		if(packaging.getExtra()!=null)
		{
			switch(packaging.getExtra())
			{
				case COLLECTOR: return ret.stream().filter(SealedProduct::isCollector).findFirst().orElse(ret.get(0));
				case DRAFT: 	return ret.stream().filter(SealedProduct::isDraft).findFirst().orElse(ret.get(0));
				case GIFT:		return ret.stream().filter(SealedProduct::isGift).findFirst().orElse(ret.get(0));
				case SET:		return ret.stream().filter(SealedProduct::isSet).findFirst().orElse(ret.get(0));
				case THEME:		return ret.stream().filter(SealedProduct::isTheme).findFirst().orElse(ret.get(0));
				case VIP:		return ret.stream().filter(SealedProduct::isVIP).findFirst().orElse(ret.get(0));
				default:		return ret.stream().filter(t->!t.isCollector() && !t.isDraft() && !t.isGift() && !t.isSet() && !t.isTheme() && !t.isVIP()).findFirst().orElse(ret.get(0));
			}
		}
		
		return ret.stream().filter(t->!t.isCollector() && !t.isDraft() && !t.isGift() && !t.isSet() && !t.isTheme() && !t.isVIP()).findFirst().orElse(ret.get(0));
		
		
	}
	
	
	@Override
	protected HistoryPrice<MTGSealedProduct> getOnlinePricesVariation(MTGSealedProduct packaging) throws IOException {
		
		HistoryPrice<MTGSealedProduct> ret = new HistoryPrice<>(packaging);
		CardSet cs = cardService.getSetByCode(packaging.getEdition().getId());
		var product = guess(cardService.getSealedProduct(cs),packaging);
		PRICES p =  PRICES.valueOf(getString(PRICE_VALUE).toUpperCase());
		
		if(product!=null)
		{
			ret.setFoil(false);
			ret.setCurrency(getCurrency());
			new PriceService().getSealedPrices(product).getPrices().get(p).forEach(c->ret.put(c.getKey(), c.getValue()));
		}
		
		
		return ret;
	}

	@Override
	protected List<CardShake> getOnlineShakerFor(FORMATS f) throws IOException {
		List<CardShake> ret = new ArrayList<>();
		
		FORMAT mtgstockformat = null;
		
		if(f!=null)
			mtgstockformat = FORMAT.valueOf(f.name());
		
		
		
		var p=PRICES.valueOf(getString(PRICE_VALUE).toUpperCase());
		
		
		logger.debug("Parsing shakers for " + f +" "+ p);
		
		List<Interest> st;
		
		PRICES c =null;
		
		if(p.equals(PRICES.MARKET)||p.equals(PRICES.MARKET_FOIL))
			c= PRICES.MARKET;
		else
			c = PRICES.AVERAGE;
		
		
		if(getBoolean(GET_FOIL))
			st = interestService.getInterestFor(c,mtgstockformat);
		else
			st = interestService.getInterestFor(c,false,mtgstockformat);
		
		st.stream().filter(inte->inte.getInterestType().equalsIgnoreCase(getString(INTEREST_TYPE))).forEach(i->{
			CardShake cs = initFromPrint(i.getPrint());
						cs.setDateUpdate(i.getDate());
						cs.setPrice(i.getPricePresent());
						cs.setPercentDayChange(i.getPercentage()/100);
						cs.setPriceDayChange(i.getPriceDayChange());
						cs.setFoil(i.isFoil());
						
						
			ret.add(cs);
		});
		
		
		
		return ret;
	}
	
	
	@Override
	protected EditionsShakers getOnlineShakesForEdition(MagicEdition ed) throws IOException {
	
		var es = new EditionsShakers();
						es.setProviderName(getName());
						es.setDate(new Date());
						es.setEdition(ed);
		
		var c = PRICES.valueOf(getString(PRICE_VALUE).toUpperCase());

		fillEditionShaker(c,ed,es,false);
		
		if(getBoolean(GET_FOIL))
		{
			if(c.equals(PRICES.AVG))
				fillEditionShaker(PRICES.FOIL,ed,es,true);
			
			else if(c.equals(PRICES.MARKET))
				fillEditionShaker(PRICES.MARKET_FOIL,ed,es,true);
				
		}
		
		return es;
	}
	
	private void fillEditionShaker(PRICES c,MagicEdition ed, EditionsShakers es, boolean b) {
		
		logger.debug("Parsing shakers for " + ed + " " + c);
		
		cardService.getPrintsBySetCode(ed.getId()).forEach(p->{
					CardShake cs = initFromPrint(p);
					cs.setEd(ed.getId());
					try {
						cs.init(p.getLatestPrices().get(c), p.getLastWeekPreviousPrice(), p.getLastWeekPrice());
						
						
					}
					catch(NullPointerException e)
					{
						logger.error(p +"  " + e);
					}
					cs.setFoil(b);
					
					
					es.getShakes().add(cs);
			});
		
		
	}

	@Override
	protected HistoryPrice<MagicEdition> getOnlinePricesVariation(MagicEdition ed) throws IOException {
		return null;
	}
	

	@Override
	protected HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc, boolean foil)throws IOException {
		HistoryPrice<MagicCard> hp = new HistoryPrice<>(mc);
		if(mc==null)
		{
			logger.error("couldn't calculate edition only");
			return hp;
		}
		
		MagicEdition ed=mc.getCurrentSet();
		
		
		Integer id = mc.getMtgstocksId();

		if(id==null)
		{
			logger.debug("id is null. Looking throught api");
			SearchResult rs = cardService.getBestResult(mc.getName());
			FullPrint fp = cardService.getCard(rs);
			CardSet set = cardService.getSetByCode(ed.getId());
			var fpSet = fp.getPrintForSetId(set.getId());
			
			if(fpSet==null)
			{
				logger.warn("fpSet is null for {} so return", set.getId());
				return hp;
			}
			logger.debug("mtgstock = {} {] {} ",fpSet,fpSet.getSetName(),fpSet.getId());
			id = fpSet.getId();
		}
		
		PRICES p = PRICES.AVG;
		
		if(foil || ed.isFoilOnly())
			p = PRICES.FOIL;
		
	
		pricesService.getPricesFor(id,p).forEach(e->{
			
			hp.setCurrency(getCurrency());
			hp.setFoil(foil);
			hp.getVariations().put(e.getKey(), e.getValue());
			hp.setSerieName(hp.toString());
		});
		
		
		return hp;
		
	}

	private CardShake initFromPrint(Print p)
	{
		var cs = new CardShake();
				cs.setCurrency(getCurrency());
				cs.setName(p.getCleanName());
				cs.setFoil(p.isFoil());
				cs.setLink(p.getWebPage());
				
				if(p.isExtendedArt())
					cs.setCardVariation(MTGCardVariation.EXTENDEDART);
				else if(p.isShowcase())
					cs.setCardVariation(MTGCardVariation.SHOWCASE);
				else if(p.isBorderless())
					cs.setCardVariation(MTGCardVariation.BORDERLESS);
				else if(p.isJapanese())
					cs.setCardVariation(MTGCardVariation.JAPANESEALT);
				
				
				cs.setEd(String.valueOf(cardService.getSetById(p.getSetId()).getAbbrevation()).toUpperCase());
		return cs;
	}
	

	@Override
	public Date getUpdatedDate() {
		return new Date();
	}

	@Override
	public String getName() {
		return "MTGStocks";
	}

	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(
					PRICE_VALUE, "market",
					GET_FOIL,"false",
					INTEREST_TYPE,"day");
	}
	

}
