package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;
import org.magic.tools.MTGLogger;
import org.magic.tools.MagicCardComparator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class JSONHttpServer extends AbstractMTGServer
{
	
	static final Logger logger = MTGLogger.getLogger(JSONHttpServer.class);
	NanoHTTPD server;

	
  @Override
    public String description() {
    	return "Acces to mtg desktop companion via json http server";
    }
	
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
			props.put("AUTOSTART", "false");
			save();
		}
		
    	server = new NanoHTTPD(Integer.parseInt(props.get("SERVER-PORT").toString())) {
    		public Response serve(IHTTPSession session) {
			 Map<String, List<String>> parms = session.getParameters();
    		logger.debug("Connection from " + session.getRemoteIpAddress() + " to " + session.getUri() + " " + parms);	
			  switch(session.getUri())
			  {
			  	  case "/collections":  return listCollections(session);
				  case "/editions":  return listEditions(session);
				  case "/cards": return listCards(session);
				  case "/search": return searchcard(session);
				  case "/prices" : return searchPrice(session);
				  case "/move" : return moveCard(session);
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
		MTGControler.getInstance().getEnabledProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
    	new JSONHttpServer().start();	
    }
    
	private Response searchPrice(IHTTPSession session) {
    	try {
	    		String att = session.getParameters().get("name").get(0).toString();
	    		String val = session.getParameters().get("set").get(0).toString();
	    		
	    		MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", att, null).get(0);
	    		MagicEdition ed = MTGControler.getInstance().getEnabledProviders().getSetById(val);
	    		
	  		  	List<MagicPrice> pricesret = new ArrayList<MagicPrice>();
	  		
	  		  	for(MagicPricesProvider prices : MTGControler.getInstance().getEnabledPricers())
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
  		logger.error("ERROR",e);
  		 return NanoHTTPD.newFixedLengthResponse("Usage : /prices?name=<i>cardname</i>&set=<i>IDSET</i>");
  	  }
	}
	
	private Response moveCard(IHTTPSession session)
	{
	  try {
		  
		  if(!session.getMethod().equals(Method.POST))
		  {
			  Response res = NanoHTTPD.newFixedLengthResponse("POST /move?card_id=<ID>&from=COL_NAME&to=COL_NAME");
			  res.setStatus(Status.BAD_REQUEST);
			  return res;
		  }
		  
		  String id=session.getParameters().get("card_id").get(0).toString();
		  MagicCollection from=new MagicCollection(session.getParameters().get("from").get(0).toString());
		  MagicCollection to=new MagicCollection(session.getParameters().get("to").get(0).toString());
		  
		  MagicCard mc = MTGControler.getInstance().getEnabledProviders().getCardById(id);
		  MTGControler.getInstance().getEnabledDAO().removeCard(mc, from);
		  MTGControler.getInstance().getEnabledDAO().saveCard(mc, from);
		  
		  Response resp = NanoHTTPD.newFixedLengthResponse("");
		  resp.setStatus(Status.OK);
		  resp.addHeader("Content-Type", "application/json");
		 
		  return resp;
	  } 
	  catch (Exception e) 
	  {
		  logger.error("ERROR",e);
		  return NanoHTTPD.newFixedLengthResponse("Usage : /move?card_id=<ID>&from=COL_NAME&to=COL_NAME");
	  }
	}

	private Response searchcard(IHTTPSession session)
	{
	  try {
		  String att=session.getParameters().keySet().toArray()[0].toString();
		  String name=session.getParameters().get(session.getParameters().keySet().toArray()[0].toString()).get(0);
		  
		  List<MagicCard> list = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria(att, name, null);
		  Response resp = NanoHTTPD.newFixedLengthResponse(new Gson().toJson(list));
		  resp.addHeader("Content-Type", "application/json");
		  resp.addHeader("Item-count", String.valueOf(list.size()));
		  return resp;
	  } 
	  catch (Exception e) 
	  {
		  logger.error("ERROR",e);
		  return NanoHTTPD.newFixedLengthResponse("Usage : /search?<i>att</i>=<i>value</i>");
	  }
	}
	
	
	private Response listCards(IHTTPSession session) {
   	 try {
		 List<MagicCard> cards = new ArrayList<MagicCard>();
		 
			 String att=session.getParameters().keySet().toArray()[0].toString();
			 String name=session.getParameters().get(session.getParameters().keySet().toArray()[0].toString()).get(0);
			 
			 String att2=session.getParameters().keySet().toArray()[1].toString();
			 String idset=session.getParameters().get(session.getParameters().keySet().toArray()[1].toString()).get(0);
			 
			 MagicCollection col = new MagicCollection(name);
			 MagicEdition ed = new MagicEdition();
			 ed.setId(idset);
			 ed.setSet(idset);
			 
			 cards = MTGControler.getInstance().getEnabledDAO().getCardsFromCollection(col, ed);
			 Collections.sort(cards,new MagicCardComparator());
			 Response resp = NanoHTTPD.newFixedLengthResponse(new Gson().toJson(cards));
			 resp.addHeader("Content-Type", "application/json");
			 resp.addHeader("Item-count", String.valueOf(cards.size()));
		  return resp;
	  } 
	  catch (Exception e) 
	  {
		  logger.error("ERROR",e);
		  return NanoHTTPD.newFixedLengthResponse("Usage : /cards?col=<i>value</i>&set=<i>id</i>");
	  }
	}

	   
	   
	   
    private Response listCollections(IHTTPSession session)
	{
    	
	  try {
		  List<MagicCollection> list = MTGControler.getInstance().getEnabledDAO().getCollections();
		  Response resp = NanoHTTPD.newFixedLengthResponse(new Gson().toJson(list));
		  resp.addHeader("Content-Type", "application/json");
		  resp.addHeader("Item-count", String.valueOf(list.size()));
		  return resp;
	  } 
	  catch (Exception e) 
	  {
		  logger.error("ERROR",e);
		  return NanoHTTPD.newFixedLengthResponse("Usage : /collections");
	  }
	}
    
    private Response listEditions(IHTTPSession session) {
    	 try {
    		 List<MagicEdition> eds = new ArrayList<MagicEdition>();
    		 
    		 if(session.getParameters().keySet().size()>0)
    		 {
    			 String att=session.getParameters().keySet().toArray()[0].toString();
    			 String name=session.getParameters().get(session.getParameters().keySet().toArray()[0].toString()).get(0);
    			 MagicCollection col = new MagicCollection(name);
    			 List<String> list = MTGControler.getInstance().getEnabledDAO().getEditionsIDFromCollection(col);
    			 for(String s : list)
    				 eds.add(MTGControler.getInstance().getEnabledProviders().getSetById(s));
    		 }
    		 else
    		 {
    			 eds = MTGControler.getInstance().getEnabledProviders().loadEditions();
    		 }
   		  
    		 Collections.sort(eds);
    		 
    		 
    	  Response resp = NanoHTTPD.newFixedLengthResponse(new Gson().toJson(eds));
   		  resp.addHeader("Content-Type", "application/json");
   		 resp.addHeader("Item-count", String.valueOf(eds.size()));
   		  return resp;
   	  } 
   	  catch (Exception e) 
   	  {
   		  logger.error("ERROR",e);
   		  return NanoHTTPD.newFixedLengthResponse("Usage : /editions?col=<i>value</i> or /editions");
   	  }
	}
    
   

	@Override
	public String getName() {
		return "Json Http Server";
	}

	
	@Override
	public boolean isAutostart() {
		return props.getProperty("AUTOSTART").equals("true");
	}
    
}