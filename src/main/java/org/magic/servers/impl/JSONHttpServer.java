package org.magic.servers.impl;

import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.getPlugin;
import static org.magic.tools.MTG.listEnabledPlugins;
import static org.magic.tools.MTG.listPlugins;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.notFound;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFiles;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.magic.api.beans.Announce;
import org.magic.api.beans.GedEntry;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicPrice;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.WebShopConfig;
import org.magic.api.beans.audit.JsonQueryInfo;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGTrackingService;
import org.magic.api.interfaces.abstracts.AbstractEmbeddedCacheProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.services.CardsManagerService;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.PluginRegistry;
import org.magic.services.ReportNotificationManager;
import org.magic.services.TechnicalServiceManager;
import org.magic.services.TransactionService;
import org.magic.services.VersionChecker;
import org.magic.services.keywords.AbstractKeyWordsManager;
import org.magic.services.network.URLTools;
import org.magic.services.providers.SealedProductProvider;
import org.magic.services.recognition.area.ManualAreaStrat;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.Chrono;
import org.magic.tools.ImageTools;
import org.magic.tools.MTG;
import org.magic.tools.POMReader;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;
import spark.route.HttpMethod;

public class JSONHttpServer extends AbstractMTGServer {

	private static final String ERROR = "error";
	private static final String CLASS = ":class";
	private static final String PROVIDER = ":provider";
	private static final String COLLECTION = ":collection";
	private static final String ID_SET = ":idSet";
	private static final String ENABLE_SSL = "ENABLE_SSL";
	private static final String NAME = ":name";
	private static final String ID_ED = ":idEd";
	private static final String ID_CARDS = ":idCards";
	private static final String PASSTOKEN = "PASSWORD-TOKEN";
	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ENABLE_GZIP = "ENABLE_GZIP";
	private static final String AUTOSTART = "AUTOSTART";
	private static final String SERVER_PORT = "SERVER-PORT";
	private static final String KEYSTORE_URI = "KEYSTORE_URI";
	private static final String KEYSTORE_PASS = "KEYSTORE_PASS";

	private ResponseTransformer transformer;
	private MTGDeckManager manager;
	private boolean running = false;
	private static final String RETURN_OK = "{\"result\":\"OK\"}";
	private static final String CACHE_TIMEOUT = "CACHE_TIMEOUT";
	private JsonExport converter;
	private UserAgentAnalyzer ua ;
	private Instant start;
	private AbstractEmbeddedCacheProvider<String, Object> cache;
	
	private String error(String msg) {
		return "{\"error\":\"" + msg + "\"}";
	}
	
	
	
	public AbstractEmbeddedCacheProvider<String, Object> getCache() {
		return cache;
	}
	
	
	public JSONHttpServer() {
		manager = new MTGDeckManager();
		converter = new JsonExport();
		ua = UserAgentAnalyzer.newBuilder().build();
		transformer = new ResponseTransformer() {
			@Override
			public String render(Object model) throws Exception {
				return converter.toJson(model);
			}
		};
		
		
	}
	
	private Object getCached(String k, Callable<Object> call)
	{
		if(cache.getItem(k)==null)
			try {
				cache.put(call.call(),k);
			} catch (Exception e) {
				logger.error(e);
				return new ArrayList<>();
			}
		
		return cache.getItem(k);
	}
	

	@Override
	public void start() throws IOException {
		var timeout = this.getInt(CACHE_TIMEOUT);
		
		cache = new AbstractEmbeddedCacheProvider<>() {
			Cache<String, Object> guava = CacheBuilder.newBuilder().expireAfterWrite(timeout, TimeUnit.MINUTES).build();
			
			public String getName() {
				return "Guava";
			}

			@Override
			public void clear() {
				guava.invalidateAll();
			}
			
			@Override
			public long size() {
				return 0;
			}
			
			public Object getItem(String k) {
				return guava.getIfPresent(k);
			}
			
			@Override
			public Map<String,Object> entries() {
				return guava.asMap();
			}
			
			@Override
			public void put(Object value, String key) throws IOException {
				guava.put(key, value);
				
			}
		};
		
		
		if(getBoolean(ENABLE_SSL))
			Spark.secure(getString(KEYSTORE_URI), getString(KEYSTORE_PASS), null, null);
		
		initVars();
		initRoutes();
		Spark.init();
		running = true;
		logger.info("Server " + getName() +" started on port " + getInt(SERVER_PORT));
	}
	

