package org.magic.servers.impl;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.put;
import static spark.Spark.delete;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;

import com.google.gson.Gson;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class JSONHttpServer extends AbstractMTGServer {

	ResponseTransformer transformer;
	ByteArrayOutputStream baos;
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

	/*// Enables CORS on requests. This method is an initialization method and should be called once.
	private static void enableCORS(final String origin, final String methods, final String headers) {

	    options("/*", (request, response) -> {

	        String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
	        if (accessControlRequestHeaders != null) {
	            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
	        }

	        String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
	        if (accessControlRequestMethod != null) {
	            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
	        }

	        return "OK";
	    });

	    before((request, response) -> {
	        response.header("Access-Control-Allow-Origin", origin);
	        response.header("Access-Control-Request-Method", methods);
	        response.header("Access-Control-Allow-Headers", headers);
	        // Note: this may or may not be necessary in your particular application
	        response.type("application/json");
	    });
	}*/
	
	
	@Override
	public void start() throws IOException {
		port(getInt("SERVER-PORT"));
		
		exception(Exception.class, new ExceptionHandler<Exception>() {
			
			@Override
			public void handle(Exception exception, Request req, Response res) {
				 
					logger.error("Error with : " + req.queryString(),exception );
				 res.status(500);
				 res.body("{\"error\":\""+exception+"\"}");
				
			}
		});
		
		notFound((req, res) -> {
		    res.status(404);
		    return "{\"error\":\"not found\"}";
		});
		
		before("/*", (request, response) ->
		{
			response.type(getString("MIME"));
			response.header("Access-Control-Allow-Origin", getString("Access-Control-Allow-Origin"));
			logger.info("Received api call from " + request.ip());
		});
		
		get("/cards/search/:att/:val",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledProviders().searchCardByCriteria(request.params(":att"), request.params(":val"), null, false);
		}, transformer);
		
		put("/cards/move/:from/:to/:id",getString("MIME"), (request, response) ->{
			  MagicCollection from=new MagicCollection(request.params(":from"));
			  MagicCollection to=new MagicCollection(request.params(":to"));
			  MagicCard mc = MTGControler.getInstance().getEnabledProviders().getCardById(request.params(":id"));
			  MTGControler.getInstance().getEnabledDAO().removeCard(mc, from);
			  MTGControler.getInstance().getEnabledDAO().saveCard(mc, to);
			  return "OK";
		}, transformer);
		
		put("/cards/add/:to/:id",getString("MIME"), (request, response) ->{
			  MagicCollection to=new MagicCollection(request.params(":to"));
			  MagicCard mc = MTGControler.getInstance().getEnabledProviders().getCardById(request.params(":id"));
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
		
		get("/cards/:idSet/cards",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("set",request.params(":idSet"),null,false);
		}, transformer);
		
		
		
		get("/collections/:name/count",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledDAO().getCardsCountGlobal(new MagicCollection(request.params(":name")));
		}, transformer);
		
		get("/collections/list",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledDAO().getCollections();
		}, transformer);
		
		get("/collections/cards/:idcards",getString("MIME"), (request, response) ->{
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().getCardById(request.params(":idcards"));
			return MTGControler.getInstance().getEnabledDAO().listCollectionFromCards(mc);
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
			MagicEdition ed = MTGControler.getInstance().getEnabledProviders().getSetById(request.params(":idSet"));
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", request.params(":name"), ed,false).get(0);
    	  	List<MagicPrice> pricesret = new ArrayList<>();
  		  	for(MTGPricesProvider prices : MTGControler.getInstance().getEnabledPricers())
  		  		pricesret.addAll(prices.getPrice(ed, mc));
  		
  		  	return pricesret;
			 
		}, transformer);
		
		get("/alerts/list",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledDAO().listAlerts();
			 
		}, transformer);
		
		get("/alerts/:idCards",getString("MIME"), (request, response) ->{
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("id", request.params(":idCards"), null,true).get(0);
			return MTGControler.getInstance().getEnabledDAO().hasAlert(mc);
			 
		}, transformer);

		put("/alerts/add/:idCards",getString("MIME"), (request, response) ->{
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("id", request.params(":idCards"), null,true).get(0);
			MagicCardAlert alert = new MagicCardAlert();
			alert.setCard(mc);
			alert.setPrice(0.0);
			MTGControler.getInstance().getEnabledDAO().saveAlert(alert);
			return "OK";
		});
		

		get("/stock/list",getString("MIME"), (request, response) ->{
			return MTGControler.getInstance().getEnabledDAO().listStocks();
			 
		}, transformer);
		
		get("/dash/history/:idSet/:name",getString("MIME"), (request, response) ->{
			MagicEdition ed = MTGControler.getInstance().getEnabledProviders().getSetById(request.params(":idSet"));
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", request.params(":name"), ed,false).get(0);
    		
    	  	return MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(mc, ed);
		}, transformer);
	

		get("/pics/cards/:id",getString("MIME"), (request, response) ->{
			
			baos = new ByteArrayOutputStream();
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().getCardById(request.params(":id"));
			BufferedImage im= MTGControler.getInstance().getEnabledPicturesProvider().getPicture(mc, null);
			ImageIO.write( im, "png", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			response.type("image/png");
		   
			return imageInByte;
		});
		
		
		if(getBoolean("ENABLE_GZIP")) {
			after((request, response) -> {
			    response.header("Content-Encoding", "gzip");
			});
		}
		
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
		return getBoolean("AUTOSTART");
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
		return STATUT.STABLE;
	}

	@Override
	public void initDefault() {
		setProperty("SERVER-PORT", "8080");
		setProperty("AUTOSTART", "false");
		setProperty("MIME","application/json");
		setProperty("ENABLE_GZIP","false");
		
		setProperty("Access-Control-Allow-Origin","*");
		setProperty("Access-Control-Request-Method","");
		setProperty("Access-Control-Allow-Headers","");
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
