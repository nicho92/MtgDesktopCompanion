package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionPriceVariations;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.tools.URLTools;

public class QuietSpeculationDashboard extends AbstractDashBoard {

	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EditionPriceVariations getOnlineShakesForEdition(MagicEdition ed) throws IOException {
		String uri = "https://www.quietspeculation.com/tradertools/prices/sets/"+ed.getSet().replaceAll(" ", "%20");
		EditionPriceVariations ret = new EditionPriceVariations();
			ret.setEdition(ed);
			ret.setDate(new Date());
			ret.setProviderName(getName());
		
		
		Document content = URLTools.extractHtml(uri);
		
		Elements trs = content.getElementById("SetCards").select("tr[id]");
		trs.forEach(tr->{
			CardShake cs = new CardShake();
				cs.setName(tr.getElementsByTag("td").get(0).text());
				cs.setPrice(Double.parseDouble(tr.getElementsByTag("td").get(5).text().replaceAll("\\$", "")));
				
				cs.setEd(ed.getSet());
				cs.setDateUpdate(new Date());
				cs.setCurrency(Currency.getInstance("USD"));
				System.out.println(cs.getName() + " " + cs.getPrice());
				
				ret.addShake(cs);
			
		});
		return ret;
	}

	@Override
	protected CardPriceVariations getOnlinePricesVariation(MagicCard mc, MagicEdition ed) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
