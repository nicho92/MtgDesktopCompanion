package org.beta;

import java.io.IOException;
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
import org.magic.api.exports.impl.EchoMTGExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGControler;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EchoMTGDashBoard extends AbstractDashBoard {

	private static final String REFERER = "Referer";
	private static final String HOST = "Host";
	private static final String X_REQUESTED_WITH = "X-Requested-With";
	private static final String ACCEPT_LANGUAGE = "Accept-Language";
	private static final String ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ACCEPT = "Accept";
	private URLToolsClient client;
	
	
	public EchoMTGDashBoard() {
		super();
		client = URLTools.newClient();
	}
	
	
	private JsonObject getCardId(MagicCard c,MagicEdition ed) throws IOException
	{
		MagicEdition edi = c.getCurrentSet();
		if(ed!=null)
			edi=ed;
		
		JsonArray list = RequestBuilder.build().method(METHOD.GET).setClient(client)
				 .url(EchoMTGExport.BASE_URL+"/data/ajax.getsearchresults.php")
				 .addContent("term", c.getName())
				 .addHeader(ACCEPT, "application/json, text/javascript, */*; q=0.01")
				 .addHeader(ACCEPT_ENCODING, "gzip, deflate, br")
				 .addHeader(ACCEPT_LANGUAGE, "en-US,en;q=0.9,fr-FR;q=0.8,fr;q=0.7")
				 .addHeader(X_REQUESTED_WITH, "XMLHttpRequest")
				 .addHeader(HOST, "www.echomtg.com")
				 .addHeader(REFERER, EchoMTGExport.BASE_URL)
				 .toJson().getAsJsonArray();
		
		for(int i = 0; i<list.getAsJsonArray().size();i++)
		{
			JsonObject obj = list.get(i).getAsJsonObject();
			if(obj.get("setcode").getAsString().equalsIgnoreCase(edi.getId()))
				return obj;
		}		
		logger.debug(edi + " is not found. Loading first one "+list.getAsJsonArray().get(0).getAsJsonObject());
		return list.getAsJsonArray().get(0).getAsJsonObject();
	}
	
	@Override
	protected CardPriceVariations getOnlinePricesVariation(MagicCard mc, MagicEdition ed) throws IOException {
		JsonObject id = getCardId(mc,ed);
		CardPriceVariations history = new CardPriceVariations(mc);
							history.setCurrency(Currency.getInstance("USD"));
							
							
		JsonArray ret = RequestBuilder.build().method(METHOD.GET).setClient(client)
				 .url(EchoMTGExport.BASE_URL+"/cache/"+id.get("emid").getAsString()+".r.json")
				 .addHeader(ACCEPT, "application/json, text/javascript, */*; q=0.01")
				 .addHeader(ACCEPT_ENCODING, "gzip, deflate, br")
				 .addHeader(ACCEPT_LANGUAGE, "en-US,en;q=0.9,fr-FR;q=0.8,fr;q=0.7")
				 .addHeader(X_REQUESTED_WITH, "XMLHttpRequest")
				 .addHeader(HOST, "www.echomtg.com")
				 .addHeader(REFERER, EchoMTGExport.BASE_URL+id.get("url").getAsString())
				 .toJson().getAsJsonArray();
		
		
		ret.forEach(arr->{
			JsonArray aray = arr.getAsJsonArray();
			history.put(new Date(aray.get(0).getAsLong()), aray.get(1).getAsDouble());
		});
		return history;
	}
	
	
	public static void main(String[] args) throws IOException {
		MTGCardsProvider prov = MTGControler.getInstance().getEnabled(MTGCardsProvider.class);
		prov.init();
		MagicEdition ed = prov.getSetById("UMA");
		
		new EchoMTGDashBoard().getOnlineShakesForEdition(ed);
	}
	
	
	@Override
	protected EditionPriceVariations getOnlineShakesForEdition(MagicEdition ed) throws IOException {
		
		EditionPriceVariations variations = new EditionPriceVariations();
		variations.setDate(new Date());
		variations.setEdition(ed);
		variations.setProviderName(getName());
		
		Document d = RequestBuilder.build().method(METHOD.GET).setClient(client)
		 .url(EchoMTGExport.BASE_URL+"/set/"+ed.getId().toUpperCase()+"/"+ed.getSet().replaceAll(" ", "-").toLowerCase()+"/")
		 .addHeader(ACCEPT, "application/json, text/javascript, */*; q=0.01")
		 .addHeader(ACCEPT_ENCODING, "gzip, deflate, br")
		 .addHeader(ACCEPT_LANGUAGE, "en-US,en;q=0.9,fr-FR;q=0.8,fr;q=0.7")
		 .addHeader(X_REQUESTED_WITH, "XMLHttpRequest")
		 .addHeader(HOST, "www.echomtg.com")
		 .addHeader(REFERER, EchoMTGExport.BASE_URL)
		 .toHtml();
		
		
		Elements trs = d.select("table#set-table tr");
		trs.remove(trs.first());
		trs.remove(trs.last());
		
		trs.forEach(tr->{
			
			Elements tds = tr.getElementsByTag("td");
			CardShake cs = new CardShake();
					  cs.setEd(ed.getId());
					  cs.setName(tds.get(2).getElementsByTag("a").first().text());
				  
					  double price =Double.parseDouble(tds.get(4).getElementsByTag("a").first().attr("data-price"));
					  double lastWeekPrice = price;
					  
					  if(!tds.get(3).text().isEmpty())
					  {
						  double pc = Double.parseDouble(tds.get(3).text().replaceAll("%",""))/100;
						  lastWeekPrice = price + (lastWeekPrice*pc);
						  //TODO calculate week price
						  System.out.println(cs + " " + price + " " + lastWeekPrice + " "+ (pc*100));
						  
					  }
					  cs.init(price, price, lastWeekPrice);
					  
					  
					  
					  
					  cs.setCurrency(getCurrency());
			variations.addShake(cs);
		});
		return variations;
	}
	
	
	
	@Override
	public List<CardDominance> getBestCards(FORMATS f, String filter) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getUpdatedDate() {
		return new Date();
	}

	@Override
	public String getName() {
		return "EchoMTG";
	}

	@Override
	protected List<CardShake> getOnlineShakerFor(FORMATS gameFormat) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}





}