	private void addInfo(Request request, Response response) {
		var info= new JsonQueryInfo();
			info.setStart(start);
			info.setContentType(request.contentType());
			info.setIp(request.ip());
			info.setMethod(request.requestMethod());
			info.setUrl(request.uri());
			info.setParameters(request.params());
			info.setAttributs(request.attributes());
			info.setHeaders(request.headers().stream().collect(Collectors.toMap(s->s,request::headers)));			
			info.setStatus(response.status());
			info.setUserAgent(ua.parse(request.userAgent()));
			info.setEnd(Instant.now());
			
		TechnicalServiceManager.inst().store(info);
		
	}


	
	private void initVars() {
		
		Spark.
		
		threadPool(getInt("THREADS"));
		
		port(getInt(SERVER_PORT));
		
		initExceptionHandler(e -> {
			running = false;
			logger.error(e);
		});
		
		if(getBoolean("ENABLE_WEB_ADMIN"))
			staticFiles.location("/web/admin-ui");
		
		exception(Exception.class, (Exception exception, Request request, Response response) -> {
			logger.error("Error :" + request.headers(URLTools.REFERER) + ":" + exception.getMessage(), exception);
			response.status(500);
			response.body(error(exception.getMessage()));
			addInfo(request,response);
		});

		notFound((req, res) -> {
			res.status(404);
			addInfo(req,res);
			return error("Not Found");
		});
		
		before("/*", (request, response) -> {
			response.type(URLTools.HEADER_JSON);
			response.header(ACCESS_CONTROL_ALLOW_ORIGIN, getString(ACCESS_CONTROL_ALLOW_ORIGIN));
			response.header(ACCESS_CONTROL_REQUEST_METHOD, getString(ACCESS_CONTROL_REQUEST_METHOD));
			response.header(ACCESS_CONTROL_ALLOW_HEADERS, getString(ACCESS_CONTROL_ALLOW_HEADERS));
			start=Instant.now(); //TODO not sure...
			
		});
	

		after((request, response) -> {
			if (getBoolean(ENABLE_GZIP)) {
				response.header("Content-Encoding", "gzip");
			}
			addInfo(request,response);
		
		});
		
		options("/*", (request, response) -> {
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header(ACCESS_CONTROL_ALLOW_HEADERS, accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers(ACCESS_CONTROL_REQUEST_METHOD);
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}
			return RETURN_OK;
		});
		
		

	}


	@SuppressWarnings("unchecked")
	private void initRoutes() {

		get("/cards/search/:att/:val", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGCardsProvider.class).searchCardByCriteria(request.params(":att"), request.params(":val"), null, false),
				transformer);
		
