package org.magic.servers.impl;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import spark.ResponseTransformer;
import spark.Spark;

public class JSONHttpServer extends AbstractMTGServer {

	ResponseTransformer transformer;
	
	private boolean running=false;
	
	public static void main(String[] args) throws Exception {
		
		MTGControler.getInstance().getEnabledProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
		
		new JSONHttpServer().start();
	}
	
	public JSONHttpServer() {
		super();
		transformer = new ResponseTransformer() {
			private Gson gson = new Gson();
			
			@Override
			public String render(Object model) throws Exception {
				return gson.toJson(model);
			}
		};
	}

	@Override
	public void start() throws IOException {
		port(getInt("SERVER-PORT"));
		
		before("/*", (q, a) -> logger.info("Received api call from " + q.ip()));
		
		get("/cards/search/:att/:val",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledProviders().searchCardByCriteria(request.params(":att"), request.params(":val"), null, false);
		}, transformer);
		
		post("/cards/move/:from/:to/:id",getString("MIME"), (request, response) ->{
			  MagicCollection from=new MagicCollection(request.params(":from"));
			  MagicCollection to=new MagicCollection(request.params(":to"));
			  MagicCard mc = MTGControler.getInstance().getEnabledProviders().getCardById(request.params(":id"));
			  MTGControler.getInstance().getEnabledDAO().removeCard(mc, from);
			  MTGControler.getInstance().getEnabledDAO().saveCard(mc, to);
			  return "OK";
		}, transformer);
		
		
		get("/cards/list/:col/:idEd",getString("MIME"), (request, response) ->{
			
			MagicCollection col = new MagicCollection(request.params(":col"));
			MagicEdition ed = new MagicEdition();
						 ed.setId(request.params(":idEd"));
						 ed.setSet(request.params(":idEd"));
			return MTGControler.getInstance().getEnabledDAO().listCardsFromCollection(col, ed);
		}, transformer);
		
		get("/cards/:id",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledProviders().getCardById(request.params(":id"));
		}, transformer);
		
		
		get("/collections/list",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledDAO().getCollections();
		}, transformer);
		
		get("/collections/:name",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledDAO().getCollection(request.params(":name"));
		}, transformer);
		
		get("/editions/list",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledProviders().loadEditions();
		}, transformer);

		get("/editions/:idSet",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledProviders().getSetById(request.params(":idSet"));
		}, transformer);
		
		get("/editions/list/:colName",getString("MIME"), (request, response) ->{
			 List<MagicEdition> eds = new ArrayList<>();
			 List<String> list = MTGControler.getInstance().getEnabledDAO().getEditionsIDFromCollection(new MagicCollection(request.params(":colName")));
			 for(String s : list)
				 eds.add(MTGControler.getInstance().getEnabledProviders().getSetById(s));
			 
			 Collections.sort(eds);
			 return eds;
			 
		}, transformer);
		
		get("/prices/:idSet/:name",getString("MIME"), (request, response) ->{
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", request.params(":name"), null,false).get(0);
    		MagicEdition ed = MTGControler.getInstance().getEnabledProviders().getSetById(request.params(":idSet"));
    		
  		  	List<MagicPrice> pricesret = new ArrayList<>();
  		
  		  	for(MTGPricesProvider prices : MTGControler.getInstance().getEnabledPricers())
  		  		pricesret.addAll(prices.getPrice(ed, mc));
  		
  		  	return pricesret;
			 
		}, transformer);
		
		
		
		Spark.init();
		logger.info("Server start on port "+ getString("SERVER-PORT"));
		running=true;
	}

	@Override
	public void stop() throws IOException {
		Spark.stop();
		logger.info("Server stop");
		running=false;
	}

	@Override
	public boolean isAlive() {
		return running;
	}

	@Override
	public boolean isAutostart() {
		return getString("AUTOSTART").equalsIgnoreCase("true");
	}

	@Override
	public String description() {
		return "Rest backend server";
	}

	@Override
	public String getName() {
		return "Json Http Server";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty("SERVER-PORT", "8080");
		setProperty("AUTOSTART", "false");
		setProperty("MIME","application/json");
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
