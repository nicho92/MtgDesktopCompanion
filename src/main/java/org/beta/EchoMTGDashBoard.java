package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.exports.impl.EchoMTGExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EchoMTGDashBoard extends AbstractDashBoard {

	private static final String WEBSITE = "www.echomtg.com";
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
				 .addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON+", text/javascript, */*; q=0.01")
				 .addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br")
				 .addHeader(URLTools.ACCEPT_LANGUAGE, "en-US,en;q=0.9,fr-FR;q=0.8,fr;q=0.7")
				 .addHeader(URLTools.X_REQUESTED_WITH, "XMLHttpRequest")
				 .addHeader(URLTools.HOST, WEBSITE)
				 .addHeader(URLTools.REFERER, EchoMTGExport.BASE_URL)
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
	protected HistoryPrice<MagicCard> getOnlinePricesVariation(MagicCard mc, MagicEdition ed) throws IOException {
		JsonObject id = getCardId(mc,ed);
		HistoryPrice<MagicCard> history = new HistoryPrice<>(mc);
							history.setCurrency(Currency.getInstance("USD"));
							
							
		JsonArray ret = RequestBuilder.build().method(METHOD.GET).setClient(client)
				 .url(EchoMTGExport.BASE_URL+"/cache/"+id.get("emid").getAsString()+".r.json")
				 .addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON+", text/javascript, */*; q=0.01")
				 .addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br")
				 .addHeader(URLTools.ACCEPT_LANGUAGE, "en-US,en;q=0.9,fr-FR;q=0.8,fr;q=0.7")
				 .addHeader(URLTools.X_REQUESTED_WITH, "XMLHttpRequest")
				 .addHeader(URLTools.HOST, WEBSITE)
				 .addHeader(URLTools.REFERER, EchoMTGExport.BASE_URL+id.get("url").getAsString())
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
		
		MTGLogger.changeLevel(Level.ALL);
		new EchoMTGDashBoard().getOnlineShakesForEdition(ed);
	}
	
	
	@Override
	protected EditionsShakers getOnlineShakesForEdition(MagicEdition ed) throws IOException {
		
		EditionsShakers variations = new EditionsShakers();
		variations.setDate(new Date());
		variations.setEdition(ed);
		variations.setProviderName(getName());
		
		Document d = RequestBuilder.build().method(METHOD.GET).setClient(client)
		 .url(EchoMTGExport.BASE_URL+"/set/"+ed.getId().toUpperCase()+"/"+ed.getSet().replace(" ", "-").toLowerCase()+"/")
		 .addHeader(URLTools.HOST, WEBSITE)
		 .addHeader(URLTools.REFERER, EchoMTGExport.BASE_URL)
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
						  double pc = Double.parseDouble(tds.get(3).text().replace("%",""))/100;
						  lastWeekPrice = price - (lastWeekPrice*pc);
					  }
					  cs.init(price, price, lastWeekPrice);
					  
					  
					  
					  
					  cs.setCurrency(getCurrency());
			variations.addShake(cs);
		});
		return variations;
	}
	
	
	@Override
	public String[] getDominanceFilters() {
		return new String[] { "magic-reserve-list", "lands", "creatures", "artifacts" };
	}

	
	@Override
	public List<CardDominance> getBestCards(FORMATS f, String filter) throws IOException {
		ArrayList<CardDominance> list = new ArrayList<>();
		
		Document d = RequestBuilder.build().method(METHOD.GET).setClient(client)
				 .url(EchoMTGExport.BASE_URL+"/groups/"+filter+"/")
				 .addHeader(URLTools.HOST, WEBSITE)
				 .addHeader(URLTools.REFERER, EchoMTGExport.BASE_URL)
				 .toHtml();
		
		
		logger.debug(d);
		
		
		
		
		return list;
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
		return  new ArrayList<>();
	}


	@Override
	public HistoryPrice<Package> getPriceVariation(Package packaging) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