		get("/version", "text", (request, response) ->  
			 getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public String call() throws Exception {
					return new VersionChecker().getVersion();
				}
			})
			
			
		);
		
		get("/cards/search/:att/:val/:exact", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGCardsProvider.class).searchCardByCriteria(request.params(":att"), request.params(":val"), null, Boolean.parseBoolean(request.params(":exact"))),
				transformer);
		
		get("/cards/suggestcard/:val", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGCardsIndexer.class).search("name:\""+request.params(":val")+"\"").stream().map(MagicCard::toLightJson).toList(),
				transformer);
		
		//used only in chromeplugin
		get("/cards/light/:name", URLTools.HEADER_JSON,(request, response) -> {
			List<MagicCard> list= getEnabledPlugin(MTGCardsProvider.class).searchCardByName(request.params(NAME), null, true);
			var arr = new JsonArray();
			
			for(MagicCard mc : list)
			{
				List<MagicCollection> cols = getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc);
				var obj = mc.toLightJson();
				obj.add("collections", converter.toJsonElement(cols));
				arr.add(obj);			
			}
			return arr;
			
		},transformer);
		
		
		post("/ged/:class/:id", URLTools.HEADER_JSON,(request, response) -> {
			
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request.raw());
			var ret = new JsonObject();
			var arr = new JsonArray();
			ret.add("files", arr);
			items.forEach(fi->{
				var fileObj = new JsonObject();
					fileObj.addProperty("name", fi.getName());
					fileObj.addProperty("size", fi.getSize());
			
				try {
					logger.debug(request);
					logger.debug("Uploading " + fi);
					var entry = new GedEntry<>(fi.get(),PluginRegistry.inst().loadClass("org.magic.api.beans."+request.params(CLASS)),request.params(":id"),fi.getName());
					MTG.getEnabledPlugin(MTGGedStorage.class).store(entry);
					fileObj.addProperty("url", (getBoolean(ENABLE_SSL)?"https://":"http://")+request.headers("Host")+"/ged/"+request.params(CLASS)+"/"+request.params(":id"));
				}
				catch(Exception e)
				{
					logger.error(e);
					fileObj.addProperty(ERROR, e.getMessage());
				}
				
				arr.add(fileObj);
				
			});
			
			return ret;
		},transformer);
		
		
		get("/ged/:class/:id", URLTools.HEADER_JSON,(request, response) -> 
			getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public JsonArray call() throws Exception {
					var arr = new JsonArray();
					
					var classename = request.params(CLASS);
					if(!classename.startsWith("org.magic.api.beans"))
						classename = "org.magic.api.beans."+ request.params(CLASS);
					
					MTG.getEnabledPlugin(MTGGedStorage.class).listDirectory(Path.of(classename,request.params(":id"))).forEach(p->{
						try {
							var e =MTG.getEnabledPlugin(MTGGedStorage.class).read(p);
							if(e.isImage()) {
								   arr.add(e.toJson());
							}
						} catch (Exception e) 
						{
							logger.error(e);
						}
					});
					return arr;
				}
			}),transformer);
		
		
		get("/orders/list", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGDao.class).listOrders(), transformer);
		
		get("/keywords", URLTools.HEADER_JSON, (request, response) -> AbstractKeyWordsManager.getInstance().toJson(), transformer);
		
		get("/categories", URLTools.HEADER_JSON, (request, response) -> EnumItems.values(), transformer);
		
		
		
		
		get("/cards/name/:idEd/:cName", URLTools.HEADER_JSON, (request, response) -> {
			MagicEdition ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(ID_ED));
			return getEnabledPlugin(MTGCardsProvider.class).searchCardByName(
					request.params(":cName"), ed, true);
		}, transformer);
		
		get("/cards/number/:idEd/:cNumber", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(request.params(":cNumber"), request.params(ID_ED)), transformer);

		post("/cards/recognize/:threeshold", URLTools.HEADER_JSON, (request, response) -> {
			var recog = MTG.getEnabledPlugin(MTGCardRecognition.class);
			var strat = new ManualAreaStrat();
			var buffImg = ImageTools.readBase64(request.body().substring(request.body().indexOf(",")+1));// Find better solution
			
			if(buffImg==null)
				return "No readable Image";
			
			recog.loadAllCachedData();
			return strat.recognize(buffImg,recog,Integer.parseInt(request.params(":threeshold")));
		}, transformer);
		
		put("/cards/move/:from/:to/:id", URLTools.HEADER_JSON, (request, response) -> {
			var from = new MagicCollection(request.params(":from"));
			var to = new MagicCollection(request.params(":to"));
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(":id"));
			getEnabledPlugin(MTGDao.class).moveCard(mc, from,to);
			return RETURN_OK;
		}, transformer);

		put("/cards/add/:id", URLTools.HEADER_JSON, (request, response) -> {
			var from = new MagicCollection(MTGControler.getInstance().get("default-library"));
			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(":id"));
			CardsManagerService.saveCard(mc, from,null);
			return RETURN_OK;
		}, transformer);

		put("/cards/add/:to/:id", URLTools.HEADER_JSON, (request, response) -> {
			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(":id"));
			CardsManagerService.saveCard(mc, new MagicCollection(request.params(":to")),null);
			return RETURN_OK;
		}, transformer);

		
		get("/cards/list/:col", URLTools.HEADER_JSON, (request, response) -> {
			return getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public List<MagicCard> call() throws Exception {
						var col = new MagicCollection(request.params(":col"));
						return getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, null);
				}
			});
		}, transformer);
		
		get("/cards/list/:col/:idEd", URLTools.HEADER_JSON, (request, response) -> {
			return getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public List<MagicCard> call() throws Exception {
					var col = new MagicCollection(request.params(":col"));
					var ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(ID_ED));
					return getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, ed);
				}
				});
		}, transformer);

		get("/cards/:id", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(":id")), transformer);
		
		get("/cards/:idSet/cards", URLTools.HEADER_JSON, (request, response) -> 
			 getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public List<MagicCard> call() throws Exception {
					var ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(ID_SET));
					var ret = getEnabledPlugin(MTGCardsProvider.class).searchCardByEdition(ed);
					Collections.sort(ret, new CardsEditionSorter());
					return ret;
				}
			})
		, transformer);


		get("/collections/:name/count", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGDao.class).getCardsCountGlobal(new MagicCollection(request.params(NAME))), transformer);

		get("/collections/list", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGDao.class).listCollections(), transformer);

		get("/collections/cards/:idcards", URLTools.HEADER_JSON, (request, response) -> {
			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(":idcards"));
			return getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc);
		}, transformer);

		get("/collections/:name", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGDao.class)
				.getCollection(request.params(NAME)), transformer);

		put("/collections/add/:name", URLTools.HEADER_JSON, (request, response) -> {
			getEnabledPlugin(MTGDao.class).saveCollection(request.params(NAME));
			return RETURN_OK;
		});

		get("/editions/list", URLTools.HEADER_JSON,(request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
				
				@Override
				public List<MagicEdition> call() throws Exception {
					return getEnabledPlugin(MTGCardsProvider.class).listEditions();
				}
			})
		, transformer);

		get("/editions/:idSet", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(ID_SET)), transformer);

		
		get("/editions/list/:colName", URLTools.HEADER_JSON, (request, response) -> {
			return getCached(request.pathInfo(), new Callable<Object>() {
				
				public List<MagicEdition> call() throws Exception{
					var eds = new ArrayList<MagicEdition>();
					var list = getEnabledPlugin(MTGDao.class).listEditionsIDFromCollection(new MagicCollection(request.params(":colName")));
					for (String s : list)
						eds.add(getEnabledPlugin(MTGCardsProvider.class).getSetById(s));

					Collections.sort(eds);
					return eds;
				}
				
			});
			
			

		}, transformer);

		get("/prices/:idSet/:name", URLTools.HEADER_JSON, (request, response) -> 
			getCached(request.pathInfo(), new Callable<Object>() {
				
				@Override
				public List<MagicPrice> call() throws Exception {
					MagicEdition ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(ID_SET));
					MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( request.params(NAME), ed, false).get(0);
					List<MagicPrice> pricesret = new ArrayList<>();
					for (MTGPricesProvider prices : listEnabledPlugins(MTGPricesProvider.class))
					{
						try {
							pricesret.addAll(prices.getPrice(mc));
						}
						catch(Exception e)
						{
							logger.error(e);
						}
					}
					return pricesret;
				}
			})
			, transformer);
			
		get("/prices/details/:provider/:idCards", URLTools.HEADER_JSON, (request, response) -> 
				getCached(request.pathInfo(), new Callable<Object>() {
					
					@Override
					public List<MagicPrice> call() throws Exception {
						MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(ID_CARDS));
						return MTG.getPlugin(request.params(":provider").trim(),MTGPricesProvider.class).getPrice(mc);
					}
				})
		, transformer);
		
		
		
	
		get("/alerts/list", URLTools.HEADER_JSON,
				(request, response) -> getCached(request.pathInfo(), new Callable<Object>() {
						@Override
						public List<MagicCardAlert> call() throws Exception {
							return getEnabledPlugin(MTGDao.class).listAlerts();
						}
					})
					, transformer);

		get("/alerts/:idCards", URLTools.HEADER_JSON, (request, response) -> {
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(ID_CARDS));
			return getEnabledPlugin(MTGDao.class).hasAlert(mc);

		}, transformer);

		put("/alerts/add/:idCards", (request, response) -> {
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(ID_CARDS));
			var alert = new MagicCardAlert();
			alert.setCard(mc);
			alert.setPrice(0.0);
			getEnabledPlugin(MTGDao.class).saveAlert(alert);
			return RETURN_OK;
		});

		put("/stock/add/:idCards", (request, response) -> {
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(ID_CARDS));
			var stock = MTGControler.getInstance().getDefaultStock();
			stock.setQte(1);
			stock.setProduct(mc);

			getEnabledPlugin(MTGDao.class).saveOrUpdateCardStock(stock);
			return RETURN_OK;
		});

		get("/pics/banner", URLTools.HEADER_JSON,(request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
					@Override
					public JsonElement call() throws Exception {
						
						var obj = new JsonObject();
						for(MagicEdition ed : MTG.getEnabledPlugin(MTGCardsProvider.class).listEditions())
						{
							try {
								obj.addProperty( ed.getId(),SealedProductProvider.inst().get(ed,EnumItems.BANNER,"en").get(0).getUrl());
							}
							catch(Exception e)
							{
								//do nothing
							}
						}
						return obj;
					}
				})
	, transformer);
		
		
		get("/sealed/list", URLTools.HEADER_JSON,(request, response) ->
				getCached(request.pathInfo(), new Callable<Object>() {
		
					@Override
					public List<SealedStock> call() throws Exception {
						return getEnabledPlugin(MTGDao.class).listSealedStocks();
					}
				})
		, transformer);
		
		
		get("/sealed/list/:collection", URLTools.HEADER_JSON,(request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
	
				@Override
				public List<SealedStock> call() throws Exception {
					return getEnabledPlugin(MTGDao.class).listSealedStocks().stream().filter(ss->ss.getMagicCollection().getName().equalsIgnoreCase(request.params(COLLECTION))).toList();
				}
			})
			, transformer);
		
		get("/sealed/sets/:collection", URLTools.HEADER_JSON,(request, response) ->
		
		 getCached(request.pathInfo(), new Callable<Object>() {
			@Override
			public List<MagicEdition> call() throws Exception {
				return getEnabledPlugin(MTGDao.class).listSealedStocks().stream().filter(ss->ss.getMagicCollection().getName().equalsIgnoreCase(request.params(COLLECTION))).map(mp->mp.getProduct().getEdition()).distinct().sorted().toList();
			}
		})
		 , transformer);
		
		get("/sealed/list/:collection/:idSet", URLTools.HEADER_JSON, (request, response) ->
		getCached(request.pathInfo(), new Callable<Object>() {
			@Override
			public List<SealedStock> call() throws Exception {
				return getEnabledPlugin(MTGDao.class).listSealedStocks(new MagicCollection(request.params(COLLECTION)),new MagicEdition(request.params(ID_SET)));
			}
		})
	, transformer);
		
		get("/sealed/get/:id", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGDao.class).getSealedStockById(Integer.parseInt(request.params(":id"))), transformer);
		
		get("/stock/list", URLTools.HEADER_JSON,(request, response) -> { 
			
			if(cache.getItem(request.pathInfo())==null)
				cache.put(getEnabledPlugin(MTGDao.class).listStocks(),request.pathInfo());
			
			return cache.getItem(request.pathInfo());
			
		}, transformer);

		get("/stock/get/:idStock", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGDao.class).getStockById(Integer.parseInt(request.params(":idStock"))), transformer);
		
		get("/stock/list/:collection", URLTools.HEADER_JSON,(request, response) ->
			 getCached(request.pathInfo(), new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return getEnabledPlugin(MTGDao.class).listStocks(List.of(new MagicCollection(request.params(COLLECTION))));
				}
			})
		, transformer);
	
		
		get("/stock/sets/:collection", URLTools.HEADER_JSON,(request, response) ->
			
			 getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public List<MagicEdition> call() throws Exception {
					return getEnabledPlugin(MTGDao.class).listStocks(List.of(new MagicCollection(request.params(COLLECTION)))).stream().map(mcs->mcs.getProduct().getEdition()).distinct().sorted().toList();
				}
			})
		, transformer);
	
		
		get("/stock/list/:collection/:idSet", URLTools.HEADER_JSON, (request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public List<MagicCardStock> call() throws Exception {
					return getEnabledPlugin(MTGDao.class).listStocks(List.of(new MagicCollection(request.params(COLLECTION)))).stream().filter(mcs->mcs.getProduct().getEdition().getId().equalsIgnoreCase(request.params(ID_SET))).toList();
				}
			})
		, transformer);
		
		get("/stock/searchCard/:collection/:cardName", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGDao.class).listStocks(request.params(":cardName"),List.of(new MagicCollection(request.params(COLLECTION)))), transformer);
		
		get("/dash/collection", URLTools.HEADER_JSON, (request, response) -> {
			List<MagicEdition> eds = getEnabledPlugin(MTGCardsProvider.class).listEditions();
			var model = new MagicEditionsTableModel();
			model.init(eds);

			var arr = new JsonArray();
			double pc = 0;
			for (MagicEdition ed : eds) {
				var obj = new JsonObject();
				obj.add("edition", converter.toJsonElement(ed));
				obj.addProperty("set", ed.getId());
				obj.addProperty("name", ed.getSet());
				obj.addProperty("release", ed.getReleaseDate());
				obj.add("qty", new JsonPrimitive(model.getMapCount().get(ed)));
				obj.add("cardNumber", new JsonPrimitive(ed.getCardCount()));
				obj.addProperty("defaultLibrary", MTGControler.getInstance().get("default-library"));
				pc = 0;
				if (ed.getCardCount() > 0)
					pc = (double) model.getMapCount().get(ed) / ed.getCardCount();
				else
					pc = (double) model.getMapCount().get(ed) / 1;

				obj.add("pc", new JsonPrimitive(pc));

				arr.add(obj);
			}
			return arr;
		}, transformer);

		
		get("/dash/variations/card/:idCards", URLTools.HEADER_JSON, (request, response) -> {
			return getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					
					var dash = getEnabledPlugin(MTGDashBoard.class);
					
					MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardById(request.params(ID_CARDS));
					var ret = new JsonObject();
					var resNormal = dash.getPriceVariation(mc,false);
					var resFoil = dash.getPriceVariation(mc,true);
					ret.addProperty("currency", MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getSymbol());
					ret.add("normal", build(resNormal));
					ret.add("foil", build(resFoil));
					ret.addProperty("provider",dash.getName());
					ret.addProperty("dateUpdate",dash.getUpdatedDate().toInstant().toEpochMilli());
					return ret;
					
				}});
			
			
		
		});
		
		
		get("/dash/edition/:idEd", URLTools.HEADER_JSON, (request, response) -> {
			var ed = new MagicEdition();
			ed.setId(request.params(ID_ED));
			return getEnabledPlugin(MTGDashBoard.class).getShakesForEdition(ed);
		}, transformer);

		get("/dash/format/:format", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGDashBoard.class).getShakerFor(MagicFormat.FORMATS.valueOf(request.params(":format"))), transformer);

		get("/pics/cards/:idEd/:cardNumber", URLTools.HEADER_JSON, (request, response) -> {
			var baos = new ByteArrayOutputStream();
			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(request.params(":cardNumber"), request.params(ID_ED));
			BufferedImage im = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc);
			ImageTools.write(im, "png", baos);
			
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			response.type("image/png");

			return imageInByte;
		});

		get("/decks/list", URLTools.HEADER_JSON, (request, response) -> {

			
			return getCached(request.pathInfo(), new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					var arr = new JsonArray();
					
					for (MagicDeck d : manager.listDecks()) {
						var el = converter.toJsonDeck(d);
							  el.remove("main");
							  el.remove("sideboard");
						arr.add(el);
					}
					return arr;
				}
				 
				 
				 
			 });
		}, transformer);

		get("/deck/:idDeck", URLTools.HEADER_JSON,(request, response) -> {
			
				var d = manager.getDeck(Integer.parseInt(request.params(":idDeck")));
				var el= converter.toJsonDeck(d);
				el.getAsJsonObject().addProperty("colors", d.getColors());
				
				return el;
		},transformer);

		get("/deck/stats/:idDeck", URLTools.HEADER_JSON, (request, response) -> {

			MagicDeck d = manager.getDeck(Integer.parseInt(request.params(":idDeck")));

			var obj = new JsonObject();

			obj.add("cmc", converter.toJsonElement(manager.analyseCMC(d.getMainAsList())));
			obj.add("types", converter.toJsonElement(manager.analyseTypes(d.getMainAsList())));
			obj.add("rarity", converter.toJsonElement(manager.analyseRarities(d.getMainAsList())));
			obj.add("colors", converter.toJsonElement(manager.analyseColors(d.getMainAsList())));
			obj.add("legalities", converter.toJsonElement(manager.analyseLegalities(d)));
			obj.add("drawing", converter.toJsonElement(manager.analyseDrawing(d)));
			return obj;

		}, transformer);


		get("/admin/recognize/caching/:setId", URLTools.HEADER_JSON, (request, response) -> {
			MTG.getEnabledPlugin(MTGCardRecognition.class).downloadCardsData(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(":setId")));
			return "OK";
		}, transformer);
		
		get("/admin/qwartz", URLTools.HEADER_JSON, (request, response) -> {
			var serv = (QwartzServer) MTG.getPlugin("Qwartz", MTGServer.class);
			return serv.toJsonDetails();
		}, transformer);
		
		get("/admin/discord", URLTools.HEADER_JSON, (request, response) -> {
			return 	TechnicalServiceManager.inst().getDiscordInfos();
		}, transformer);
		
		get("/admin/currency", URLTools.HEADER_JSON, (request, response) -> {
			MTGControler.getInstance().getCurrencyService().clean();
			return MTGControler.getInstance().getCurrencyService().getChanges();
			
		}, transformer);
		
		get("/admin/caches", URLTools.HEADER_JSON, (request, response) -> {
			return cache.entries().keySet();
		}, transformer);
		
		get("/admin/jdbc", URLTools.HEADER_JSON, (request, response) -> {
			return TechnicalServiceManager.inst().getDaoInfos();
		}, transformer);
		
		get("/admin/jsonQueries", URLTools.HEADER_JSON, (request, response) -> {
			return TechnicalServiceManager.inst().getJsonInfo();
		}, transformer);
		
		get("/admin/threads", URLTools.HEADER_JSON, (request, response) -> {
			return ThreadManager.getInstance().toJson();
		}, transformer);
		
		get("/admin/network", URLTools.HEADER_JSON, (request, response) -> {
			return TechnicalServiceManager.inst().getNetworkInfos();
		}, transformer);
		
		get("/admin/clearCache", URLTools.HEADER_JSON, (request, response) -> {
			clearCache();
			return "ok";
		}, transformer);
		
		get("/admin/plugins/list", URLTools.HEADER_JSON, (request, response) -> {
			var obj = new JsonObject();
			PluginRegistry.inst().entrySet().forEach(entry->obj.add(entry.getValue().getType().name(), converter.convert(listPlugins(entry.getKey()))));
			return obj;
		}, transformer);
		
		
		get("/admin/reindexation", URLTools.HEADER_JSON, (request, response) -> {
			Chrono c = new Chrono();
						 c.start();
				MTG.getEnabledPlugin(MTGCardsIndexer.class).initIndex();
			return "done in " + c.stop() +" s";
		}, transformer);
		
		
		get("/webshop/config", URLTools.HEADER_JSON, (request, response) -> 
			
			 getCached(request.pathInfo(), new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					WebShopConfig conf =  MTGControler.getInstance().getWebConfig();
					conf.getContact().setPassword(null);
					return conf;
					
				}
			})
			
		, transformer);
		
		get("/track/:provider/:number", URLTools.HEADER_JSON, (request, response) -> 
		getPlugin(request.params(PROVIDER),MTGTrackingService.class).track(request.params(":number"))
	, transformer);
	
	
		
		get("/webshop/transaction/:id", URLTools.HEADER_JSON, (request, response) -> 
			MTG.getPlugin(MTGConstants.MTG_APP_NAME,MTGExternalShop.class).getTransactionById(Integer.parseInt(request.params(":id")))
		, transformer);

	
		get("/extShop/:provider/:search", URLTools.HEADER_JSON, (request, response) -> 
			getPlugin(request.params(PROVIDER),MTGExternalShop.class).listProducts(request.params(":search"))
		, transformer);
		
		get("/extShop/transactions/from/:provider", URLTools.HEADER_JSON, (request, response) -> 
			getPlugin(request.params(PROVIDER),MTGExternalShop.class).listTransaction()
		, transformer);
		
		post("/extShop/transactions/:to/save/:createProduct", URLTools.HEADER_JSON, (request, response) ->{ 
			
			MTGExternalShop extShop  = MTG.getPlugin(request.params(":to"), MTGExternalShop.class);
			
			List<Transaction> ret = converter.fromJsonList(new InputStreamReader(request.raw().getInputStream()), Transaction.class);
			var arr = new HashMap<String, List<Transaction>>();
			
			arr.put("ok", new ArrayList<>());
			arr.put(ERROR, new ArrayList<>());
			
			for(Transaction p : ret)
				{
				try {
					extShop.createTransaction(p,Boolean.parseBoolean(request.params(":createProduct")));
					arr.get("ok").add(p);
				}catch(Exception e)
				{
					logger.error(e);
					arr.get(ERROR).add(p);
				}
				}
			return arr;
				
		}, transformer);
		
		
		
		post("/extShop/:from/:to/:idCategory/:language", URLTools.HEADER_JSON, (request, response) ->{ 
				
			MTGExternalShop srcShop  = MTG.getPlugin(request.params(":from"), MTGExternalShop.class);
			MTGExternalShop extShop  = MTG.getPlugin(request.params(":to"), MTGExternalShop.class);
			
			List<MTGProduct> ret = converter.fromJsonList(new InputStreamReader(request.raw().getInputStream()), MTGProduct.class);
			var arr = new JsonArray();
			for(MTGProduct p : ret)
				{
					Category c = extShop.listCategories().stream().filter(cat->cat.getIdCategory()==Integer.parseInt(request.params(":idCategory"))).findFirst().orElse(new Category());
					int res = extShop.createProduct(srcShop,p,request.params(":language"),c);
					arr.add(res);
				}
			return arr;
				
		}, transformer);
		
		

		put("/favorites/:classename/:idContact/:idAnnounce", URLTools.HEADER_JSON, (request, response) -> {
			try{ 
				MTG.getEnabledPlugin(MTGDao.class).saveFavorites(Integer.parseInt(request.params(":idContact")), Integer.parseInt(request.params(":idAnnounce")),request.params(":classename"));
				return "ok";
			}catch(Exception e)
			{
				return e;
			}
		}, transformer);

		
		delete("/favorites/:classename/:idContact/:idAnnounce", URLTools.HEADER_JSON, (request, response) -> {
			try{ 
				MTG.getEnabledPlugin(MTGDao.class).deleteFavorites(Integer.parseInt(request.params(":idContact")), Integer.parseInt(request.params(":idAnnounce")),request.params(":classename"));
				return "ok";
			}catch(Exception e)
			{
				return e;
			}
		}, transformer);
		
		get("/favorites/:classename/:idContact", URLTools.HEADER_JSON, (request, response) -> {
			try{ 
				var cont = MTG.getEnabledPlugin(MTGDao.class).getContactById(Integer.parseInt(request.params(":idContact")));
				return MTG.getEnabledPlugin(MTGDao.class).listFavorites(cont,request.params(":classename"));
			}catch(Exception e)
			{
				logger.error(e);
				return new ArrayList<>();
			}
		}, transformer);
		
		
		
		
		post("/announces/new", URLTools.HEADER_JSON, (request, response) -> {
			Announce a=converter.fromJson(new InputStreamReader(request.raw().getInputStream()), Announce.class);
			return MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateAnnounce(a);
		}, transformer);

		get("/announces/get/:id", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).getAnnounceById(Integer.parseInt(request.params(":id"))), transformer);

		get("/announces/stats", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(false).stream().collect(Collectors.groupingBy(Announce::getCategorie, Collectors.counting())), transformer);
		
		get("/announces/list", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(true), transformer);
		
		get("/announces/last/:qty", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(Integer.parseInt(request.params(":qty")),false), transformer);

		get("/announces/keyword/:search", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(URLTools.decode(request.params(":search"))), transformer);

		get("/announces/category/:type", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(EnumItems.valueOf(request.params(":type"))), transformer);
		
		get("/announces/contact/:id", URLTools.HEADER_JSON, (request, response) -> {
			var c = new Contact();
			c.setId(Integer.parseInt(request.params(":id")));
			return MTG.getEnabledPlugin(MTGDao.class).listAnnounces(c);
		}, transformer);
		
		get("/webshop/:dest/categories", URLTools.HEADER_JSON, (request, response) ->MTG.getPlugin(request.params(":dest"), MTGExternalShop.class).listCategories(), transformer);
	
		post("/webshop/user/connect", URLTools.HEADER_JSON, (request, response) ->MTG.getEnabledPlugin(MTGExternalShop.class).getContactByLogin(request.queryParams("email"),request.queryParams("password")), transformer);
		
		post("/webshop/transaction/cancel/:id", URLTools.HEADER_JSON, (request, response) -> {
			
			Contact c=converter.fromJson(request.queryParams("user"), Contact.class);
			
			var t =MTG.getEnabledPlugin(MTGExternalShop.class).getTransactionById(Integer.parseInt(request.params(":id")));
			
			if(t.getContact().getId()==c.getId())
			{
				t.setStatut(TransactionStatus.CANCELATION_ASK);
				MTG.getEnabledPlugin(MTGExternalShop.class).saveOrUpdateTransaction(t);
				return "OK";
			}
			else
			{
				return "Wrong User";	
			}
			
		}, transformer);
		
		
		post("/transaction/add", URLTools.HEADER_JSON, (request, response) -> {
			try{ 
				Transaction t=converter.fromJson(new InputStreamReader(request.raw().getInputStream()), Transaction.class);
				return TransactionService.newTransaction(t);
			}catch(Exception e)
			{
				logger.error("error reading transaction ", e);
				throw new IOException(e);
			}
			
		});
	
		post("/transaction/paid/:provider", URLTools.HEADER_JSON, (request, response) -> {
			
			Transaction t=converter.fromJson(new InputStreamReader(request.raw().getInputStream()), Transaction.class);
			TransactionService.payingTransaction(t,request.params(PROVIDER));
			
			return "ok";
		}, transformer);
		
		post("/transactions/contact", URLTools.HEADER_JSON, (request, response) -> {
			Contact c=converter.fromJson(new InputStreamReader(request.raw().getInputStream()), Contact.class);
			return MTG.getEnabledPlugin(MTGExternalShop.class).listTransactions(c);
		}, transformer);

		post("/contact/save", URLTools.HEADER_JSON, (request, response) -> {
			Contact t=converter.fromJson(new InputStreamReader(request.raw().getInputStream()), Contact.class);
			if(t.getId()<=0)
			{
				try{
					TransactionService.createContact(t);
					return t;
				}
				catch(IOException e)
				{
					response.status(500);
					return e.getMessage();
				}
			}
			else
			{
				try{
					getEnabledPlugin(MTGExternalShop.class).saveOrUpdateContact(t);
					return t;
				}
				catch(Exception e)
				{
					response.status(500);
					return e.getMessage();
				}
			}
		}, transformer);
		
		get("/contact/validation/:token", URLTools.HEADER_JSON, (request, response) -> 
			MTG.getEnabledPlugin(MTGExternalShop.class).enableContact(request.params(":token"))
		, transformer);
		
		get("/",URLTools.HEADER_HTML,(request,response) -> {
			
			var temp = new StringBuilder();
			response.type(URLTools.HEADER_HTML);
			
			Spark.routes().stream().filter(rm->rm.getHttpMethod()!=HttpMethod.after && rm.getHttpMethod()!=HttpMethod.before && rm.getHttpMethod()!=HttpMethod.options).forEach(rm->{
				temp.append(rm.getHttpMethod());
				temp.append("&nbsp;");
				temp.append("<a href='").append(rm.getMatchUri()).append("'>").append(rm.getMatchUri()).append("</a>");
				temp.append("<br/>");
			});
			
			return temp.toString();
		});		
		
		get("/share/announce/:id",URLTools.HEADER_HTML,(request,response) -> {
			response.type(URLTools.HEADER_HTML);
			var report = new ReportNotificationManager();
			var announce = MTG.getEnabledPlugin(MTGDao.class).getAnnounceById(Integer.parseInt(request.params(":id")));
			return report.generate(FORMAT_NOTIFICATION.HTML, announce, "share");
		});		
		
	}


	public void clearCache() {
		cache.clear();
	}
	
	
	private JsonArray build(HistoryPrice<MagicCard> res) {
		
		
		var arr = new JsonArray();
		
		for (Entry<Date, Double> val : res) {
			var obj = new JsonObject();
			obj.add("date", new JsonPrimitive(val.getKey().getTime()));
			obj.add("value", new JsonPrimitive(val.getValue()));

			arr.add(obj);
		}
		
		return arr;
		
	}

	@Override
	public void stop() throws IOException {
		Spark.stop();
		logger.info("Server stop");
		running = false;
	}

	@Override
	public boolean isAlive() {
		return running;
	}

	@Override
	public boolean isAutostart() {
		return getBoolean(AUTOSTART);
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
	public Icon getIcon() {
		return new ImageIcon(DiscordBotServer.class.getResource("/icons/plugins/json.png"));
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var map = new HashMap<String,String>();
		
		map.put(SERVER_PORT, "8080");
		map.put(AUTOSTART, FALSE);
		map.put(ENABLE_GZIP, FALSE);
		map.put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		map.put(ACCESS_CONTROL_REQUEST_METHOD, "GET,PUT,POST,DELETE,OPTIONS");
		map.put(ACCESS_CONTROL_ALLOW_HEADERS,"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		map.put(PASSTOKEN, "");
		map.put("THREADS",String.valueOf(Runtime.getRuntime().availableProcessors()));
		map.put(ENABLE_SSL,FALSE);
		map.put(KEYSTORE_URI, new File(MTGConstants.DATA_DIR,"jetty.jks").getAbsolutePath());
		map.put(KEYSTORE_PASS, "changeit");
		map.put(CACHE_TIMEOUT, "60");
		map.put("ENABLE_WEB_ADMIN", "true");
		return map;
	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(Spark.class, "/META-INF/maven/com.sparkjava/spark-core/pom.properties");
	}

}
