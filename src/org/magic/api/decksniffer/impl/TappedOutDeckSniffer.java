package org.magic.api.decksniffer.impl;
		
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.CardExporter;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.gui.models.conf.DeckSnifferTreeTableModel;
import org.magic.services.MagicFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TappedOutDeckSniffer extends AbstractDeckSniffer {

	private CookieStore cookieStore;
	private HttpClient httpclient;
	private HttpContext httpContext; 
    static final Logger logger = LogManager.getLogger(TappedOutDeckSniffer.class.getName());

    
	@Override
	public String toString() {
		return getName();
	}
	
	public TappedOutDeckSniffer() throws Exception {
		super();

		if(!new File(confdir, getName()+".conf").exists()){
				props.put("LOGIN", "login@mail.com");
				props.put("PASSWORD", "changeme");
				props.put("FORMAT", "standard");
				props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
				props.put("URL_JSON", "http://tappedout.net/api/deck/latest/%FORMAT%");
				save();
		}
		
	}
	
	@Override
	public String[] listFilter(){
		return new String[]{"latest","standard","modern","legacy","vintage","edh","tops","pauper","aggro","budget","control"};
	}
	
	ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

    public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
      	 int status = response.getStatusLine().getStatusCode();
         HttpEntity entity = response.getEntity();
     	
         if (status >= 200 && status < 300) 
           {
           	return entity != null ? EntityUtils.toString(entity) : null;
           } 
           else {
           	  throw new ClientProtocolException("Unexpected response status: " + status);
           }
        }
    };
    
	
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
					.build();

		httpclient.execute(new HttpGet("http://tappedout.net/accounts/login"), responseHandler,httpContext); //get CSRF
	       

		 HttpPost login = new HttpPost("http://tappedout.net/accounts/login/");
	        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	        					 nvps.add(new BasicNameValuePair("next", "/"));
						         nvps.add(new BasicNameValuePair("username", props.getProperty("LOGIN")));
						         nvps.add(new BasicNameValuePair("password", props.getProperty("PASSWORD")));
						         nvps.add(new BasicNameValuePair("csrfmiddlewaretoken", getCookieValue(cookieStore, "csrftoken")));
				login.setEntity(new UrlEncodedFormEntity(nvps));
				
			httpclient.execute(login, responseHandler,httpContext);
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception
	{
		
		String responseBody = httpclient.execute(new HttpGet(info.getUrl()), responseHandler, httpContext);
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
		    {
		    	cardName=cardName.substring(0,cardName.indexOf("#")).trim();
		    }
		    
		    
		    
		    List<MagicCard> ret ;
			if(idSet==null)
			{
				ret = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, null);
			}
			else
			{
				MagicEdition ed = new MagicEdition();
				ed.setId(idSet);
				ret = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, ed);
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
	
	/* (non-Javadoc)
	 * @see org.magic.api.decksniffer.impl.DeckSniffer#getDeckList()
	 */
	@Override
	public List<RetrievableDeck> getDeckList() throws Exception {

		String tappedJson = props.getProperty("URL_JSON").replaceAll("%FORMAT%", props.getProperty("FORMAT"));
		String responseBody = httpclient.execute(new HttpGet(tappedJson), responseHandler,httpContext);
        JsonElement root = new JsonParser().parse(responseBody);
		List<RetrievableDeck> list = new ArrayList<RetrievableDeck>();
		
		
        for(int i=0;i<root.getAsJsonArray().size();i++)
		{
			JsonObject obj = root.getAsJsonArray().get(i).getAsJsonObject();
			RetrievableDeck deck = new RetrievableDeck();
			deck.setName(obj.get("name").getAsString());
			deck.setUrl(new URI(obj.get("resource_uri").getAsString()));
			list.add(deck);
		}
        
        return list;
	}
	
	
	private String getCookieValue(CookieStore cookieStore, String cookieName) 
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
