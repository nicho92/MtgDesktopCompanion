package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.beans.Packaging;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.tools.URLTools;

public class QuietSpeculationDashboard extends AbstractDashBoard {

	
	private static final String BASE_URL = "https://www.quietspeculation.com";


	public static void main(String[] args) throws IOException {
		QuietSpeculationDashboard dahs = new QuietSpeculationDashboard();
		
		MagicEdition ed = new MagicEdition("A25");
		ed.setSet("Masters 25");
		
		dahs.getOnlineShakesForEdition(ed);
		
	}
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public List<CardDominance> getBestCards(FORMATS f, String filter) throws IOException {
		return new ArrayList<>();
	}

	@Override
	public Date getUpdatedDate() {
		return new Date();
	}

	@Override
	public String getName() {
		return "QuietSpeculation";
	}

	@Override
	protected List<CardShake> getOnlineShakerFor(FORMATS gameFormat) throws IOException {
		return new ArrayList<>();
	}

	@Override
	protected EditionsShakers getOnlineShakesForEdition(MagicEdition ed) throws IOException {
		String uri = BASE_URL+"/tradertools/prices/sets/"+ed.getSet().replace(" ", "%20");
		EditionsShakers ret = new EditionsShakers();
			ret.setEdition(ed);
			ret.setDate(new Date());
			ret.setProviderName(getName());
		
		
		Document content = URLTools.extractHtml(uri);
		
		Elements trs = content.getElementById("SetCards").select("tr[id]");
		trs.forEach(tr->{
			CardShake cs = new CardShake();
				cs.setName(tr.getElementsByTag("td").get(0).text());
				
				try {
				cs.setPrice(Double.parseDouble(tr.getElementsByTag("td").get(5).text().replace("\\$", "")));
				}
				catch(Exception ex)
				{
					cs.setPrice(0.0);
				}
				
				
				cs.setEd(ed.getSet());
				cs.setDateUpdate(new Date());
				cs.setCurrency(Currency.getInstance("USD"));
				ret.addShake(cs);
				notify(cs);
		});
		return ret;
	}

	@Override
	protected HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc, MagicEdition ed,boolean foil) throws IOException {
		return new HistoryPrice<>(mc);
	}


	@Override
	public HistoryPrice<Packaging> getOnlinePricesVariation(Packaging packaging) throws IOException {
		return new HistoryPrice<>(packaging);
	}

}
