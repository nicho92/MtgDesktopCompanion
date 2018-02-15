package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;

public class MagicBazarPricer extends AbstractMagicPricesProvider {

	Document doc;
	private ArrayList<MagicPrice> list;
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	
	public MagicBazarPricer() {
		super();	
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("URL", "https://www.magicbazar.fr/recherche/result.php?s=");
			props.put("USER_AGENT", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
			
			save();
		}
		
		list=new ArrayList<>();
	}
	
	

	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws IOException {
		list.clear();
		String url = props.getProperty("URL")+URLEncoder.encode(card.getName(), "UTF-8");
		logger.info(getName() +" looking for prices " + url );
		
		try{
			doc = Jsoup.connect(url).userAgent(props.getProperty("USER_AGENT")).timeout(0).get();
			Elements els = doc.select("div.filterElement");
			for(int i = 0; i <els.size();i++)
			{
				Element e = els.get(i);
				MagicPrice mp = new MagicPrice();
						   mp.setLanguage(e.getElementsByClass("langue").get(0).getElementsByTag("img").get(0).attr("alt"));
						   mp.setQuality(e.getElementsByClass("etat").html());

							   mp.setValue(Double.parseDouble(clean(e.select("div.prix").text())));
						   mp.setCurrency("EUR");
						   mp.setSite(getName());
						   mp.setUrl(url);
						   mp.setSeller(e.getElementsByClass("edition").get(0).getElementsByIndexEquals(0).get(0).text());
						   mp.setFoil(!e.getElementsByClass("logo").isEmpty());
				list.add(mp);
				
			}
			return list;
		}
		catch(Exception e)
		{
			logger.trace("Error loading price for " + url,e);
			logger.info(getName() +" no item : "+ e.getMessage());
			return list;
		}
	}

	private String clean(String html) {
		return StringEscapeUtils.escapeHtml3(html).replaceAll(",", ".").replaceAll(" ", "").replaceAll("€", "");
	}


	@Override
	public String getName() {
		return "MagicBazar";
	}

	

	@Override
	public void alertDetected(List<MagicPrice> okz) {
		//do nothing

	}
	
}
