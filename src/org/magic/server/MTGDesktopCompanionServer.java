package org.magic.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.services.MagicFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD;

public class MTGDesktopCompanionServer  extends NanoHTTPD
{
	
	static final Logger logger = LogManager.getLogger(MTGDesktopCompanionServer.class.getName());


	public boolean isRunning()
	{
		return super.isAlive();
	}
	
    public MTGDesktopCompanionServer() throws IOException {
		super(Integer.parseInt(MagicFactory.getInstance().get("server-port")));
	}
    
    public void stop()
    {
    	logger.info("Server stop");
    	super.stop();
    }
    
    @Override
    public void start() throws IOException {
    	logger.info("Server start on port "+ MagicFactory.getInstance().get("server-port"));
    	start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }
    

	public static void main(String[] args) throws Exception {
		MagicFactory.getInstance().getEnabledProviders().init();
		MagicFactory.getInstance().getEnabledDAO().init();
    	new MTGDesktopCompanionServer().start();	
    }
    
	@Override
	public Response serve(IHTTPSession session) {
		  Map<String, List<String>> parms = session.getParameters();
		  
		  switch(session.getUri())
		  {
			  case "/collection":  return searchcardColl(session.getParameters().keySet().toArray()[0].toString(), parms.get(session.getParameters().keySet().toArray()[0].toString()).get(0)); 
			  case "/search": return searchcard(session.getParameters().keySet().toArray()[0].toString(), parms.get(session.getParameters().keySet().toArray()[0].toString()).get(0));
			  case "/prices" : return searchPrice(session.getParameters().get("name").get(0).toString(),session.getParameters().get("set").get(0).toString());
			  default : return newFixedLengthResponse("Not usable uri")  ;
		  }
	}
	
    private Response searchPrice(String string, String string2) {
    	try {
  		  MagicCard mc = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria("name", string, null).get(0);
  		  
  		  
  		  
  		  List<MagicPrice> pricesret = new ArrayList<MagicPrice>();
  		  for(MagicPricesProvider prices : MagicFactory.getInstance().getEnabledPricers())
  		  {
  			pricesret.addAll(prices.getPrice(mc.getEditions().get(0), mc));
  		  }
  		  
  		 JsonObject card = new JsonObject();
 		  card.add("card", new Gson().toJsonTree(mc));
 		  card.add("prices", new Gson().toJsonTree(pricesret));
 		 
  		  
  		  Response resp = newFixedLengthResponse(card.toString());
  		  resp.addHeader("Content-Type", "application/json");
  		  return resp;
  	  } 
  	  catch (Exception e) 
  	  {
  		  e.printStackTrace();
  		 return newFixedLengthResponse("Usage : /prices?name=<cardname>&set=<IDSET>");
  	  }
	}

	private Response searchcard(String att,String name)
	{
	  try {
		  List<MagicCard> list = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria(att, name, null);
		  Response resp = newFixedLengthResponse(new Gson().toJson(list));
		  resp.addHeader("Content-Type", "application/json");
		  return resp;
	  } 
	  catch (Exception e) 
	  {
		  e.printStackTrace();
		  return newFixedLengthResponse("Usage : /search?<att>=<value>");
	  }
	}
	
    
    private Response searchcardColl(String att,String name)
	{
	  try {
		  MagicCollection col = new MagicCollection();
		  col.setName(name);
		  List<MagicCard> list = MagicFactory.getInstance().getEnabledDAO().getCardsFromCollection(col);
		  Response resp = newFixedLengthResponse(new Gson().toJson(list));
		  resp.addHeader("Content-Type", "application/json");
		  return resp;
	  } 
	  catch (Exception e) 
	  {
		  e.printStackTrace();
		  return newFixedLengthResponse("Usage : /search?col=<value>");
	  }
	}
    
}