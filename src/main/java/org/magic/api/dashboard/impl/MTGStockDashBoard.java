package org.magic.api.dashboard.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.beans.Packaging;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGControler;
import org.mtgstock.modele.CardSet;
import org.mtgstock.modele.FullPrint;
import org.mtgstock.modele.Print;
import org.mtgstock.modele.SearchResult;
import org.mtgstock.services.CardsService;
import org.mtgstock.services.InterestsService;
import org.mtgstock.services.PriceService;
import org.mtgstock.tools.MTGStockConstants.CATEGORY;
import org.mtgstock.tools.MTGStockConstants.FORMAT;
import org.mtgstock.tools.MTGStockConstants.PRICES;

public class MTGStockDashBoard extends AbstractDashBoard {

	private CardsService cardService;
	private InterestsService interestService;
	private PriceService pricesService;
	
	
	public static void main(String[] args) throws IOException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		
		MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName("Liliana of the Veil", new MagicEdition("UMA"), false).get(0);
		
		
		MTGStockDashBoard dash = new MTGStockDashBoard();
		List<CardShake>	 ret = null;
		
//		ret = dash.getOnlineShakesForEdition(new MagicEdition("UMA", "Ultimate Masters")).getShakes();
//		ret = dash.getOnlineShakerFor(FORMATS.VINTAGE);
//		for(CardShake cs : ret)
//		{
//			System.out.println(cs.getName() + " " + cs.getEd() +" " + cs.getPrice()+ "$ "+ cs.getPercentDayChange() +"% ");
//		}
//		
		dash.getPriceVariation(mc, null, false).forEach(System.out::println);
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	public MTGStockDashBoard() {
		cardService = new CardsService();
		interestService = new InterestsService();
		pricesService = new PriceService();
	}
	

	@Override
	public List<CardDominance> getBestCards(FORMATS f, String filter) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}





	@Override
	protected HistoryPrice<Packaging> getOnlinePricesVariation(Packaging packaging) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<CardShake> getOnlineShakerFor(FORMATS f) throws IOException {
		List<CardShake> ret = new ArrayList<>();
		
		FORMAT mtgstockformat = null;
		
		if(f!=null)
			mtgstockformat = FORMAT.valueOf(f.name());
		
		
		logger.debug("Parsing shakers for " + f);
		interestService.getInterestFor(CATEGORY.AVERAGE,true,mtgstockformat).forEach(i->{
			
			CardShake cs = initFromPrint(i.getPrint());
						cs.setDateUpdate(i.getDate());
						cs.setPrice(i.getPricePresent());
						cs.setPercentDayChange(i.getPercentage());
						cs.setPriceDayChange(i.getPriceDayChange());
						cs.setFoil(i.isFoil());
						
						
			ret.add(cs);
		});
		
		
		
		return ret;
	}
	
	
	@Override
	protected EditionsShakers getOnlineShakesForEdition(MagicEdition ed) throws IOException {
	
		EditionsShakers es = new EditionsShakers();
						es.setProviderName(getName());
						es.setDate(new Date());
						es.setEdition(ed);
		
		logger.debug("Parsing shakers for " + ed);
		cardService.getPrintsBySetCode(ed.getId()).forEach(p->{
					CardShake cs = initFromPrint(p);
					cs.setEd(ed.getId());
					cs.init(p.getLatestPrices().get(PRICES.AVG), p.getLastWeekPreviousPrice(), p.getLastWeekPrice());
					es.getShakes().add(cs);
			});
		return es;
	}

	@Override
	protected HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc, MagicEdition ed, boolean foil)throws IOException {
		HistoryPrice<MagicCard> hp = new HistoryPrice<>(mc);
		if(mc==null)
		{
			logger.error("couldn't calculate edition only");
			return hp;
		}
		
		if(ed==null)
			ed=mc.getCurrentSet();
		
		
		Integer id = mc.getMtgstocksId();

		if(id==null)
		{
			SearchResult rs = cardService.getBestResult(mc.getName());
			FullPrint fp = cardService.getCard(rs);
			CardSet set = cardService.getSetByCode(ed.getId());
			Print fpSet = fp.getPrintForSetId(set.getId());
			
			logger.debug("found " + fpSet +" " + fpSet.getSetName());
			id = fpSet.getId();
		}
		
		PRICES p = PRICES.AVG;
		
		if(foil)
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
		CardShake cs = new CardShake();
				cs.setCurrency(getCurrency());
				cs.setName(p.getCleanName());
				cs.setFoil(p.isFoil());
				cs.setLink(p.getWebPage());
				cs.setBorderless(p.isBorderless());
				cs.setShowcase(p.isShowcase());
				cs.setExtendedArt(p.isExtendedArt());
				cs.setEd(cardService.getSetById(p.getSetId()).getAbbrevation());
		return cs;
	}
	

	@Override
	public Date getUpdatedDate() {
		return new Date();
	}

	@Override
	public String getName() {
		return "MTGStock";
	}


	@Override
	public void initDefault() {
		setProperty("LOGIN", "login@mail.com");
		setProperty("PASS", "changeme");
		setProperty("CARD_PRICES_SHAKER", "market"); // [low, avg, high, foil, market, market_foil]
		setProperty("AVERAGE_MARKET", "average"); // average // market
		setProperty("GET_FOIL","false");
	}
	

}
