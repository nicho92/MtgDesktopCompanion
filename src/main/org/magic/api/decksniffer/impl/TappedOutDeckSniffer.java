package org.magic.api.decksniffer.impl;
		
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.InstallCert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TappedOutDeckSniffer extends AbstractDeckSniffer {

	private CookieStore cookieStore;
	private HttpClient httpclient;
	private HttpContext httpContext; 
  	

	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	
	public TappedOutDeckSniffer() throws Exception {
		super();

		if(!new File(confdir, getName()+".conf").exists()){
				props.put("LOGIN", "login@mail.com");
				props.put("PASSWORD", "changeme");
				props.put("FORMAT", "standard");
				props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
				props.put("URL_JSON", "https://tappedout.net/api/deck/latest/%FORMAT%");
				props.put("CERT_SERV", "www.tappedout.net");
				save();
		}
		
		try {
   			InstallCert.install(props.getProperty("CERT_SERV"));
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGConstants.KEYSTORE_NAME).getAbsolutePath());
 		} catch (Exception e1) {
			logger.error(e1);
		}
	}
	
	@Override
	public String[] listFilter(){
		return new String[]{"latest","standard","modern","legacy","vintage","edh","tops","pauper","aggro","budget","control"};
	}
    
    public static void main(String[] args) {
		try {
			TappedOutDeckSniffer sniff = new TappedOutDeckSniffer();
				sniff.connect();
				RetrievableDeck temp = sniff.getDeckList().get(15);
				sniff.getDeck(temp);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 	
	@Override
	public String getName() {
		return "Tapped Out";
	}
    
	@Override
	public void connect() throws Exception
	{
		cookieStore = new BasicCookieStore();
		
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		
		httpclient = HttpClients.custom()
					.setUserAgent(props.getProperty("USER_AGENT"))
					.setRedirectStrategy(new LaxRedirectStrategy())
					//.setConnectionTimeToLive(3000, TimeUnit.MILLISECONDS)
					.build();
		
		httpclient.execute(new HttpGet("https://tappedout.net/accounts/login/?next=/"), httpContext); //get csrfmiddlewaretoken in cookies
	
		HttpPost login = new HttpPost("https://tappedout.net/accounts/login/"); //cookies need have tapped=buxy7qwywslb2tslv85ewqikh52xtztl;
	    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	       					 nvps.add(new BasicNameValuePair("next", "/"));
					         nvps.add(new BasicNameValuePair("username", props.getProperty("LOGIN")));
					         nvps.add(new BasicNameValuePair("password", props.getProperty("PASSWORD")));
					         nvps.add(new BasicNameValuePair("csrfmiddlewaretoken", getCookieValue("csrftoken")));
				 login.setEntity(new UrlEncodedFormEntity(nvps));
				 login.addHeader("Referer", "https://tappedout.net/accounts/login/?next=/");
				 login.addHeader("Upgrade-Insecure-Requests","1");
				 login.addHeader("Origin","https://tappedout.net");
				 HttpResponse resp = httpclient.execute(login, httpContext);
		logger.debug("Connection OK : " + props.getProperty("LOGIN") + " " + resp.getStatusLine().getStatusCode());
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception
	{
		HttpGet get = new HttpGet(info.getUrl());
		
		logger.debug("sniff deck : "+ info.getName()+ " at " + info.getUrl());
		
		String responseBody = EntityUtils.toString(httpclient.execute(get, httpContext).getEntity());
		
		MagicDeck deck = new MagicDeck();
		
		JsonElement root = new JsonParser().parse(responseBody);
		deck.setName(root.getAsJsonObject().get("name").getAsString());
		deck.setDescription(root.getAsJsonObject().get("url").getAsString());
		for(int i=0;i<root.getAsJsonObject().get("inventory").getAsJsonArray().size();i++)
		{
			JsonArray inv = root.getAsJsonObject().get("inventory").getAsJsonArray().get(i).getAsJsonArray();
			String cardName = inv.get(0).getAsString();
			String position =inv.get(1).getAsJsonObject().get("b").getAsString();
			int qte = inv.get(1).getAsJsonObject().get("qty").getAsInt();
			
			
			//remove foil if present
			cardName=cardName.replaceAll("\\*.+?\\*", "").trim();
			
			
			
			//ged ed if present
			String idSet = null;
			Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(cardName);
		    while(m.find()) {
		      idSet = (m.group(1));    
		    }
		    cardName=cardName.replaceAll("\\(([^)]+)\\)", "").trim();
		    
		    
		    //remove behavior if present
		    if (cardName.contains("#"))
		    	cardName=cardName.substring(0,cardName.indexOf("#")).trim();

		    if(cardName.contains("//"))
				cardName=cardName.substring(0, cardName.indexOf("//")).trim();
		
		    List<MagicCard> ret ;
			if(idSet==null)
			{
				if(cardName.trim().equalsIgnoreCase("Plains")||cardName.trim().equalsIgnoreCase("Island")||cardName.trim().equalsIgnoreCase("Swamp")||cardName.trim().equalsIgnoreCase("Mountain")||cardName.trim().equalsIgnoreCase("Forest"))
				{
					MagicEdition ed = new MagicEdition();
					ed.setId(MTGControler.getInstance().get("default-land-deck"));
					ret = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, ed);
				}
				else
				{
					ret = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, null);
				}
				
			}
			else
			{
				MagicEdition ed = new MagicEdition();
				ed.setId(idSet);
				ret = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, ed);
			}
			
			if(ret.size()>0)
			{
				setChanged();
				notifyObservers(deck.getMap());
				
				if(position.equalsIgnoreCase("main"))
					deck.getMap().put(ret.get(0), qte);
				else
					deck.getMapSideBoard().put(ret.get(0), qte);
			}
		}
		return deck;
		
	}
	
	public List<RetrievableDeck> getDeckList() throws Exception {

		String tappedJson = props.getProperty("URL_JSON").replaceAll("%FORMAT%", props.getProperty("FORMAT"));
		
		logger.debug("sniff url : " + tappedJson);
		String responseBody = EntityUtils.toString(httpclient.execute(new HttpGet(tappedJson), httpContext).getEntity());
		
        JsonElement root = new JsonParser().parse(responseBody);
		List<RetrievableDeck> list = new ArrayList<RetrievableDeck>();
		
        for(int i=0;i<root.getAsJsonArray().size();i++)
		{
			JsonObject obj = root.getAsJsonArray().get(i).getAsJsonObject();
			RetrievableDeck deck = new RetrievableDeck();
			deck.setName(obj.get("name").getAsString());
			deck.setUrl(new URI(obj.get("resource_uri").getAsString()));
			deck.setAuthor(obj.get("user").getAsString());
			deck.setColor("");
			list.add(deck);
		}
        
        return list;
	}
	
	private String getCookieValue(String cookieName) 
	{
		String value = null;
		for (Cookie cookie: cookieStore.getCookies()) {
				if (cookie.getName().equals(cookieName)) {
						value = cookie.getValue();
						break;
				}
		}
		return value;
	}

}
