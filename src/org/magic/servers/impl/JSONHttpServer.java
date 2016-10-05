package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MagicFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class JSONHttpServer extends AbstractMTGServer
{
	
	static final Logger logger = LogManager.getLogger(JSONHttpServer.class.getName());
	NanoHTTPD server;

	public boolean isAlive()
	{
		try{
			return server.isAlive();
		}
		catch(Exception e)
		{
				logger.error(e);
				return false;
		}
	}
	
    public JSONHttpServer() throws IOException {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("SERVER-PORT", "8080");
			save();
		}
		
    	server = new NanoHTTPD(Integer.parseInt(props.get("SERVER-PORT").toString())) {
    		public Response serve(IHTTPSession session) {
			  Map<String, List<String>> parms = session.getParameters();
			  switch(session.getUri())
			  {
				  case "/collection":  return searchcardColl(session); 
				  case "/search": return searchcard(session);
				  case "/prices" : return searchPrice(session);
				  default : return newFixedLengthResponse("Not usable uri")  ;
			  }
    		}
		};
	}
    
    public void stop()
    {
    	logger.info("Server stop");
    	server.stop();
    }
    
    public void start() throws IOException {
    	logger.info("Server start on port "+ props.get("SERVER-PORT"));
    	server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }
    

	public static void main(String[] args) throws Exception {
		MagicFactory.getInstance().getEnabledProviders().init();
		MagicFactory.getInstance().getEnabledDAO().init();
    	new JSONHttpServer().start();	
    }
    
	private Response searchPrice(IHTTPSession session) {
    	try {
	    		String att = session.getParameters().get("name").get(0).toString();
	    		String val = session.getParameters().get("set").get(0).toString();
	    		
	    		MagicCard mc = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria("name", att, null).get(0);
	    		MagicEdition ed = MagicFactory.getInstance().getEnabledProviders().searchSetByCriteria("id", val).get(0);
	    		
	  		  	List<MagicPrice> pricesret = new ArrayList<MagicPrice>();
	  		
	  		  	for(MagicPricesProvider prices : MagicFactory.getInstance().getEnabledPricers())
	  		  		pricesret.addAll(prices.getPrice(ed, mc));
	  		  
	  		  	JsonObject card = new JsonObject();
	  		  			   card.add("card", new Gson().toJsonTree(mc));
	  		  			   card.add("prices", new Gson().toJsonTree(pricesret));
	 		  
	  		  	Response resp =NanoHTTPD.newFixedLengthResponse(card.toString());
	  		  			 resp.addHeader("Content-Type", "application/json");
	  		  			 
  		  return resp;
  	  } 
  	  catch (Exception e) 
  	  {
  		  e.printStackTrace();
  		 return NanoHTTPD.newFixedLengthResponse("Usage : /prices?name=<i>cardname</i>&set=<i>IDSET</i>");
  	  }
	}

	private Response searchcard(IHTTPSession session)
	{
	  try {
		  String att=session.getParameters().keySet().toArray()[0].toString();
		  String name=session.getParameters().get(session.getParameters().keySet().toArray()[0].toString()).get(0);
		  
		  List<MagicCard> list = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria(att, name, null);
		  Response resp = NanoHTTPD.newFixedLengthResponse(new Gson().toJson(list));
		  resp.addHeader("Content-Type", "application/json");
		  return resp;
	  } 
	  catch (Exception e) 
	  {
		  e.printStackTrace();
		  return NanoHTTPD.newFixedLengthResponse("Usage : /search?<i>att</i>=<i>value</i>");
	  }
	}
	
    
    private Response searchcardColl(IHTTPSession session)
	{
    	
	  try {
		  
		  String att=session.getParameters().keySet().toArray()[0].toString();
		  String name=session.getParameters().get(session.getParameters().keySet().toArray()[0].toString()).get(0);
		  
		  MagicCollection col = new MagicCollection();
		  col.setName(name);
		  List<MagicCard> list = MagicFactory.getInstance().getEnabledDAO().getCardsFromCollection(col);
		  Response resp = NanoHTTPD.newFixedLengthResponse(new Gson().toJson(list));
		  resp.addHeader("Content-Type", "application/json");
		  return resp;
	  } 
	  catch (Exception e) 
	  {
		  e.printStackTrace();
		  return NanoHTTPD.newFixedLengthResponse("Usage : /collection?col=<i>value</i>");
	  }
	}

	@Override
	public String getName() {
		return "Json Http Server";
	}

    
}