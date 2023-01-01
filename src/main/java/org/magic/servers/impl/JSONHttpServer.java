 package org.magic.servers.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;
import static org.magic.services.tools.MTG.listPlugins;
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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.magic.api.beans.Announce;
import org.magic.api.beans.Announce.STATUS;
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
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.ConverterItem;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.beans.technical.WebShopConfig;
import org.magic.api.beans.technical.audit.JsonQueryInfo;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.api.interfaces.MTGTrackingService;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.services.CardsManagerService;
import org.magic.services.CollectionEvaluator;
import org.magic.services.JWTServices;
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
import org.magic.services.recognition.area.ManualAreaStrat;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.Chrono;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.GithubUtils;
import org.magic.services.tools.IDGenerator;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.POMReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;
import spark.route.HttpMethod;
import spark.routematch.RouteMatch;

public class JSONHttpServer extends AbstractMTGServer {

	private static final String ID_DECK = ":idDeck";
	private static final String ID_ANNOUNCE = ":idAnnounce";
	private static final String DEFAULT_LIBRARY = "default-library";
	private static final String TYPE = ":type";
	private static final String SCRYFALL_ID = ":scryfallId";
	private static final String CLASSENAME = ":classename";
	private static final String ID_CONTACT = ":idContact";
	private static final String PAGE = "page";
	private static final String PAGINATE = "paginate";
	private static final String ERROR = "error";
	private static final String CLASS = ":class";
	private static final String PROVIDER = ":provider";
	private static final String COLLECTION = ":collection";
	private static final String ID_SET = ":idSet";
	private static final String ENABLE_SSL = "ENABLE_SSL";
	private static final String NAME = ":name";
	private static final String ID_ED = ":idEd";
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
	private JsonExport converter;
	private UserAgentAnalyzer ua ;
	private JWTServices jwtService;
	private static final String robotVars="""
			\tUser-agent: Googlebot
			\tDisallow: /
			\tUser-agent: googlebot-image
			\tDisallow: /
			\tUser-agent: googlebot-mobile
			\tDisallow: /
			\tUser-agent: MSNBot
			\tDisallow: /
			\tUser-agent: Slurp
			\tDisallow: /
			\tUser-agent: Teoma
			\tDisallow: /
			\tUser-agent: Gigabot
			\tDisallow: /
			\tUser-agent: Robozilla
			\tDisallow: /
			\tUser-agent: Nutch
			\tDisallow: /
			\tUser-agent: ia_archiver
			\tDisallow: /
			\tUser-agent: baiduspider
			\tDisallow: /
			\tUser-agent: naverbot
			\tDisallow: /
			\tUser-agent: yeti
			\tDisallow: /
			\tUser-agent: yahoo-mmcrawler
			\tDisallow: /
			\tUser-agent: psbot
			\tDisallow: /
			\tUser-agent: yahoo-blogs/v3.9
			\tDisallow: /
			\tUser-agent: *
			\tDisallow: /
			\tDisallow: /cgi-bin/
			\t""";


	private JsonObject error(Request req, Response response, String msg, int code) {
		response.status(code);

		var obj = new JsonObject();
			obj.addProperty("method", req.requestMethod());
			obj.addProperty("uri", req.pathInfo());
			obj.addProperty("code", code);
			obj.addProperty(ERROR, msg);

		return obj;
	}

	private JsonObject ok(Request req, Response response, Object msg) {
		response.status(200);

		var obj = new JsonObject();
			obj.addProperty("method", req.requestMethod());
			obj.addProperty("uri", req.pathInfo());
			obj.addProperty("code", 200);
			obj.addProperty("msg", converter.toJson(msg));

		return obj;
	}

	private void storeToken(Response response, String string)
	{
		response.cookie("x-auth-token", string);
	}

	private String readToken(Request request)
	{
		return request.cookie("x-auth-token");
	}

	public JSONHttpServer() {
		manager = new MTGDeckManager();
		converter = new JsonExport();

		if(!getBoolean("PRETTY_PRINT"))
			converter.removePrettyString();

		ua = UserAgentAnalyzer.newBuilder().build();
		transformer = new ResponseTransformer() {
			@Override
			public String render(Object model) throws Exception {
				return converter.toJson(model);
			}
		};
	}



	@Override
	public void start() throws IOException {

		if(getBoolean(ENABLE_SSL))
			Spark.secure(getString(KEYSTORE_URI), getString(KEYSTORE_PASS), null, null);

			initCache();
			initVars();
			initRoutes();
			Spark.init();

			jwtService = new JWTServices(getString("JWT_SECRET"), MTGConstants.MTG_APP_NAME);

			running = true;
			logger.info("Server {} started on port {}",getName(),getInt(SERVER_PORT));
		}


	private void addInfo(Request request, Response response) {

		if(!request.uri().startsWith("/admin"))
		{
			var info= new JsonQueryInfo();
			info.setStart(Instant.ofEpochMilli(Long.parseLong(response.raw().getHeader("startAt"))));
			info.setContentType(request.contentType());
			info.setIp(request.ip());
			info.setMethod(request.requestMethod());
			info.setUrl(request.uri());
			info.setParameters(request.params());
			info.setQuery(request.queryParams().stream().collect(Collectors.toMap(q->q, request::queryParams)));
			info.setSessionId(request.session().id());
			info.setPath(request.servletPath());
			info.setAttributs(request.attributes().stream().collect(Collectors.toMap(a->a, request::attribute)));
			info.setHeaders(request.headers().stream().collect(Collectors.toMap(s->s,request::headers)));
			info.setStatus(response.status());
			info.setUserAgent(ua.parse(request.userAgent()));
			info.setEnd(Instant.now());
			TechnicalServiceManager.inst().store(info);
		}
	}

	private <T> List<T> paginate(List<T> elements, int pageNumber, int size)
	{
		 int skipCount = (pageNumber - 1) * size;
		 return elements.stream().skip(skipCount).limit(size).toList();
	}

	private void initVars() {

		Spark.

		threadPool(getInt("THREADS"));

		port(getInt(SERVER_PORT));

		initExceptionHandler(e -> {
			running = false;
			logger.error(e);
		});

		exception(Exception.class, (Exception exception, Request request, Response response) -> {
			logger.error("{} : {} ",request.uri(),exception.getMessage(), exception);
			response.body(error(request,response, exception.getMessage(),500).toString());
			addInfo(request,response);
		});

		notFound((req, res) -> {
			addInfo(req,res);
			return error(req, res,"Not Found",404);
		});

		before("/*", (request, response) -> {
			response.type(URLTools.HEADER_JSON);
			response.header(ACCESS_CONTROL_ALLOW_ORIGIN, getString(ACCESS_CONTROL_ALLOW_ORIGIN));
			response.header(ACCESS_CONTROL_REQUEST_METHOD, getString(ACCESS_CONTROL_REQUEST_METHOD));
			response.header(ACCESS_CONTROL_ALLOW_HEADERS, getString(ACCESS_CONTROL_ALLOW_HEADERS));
			response.header("Content-Security-Policy","");
			response.header("startAt", String.valueOf(Instant.now().toEpochMilli()));

			if (getBoolean(ENABLE_GZIP)) {
				response.header("Content-Encoding", "gzip");
			}

		});


		after(this::addInfo);

		options("/*", (request, response) -> {
			var accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header(ACCESS_CONTROL_ALLOW_HEADERS, accessControlRequestHeaders);
			}
			var accessControlRequestMethod = request.headers(ACCESS_CONTROL_REQUEST_METHOD);
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}
			return "OK";
		});



	}


	@SuppressWarnings("unchecked")
	private void initRoutes() {

		post("/services/auth",(request, response) -> {
			var c = MTG.getEnabledPlugin(MTGExternalShop.class).getContactByLogin(request.queryParams("email"),request.queryParams("password"));
			var obj = new JsonObject();
			var m = new HashMap<String,Object>();
				m.put("name", c.getName());
				m.put("mail", c.getEmail());
				m.put("sub", c.getId());

			obj.addProperty("accessToken",jwtService.generateToken(m,getInt("JWT_EXPIRATION_MINUTES"),false));
			obj.addProperty("refreshToken",jwtService.generateToken(m,getInt("JWT_REFRESH_EXPIRATION_MINUTES"),true));

			storeToken(response,obj.get("accessToken").getAsString());

			return obj;
		},transformer);


		get("/services/auth",(request, response) -> jwtService.validateToken(readToken(request)),transformer);




		get("/cards/token/:scryfallId", URLTools.HEADER_JSON,(request, response) -> {

			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));

			if(mc!=null) {
				return getEnabledPlugin(MTGTokensProvider.class).generateTokenFor(mc);
			}

			return null;
		},transformer);

		get("/cards/suggestcard/:val", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGCardsIndexer.class).search("name:"+request.params(":val").replace(" ", " AND ")+"*").stream().map(MagicCard::toLightJson).toList(),
				transformer);


		get("/cards/moreLike/:scryfallId", URLTools.HEADER_JSON,(request, response) -> {
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
			return getEnabledPlugin(MTGCardsIndexer.class).similarity(mc).keySet().stream() .filter(c->!c.getName().equals(mc.getName()))
																							.map(MagicCard::toLightJson)
																							.toList();
		},transformer);


		//used only in chromeplugin
		get("/cards/light/:name", URLTools.HEADER_JSON,(request, response) -> {
			List<MagicCard> list= getEnabledPlugin(MTGCardsProvider.class).searchCardByName(request.params(NAME), null, true);
			var arr = new JsonArray();

			for(MagicCard mc : list)
			{
				var cols = getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc);
				var obj = mc.toLightJson();
				obj.add("collections", converter.toJsonElement(cols));
				arr.add(obj);
			}
			return arr;

		},transformer);



		post("/ged/uploadPic/:class/:id", URLTools.HEADER_JSON,(request, response) -> {
			var buffImg = ImageTools.readBase64(request.body().substring(request.body().indexOf(",")+1));// Find better solution
			if(buffImg==null)
				return error(request, response, "No readable Image",500);


			var id = request.params(":id");

			if(request.params(CLASS).equals("MagicCard"))
				id = IDGenerator.generate(getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(":id")));



			var entry = new GedEntry<>(ImageTools.toByteArray(buffImg),PluginRegistry.inst().loadClass("org.magic.api.beans."+request.params(CLASS)),id,"webupload_"+Instant.now().toEpochMilli()+".png");
			MTG.getEnabledPlugin(MTGGedStorage.class).store(entry);
			return ok(request,response,"Picture uploaded");
		});




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
					logger.debug("Uploading {}",fi);
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


		get("/orders/id/:id", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGDao.class).getOrderById(Integer.parseInt(request.params(":id"))), transformer);

		get("/orders/list/:idTransaction", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGDao.class).listOrdersByIdTransaction(request.params(":idTransaction")), transformer);

		get("/orders/list", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGDao.class).listOrders(), transformer);


		post("/orders/new", URLTools.HEADER_JSON, (request, response) ->{
			var a=converter.fromJson(new InputStreamReader(request.raw().getInputStream()), OrderEntry.class);
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateOrderEntry(a);
			return ok(request,response,a.getId());
		}, transformer);



		get("/cards/scryfall/:scryfallId", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID)), transformer);

		post("/cards/import/:provider", URLTools.HEADER_JSON,(request, response) -> {
			var content = request.body();
			return converter.toJsonDeck(MTG.getPlugin(request.params(PROVIDER).trim(),MTGCardsExport.class).importDeck(content, "webimport"));
		}, transformer);

		post("/cards/recognize/:threeshold", URLTools.HEADER_JSON, (request, response) -> {
			var recog = MTG.getEnabledPlugin(MTGCardRecognition.class);
			var strat = new ManualAreaStrat();
			var buffImg = ImageTools.readBase64(request.body().substring(request.body().indexOf(",")+1));// Find better solution

			if(buffImg==null)
				return "No readable Image";

			recog.loadAllCachedData();
			return strat.recognize(buffImg,recog,Integer.parseInt(request.params(":threeshold")));
		}, transformer);

		put("/cards/move/:from/:to/:scryfallId", URLTools.HEADER_JSON, (request, response) -> {
			var from = new MagicCollection(request.params(":from"));
			var to = new MagicCollection(request.params(":to"));
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
			getEnabledPlugin(MTGDao.class).moveCard(mc, from,to);
			return ok(request,response,mc + " is moved from " + from + " to " + to);
		}, transformer);

		put("/cards/add/:scryfallId", URLTools.HEADER_JSON, (request, response) -> {
			var from = new MagicCollection(MTGControler.getInstance().get(DEFAULT_LIBRARY));
			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
			CardsManagerService.saveCard(mc, from,null);
			return ok(request,response,mc + " is added to " + from);
		}, transformer);

		put("/cards/add/:to/:scryfallId", URLTools.HEADER_JSON, (request, response) -> {
			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(":id"));
			CardsManagerService.saveCard(mc, new MagicCollection(request.params(":to")),null);
			return ok(request,response,mc + " is added to " + request.params(":to"));
		}, transformer);


		get("/cards/list/:col", URLTools.HEADER_JSON, (request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public List<MagicCard> call() throws Exception {
						var col = new MagicCollection(request.params(":col"));
						return getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, null);
				}
			})
		, transformer);

		get("/cards/list/:col/:idEd", URLTools.HEADER_JSON, (request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public List<MagicCard> call() throws Exception {
					var col = new MagicCollection(request.params(":col"));
					var ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(ID_ED));
					return getEnabledPlugin(MTGDao.class).listCardsFromCollection(col, ed);
				}
				})
		, transformer);

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

		get("/collections/default", URLTools.HEADER_JSON,
				(request, response) -> MTGControler.getInstance().get(DEFAULT_LIBRARY), transformer);

		
		
		get("/collections/cards/:scryfallId", URLTools.HEADER_JSON, (request, response) -> {
			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
			return getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc);
		}, transformer);

		put("/collections/add/:name", URLTools.HEADER_JSON, (request, response) -> {
			getEnabledPlugin(MTGDao.class).saveCollection(request.params(NAME));
			return ok(request,response,NAME + " is added");
		});

		get("/editions/list", URLTools.HEADER_JSON,(request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {

				@Override
				public List<MagicEdition> call() throws Exception {
					var list = getEnabledPlugin(MTGCardsProvider.class).listEditions();
					Collections.sort(list);
					return list;

				}
			})
		, transformer);

		get("/editions/:idSet", URLTools.HEADER_JSON, (request, response) -> getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(ID_SET)), transformer);


		get("/editions/list/:colName", URLTools.HEADER_JSON, (request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {

				@Override
				public List<MagicEdition> call() throws Exception{
					var eds = new ArrayList<MagicEdition>();
					var list = getEnabledPlugin(MTGDao.class).listEditionsIDFromCollection(new MagicCollection(request.params(":colName")));
					for (String s : list)
						eds.add(getEnabledPlugin(MTGCardsProvider.class).getSetById(s));

					Collections.sort(eds);
					return eds;
				}

			})
		, transformer);


		post("/prices/wizard/:provider",URLTools.HEADER_JSON, (request, response) -> {
			var list = converter.fromJsonList(new InputStreamReader(request.raw().getInputStream()), MagicCard.class);
			return MTG.getPlugin(request.params(PROVIDER).trim(),MTGPricesProvider.class).getPricesBySeller(list);
		}, transformer);

		get("/prices/:scryfallId", URLTools.HEADER_JSON, (request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {

				@Override
				public List<MagicPrice> call() throws Exception {
					MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId( request.params(SCRYFALL_ID));
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

		get("/prices/details/:provider/:scryfallId", URLTools.HEADER_JSON, (request, response) ->
				getCached(request.pathInfo(), new Callable<Object>() {

					@Override
					public List<MagicPrice> call() throws Exception {
						MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
						return MTG.getPlugin(request.params(PROVIDER).trim(),MTGPricesProvider.class).getPrice(mc);
					}
				})
		, transformer);

		get("/partner/:scryfallId", URLTools.HEADER_JSON, (request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public List<MagicPrice> call() throws Exception {
					MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
					return MTG.listEnabledPlugins(MTGPricesProvider.class).stream().filter(MTGPlugin::isPartner).map(pricer->pricer.getBestPrice(mc)).toList();
				}
			})
		, transformer);

		get("/alerts/list", URLTools.HEADER_JSON,(request, response) -> getEnabledPlugin(MTGDao.class).listAlerts(), transformer);

		get("/alerts/:scryfallId", URLTools.HEADER_JSON, (request, response) -> {
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
			return getEnabledPlugin(MTGDao.class).hasAlert(mc);

		}, transformer);

		delete("/alerts/:scryfallId", URLTools.HEADER_JSON, (request, response) -> {
			var alert = getEnabledPlugin(MTGDao.class).listAlerts().stream().filter(mca->mca.getCard().getScryfallId().equals(request.params(SCRYFALL_ID))).findFirst().orElse(null);

			if(alert==null)
				throw new NullPointerException("No alert with id="+request.params(":id"));

			getEnabledPlugin(MTGDao.class).deleteAlert(alert);
			return ok(request,response,alert);

		}, transformer);

		put("/alerts/update/:scryfallId", URLTools.HEADER_JSON, (request, response) -> {
			var alert = getEnabledPlugin(MTGDao.class).listAlerts().stream().filter(mca->mca.getCard().getScryfallId().equals(request.params(SCRYFALL_ID))).findFirst().orElse(null);

			if(alert==null)
				throw new NullPointerException("No alert with scryfallId="+request.params(SCRYFALL_ID));

			JsonObject postItems= readJsonObject(request);

			alert.setPrice(postItems.get("bid").getAsDouble());
			alert.setQty(postItems.get("qty").getAsInt());
			alert.setFoil(postItems.get("foil").getAsBoolean());

			getEnabledPlugin(MTGDao.class).updateAlert(alert);
			return ok(request,response,alert);

		}, transformer);




		post("/alerts/add/:scryfallId", (request, response) -> {
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
			var alert = new MagicCardAlert();
			alert.setCard(mc);
			alert.setPrice(0.0);
			getEnabledPlugin(MTGDao.class).saveAlert(alert);
			return alert;
		}, transformer);



		put("/stock/:type/update", (request, response) -> {
			JsonObject postItems= readJsonObject(request);

			var source = request.params(TYPE);


			MTGStockItem obj = null;

			if(source.equals("sealed"))
				obj = getEnabledPlugin(MTGDao.class).getSealedStockById(postItems.get("id").getAsLong());
			else
				obj = getEnabledPlugin(MTGDao.class).getStockById(postItems.get("id").getAsLong());


			if(obj==null)
				throw new NullPointerException("no item found with id="+postItems.get("id"));


				  obj.setQte(postItems.get("qty").getAsInt());
				  obj.setPrice(postItems.get("price").getAsDouble());
				  obj.setCondition(EnumCondition.valueOf(postItems.get("condition").getAsString()));
				  obj.setLanguage(postItems.get("language").getAsString());
				  obj.setMagicCollection(new MagicCollection(postItems.get("collection").getAsString()));
				  obj.setFoil(postItems.get("foil").getAsBoolean());
				  obj.setAltered(postItems.get("altered").getAsBoolean());
				  obj.setEtched(postItems.get("etched").getAsBoolean());
				  obj.setSigned(postItems.get("signed").getAsBoolean());
				  obj.setComment(postItems.get("comment").getAsString());

				  getEnabledPlugin(MTGDao.class).saveOrUpdateStock(obj);

			return obj;
		});


		post("/stock/add/:scryfallId", (request, response) -> {
			var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
			var stock = MTGControler.getInstance().getDefaultStock();
			stock.setQte(1);
			stock.setProduct(mc);

			getEnabledPlugin(MTGDao.class).saveOrUpdateCardStock(stock);
			return ok(request,response,stock);
		});


		delete("/stock/:idStock", (request, response) -> {
			var stock = getEnabledPlugin(MTGDao.class).getStockById(Long.parseLong(request.params(":idStock")));
			getEnabledPlugin(MTGDao.class).deleteStock(stock);
			return ok(request,response,stock);
		});


		post("/stock/sync/:from/:to", (request, response) -> {


			List<MTGStockItem> postItems= converter.fromJsonList(new InputStreamReader(request.raw().getInputStream()), MTGStockItem.class);

			if(postItems.size()<2)
				throw new IOException("please choose source and destination product");

			var src = postItems.get(0);
			var dest = postItems.get(1);

			var item = new ConverterItem(request.params(":from"),request.params(":to"),src.getProduct().getName(),src.getId(),dest.getId());

			getEnabledPlugin(MTGDao.class).saveOrUpdateConversionItem(item);
			return ok(request,response,item);
		});

		get("/sealed/list", URLTools.HEADER_JSON,(request, response) ->{
			var data=(List<SealedStock>)getCached(request.pathInfo(), new Callable<Object>() {

					@Override
					public List<SealedStock> call() throws Exception {
						return getEnabledPlugin(MTGDao.class).listSealedStocks();
					}
				});

			if(request.queryParams(PAGE)!=null && request.queryParams(PAGINATE)!=null)
				return paginate(data,Integer.parseInt(request.queryParams(PAGE)),Integer.parseInt(request.queryParams(PAGINATE)));
			else
				return data;


		}, transformer);


		get("/sealed/list/:collection", URLTools.HEADER_JSON,(request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {

				@Override
				public List<SealedStock> call() throws Exception {
					return getEnabledPlugin(MTGDao.class).listSealedStocks().stream().filter(ss->ss.getMagicCollection().getName().equalsIgnoreCase(request.params(COLLECTION))).toList();
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
				(request, response) -> getEnabledPlugin(MTGDao.class).getSealedStockById(Long.parseLong(request.params(":id"))), transformer);

		get("/stock/list", URLTools.HEADER_JSON,(request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return getEnabledPlugin(MTGDao.class).listStocks();
				}
			})
		, transformer);

		get("/stock/get/:idStock", URLTools.HEADER_JSON,
				(request, response) -> getEnabledPlugin(MTGDao.class).getStockById(Long.parseLong(request.params(":idStock"))), transformer);



		get("/stock/list/:collection", URLTools.HEADER_JSON,(request, response) ->{

			var data = (List<MagicCardStock>) getCached(request.pathInfo(), new Callable<Object>() {
								@Override
								public Object call() throws Exception {
								return getEnabledPlugin(MTGDao.class).listStocks(List.of(new MagicCollection(request.params(COLLECTION))));
							}
						});

			if(request.queryParams(PAGE)!=null && request.queryParams(PAGINATE)!=null)
				return paginate(data,Integer.parseInt(request.queryParams(PAGE)),Integer.parseInt(request.queryParams(PAGINATE)));
			else
				return data;

		},transformer);


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

		get("/dash/collection", URLTools.HEADER_JSON, (request, response) ->CollectionEvaluator.analyseToJson(new MagicCollection(MTGControler.getInstance().get(DEFAULT_LIBRARY))), transformer);

		get("/dash/collection/:collection", URLTools.HEADER_JSON, (request, response) -> CollectionEvaluator.analyseToJson(new MagicCollection(request.params(COLLECTION))), transformer);


		get("/dash/variations/card/:scryfallId", URLTools.HEADER_JSON, (request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public Object call() throws Exception {

					var dash = getEnabledPlugin(MTGDashBoard.class);

					MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(request.params(SCRYFALL_ID));
					var ret = new JsonObject();
					var resNormal = dash.getPriceVariation(mc,false);
					var resFoil = dash.getPriceVariation(mc,true);
					ret.addProperty("currency", MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getSymbol());
					ret.add("normal", build(resNormal));
					ret.add("foil", build(resFoil));
					ret.addProperty("provider",dash.getName());
					ret.addProperty("dateUpdate",dash.getUpdatedDate().toInstant().toEpochMilli());
					return ret;

				}})
		);


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

		get("/decks/list", URLTools.HEADER_JSON, (request, response) ->
			getCached(request.pathInfo(), new Callable<Object>() {
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



			 })
		, transformer);

		post("/decks/import", URLTools.HEADER_JSON, (request, response) ->{

			var obj = request.queryParamsSafe("url");

			var d = new RetrievableDeck();
				d.setUrl(URI.create(obj));
				d.setName(request.queryParamsSafe("name"));
			var deck = getPlugin(request.queryParamsSafe("provider"),MTGDeckSniffer.class).getDeck(d);

			manager.saveDeck(deck);

			return deck;
		}
		, transformer);

		get("/deck/export/:provider/"+ID_DECK, (request, response) -> {
			var plug = getPlugin(request.params(PROVIDER),MTGCardsExport.class);
			var d = manager.getDeck(Integer.parseInt(request.params(ID_DECK)));
			
			if(d==null)
				return error(request, response, "Error getting deck with id="+request.params(ID_DECK), 500);
			
			var p =FileTools.createTempFile("deck",plug.getFileExtension());
			var f = p.toFile();
			var ct = Files.probeContentType(p)==null?"text/plain":Files.probeContentType(p);
			plug.exportDeck(d, f);
			response.raw().setContentType(ct);
			
			if(ImageTools.isImage(p))
			{
				var b = Files.readAllBytes(p);
				response.raw().getOutputStream().write(b);
				return response;
			}
			else
			{
				return FileTools.readFile(f);
			}
		});




		delete("/deck/"+ID_DECK, URLTools.HEADER_JSON,(request, response) -> {

			var d = manager.getDeck(Integer.parseInt(request.params(ID_DECK)));
			manager.remove(d);
			return ok(request,response,d);
		},transformer);

		get("/deck/:idDeck", URLTools.HEADER_JSON,(request, response) -> {

				var d = manager.getDeck(Integer.parseInt(request.params(ID_DECK)));
				var el= converter.toJsonDeck(d);
				el.getAsJsonObject().addProperty("colors", d.getColors());

				return el;
		},transformer);


		get("/deck/filters/:provider", URLTools.HEADER_JSON,(request, response) -> {
			var plug = getPlugin(request.params(PROVIDER),MTGDeckSniffer.class);
			return plug.listFilter();
		},transformer);


		get("/deck/search/:provider/:filter", URLTools.HEADER_JSON,(request, response) ->
			getPlugin(request.params(PROVIDER),MTGDeckSniffer.class).getDeckList(request.params(":filter"))
		,transformer);



		get("/deck/stats/:idDeck", URLTools.HEADER_JSON, (request, response) -> {

			MagicDeck d = manager.getDeck(Integer.parseInt(request.params(ID_DECK)));

			var obj = new JsonObject();

			obj.add("cmc", converter.toJsonElement(manager.analyseCMC(d.getMainAsList())));
			obj.add("types", converter.toJsonElement(manager.analyseTypes(d.getMainAsList())));
			obj.add("rarity", converter.toJsonElement(manager.analyseRarities(d.getMainAsList())));
			obj.add("colors", converter.toJsonElement(manager.analyseColors(d.getMainAsList())));
			obj.add("legalities", converter.toJsonElement(manager.analyseLegalities(d)));
			obj.add("drawing", converter.toJsonElement(manager.analyseDrawing(d)));
			return obj;

		}, transformer);

		get("/metadata/recognition/list", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGCardRecognition.class).getDataList(), transformer);


		get("/metadata/recognition/download/:idSet", URLTools.HEADER_JSON, (request, response) -> {
			MTG.getEnabledPlugin(MTGCardRecognition.class).downloadCardsData(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(ID_SET)));
			return ok(request,response,"recognition list for " + request.params(ID_SET) + " downloaded");
		}, transformer);

		get("/metadata/conditions", URLTools.HEADER_JSON,(request, response) -> EnumCondition.values(), transformer);

		get("/metadata/keywords", URLTools.HEADER_JSON, (request, response) -> AbstractKeyWordsManager.getInstance().toJson(), transformer);

		get("/metadata/categories", URLTools.HEADER_JSON, (request, response) -> EnumItems.values(), transformer);

		get("/metadata/git", URLTools.HEADER_JSON, (request, response) -> GithubUtils.inst().getReleases(), transformer);
		
		get("/metadata/version", "text", (request, response) ->
			 getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public String call() throws Exception {
					return new VersionChecker().getVersion();
				}
			})
		);


		get("/admin/qwartz", URLTools.HEADER_JSON, (request, response) -> {
			var serv = (QwartzServer) MTG.getPlugin("Qwartz", MTGServer.class);
			return serv.toJsonDetails();
		}, transformer);

		get("/admin/discord", URLTools.HEADER_JSON, (request, response) -> {
			var ret = new JsonObject();
			var serv = (DiscordBotServer) MTG.getPlugin("Discord", MTGServer.class);
			ret.add("server", serv.toJsonDetails());
			ret.add("queries",converter.toJsonElement(TechnicalServiceManager.inst().getDiscordInfos()));
			return ret;
		}, transformer);

		get("/admin/currency", URLTools.HEADER_JSON, (request, response) -> {
			MTGControler.getInstance().getCurrencyService().clean();
			return MTGControler.getInstance().getCurrencyService().getChanges();

		}, transformer);

		get("/admin/files", URLTools.HEADER_JSON, (request, response) ->TechnicalServiceManager.inst().getFileInfos(), transformer);
		
		get("/admin/caches", URLTools.HEADER_JSON, (request, response) -> {
			JsonArray arr = new JsonArray();

			getCache().entries().keySet().forEach(s->{

					var obj = new JsonObject();
						  obj.addProperty("url",s);
						  obj.addProperty("size",s.length());
						  arr.add(obj);
			});

			return arr;
		}, transformer);

		get("/admin/jdbc", URLTools.HEADER_JSON, (request, response) -> TechnicalServiceManager.inst().getDaoInfos(), transformer);

		get("/admin/jsonQueries", URLTools.HEADER_JSON, (request, response) -> TechnicalServiceManager.inst().getJsonInfo(), transformer);

		get("/admin/threads", URLTools.HEADER_JSON, (request, response) -> ThreadManager.getInstance().toJson(), transformer);

		get("/admin/network", URLTools.HEADER_JSON, (request, response) -> TechnicalServiceManager.inst().getNetworkInfos().stream().map(NetworkInfo::toJson).toList(), transformer);

		get("/admin/clearCache", URLTools.HEADER_JSON, (request, response) -> {
			clearCache();
			return ok(request,response,"cache clear");
		}, transformer);


		get("/admin/plugins/list", URLTools.HEADER_JSON, (request, response) -> {
			var obj = new JsonObject();
			PluginRegistry.inst().entrySet().forEach(entry->obj.add(entry.getValue().getType().name(), converter.convert(listPlugins(entry.getKey()))));
			return obj;
		}, transformer);

		put("/admin/plugins/:type/:name/:enable", URLTools.HEADER_JSON, (request, response) -> {
			var selectedProvider = PluginRegistry.inst().getPluginById((request.params(TYPE)+request.params(NAME)));

			if(selectedProvider==null)
				throw new NullPointerException("Provider is not found");

			selectedProvider.enable(Boolean.parseBoolean(request.params(":enable")));
			MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());

			return selectedProvider.toJson();
		}, transformer);


		get("/admin/reindexation", URLTools.HEADER_JSON, (request, response) -> {
			Chrono c = new Chrono();
						 c.start();
				MTG.getEnabledPlugin(MTGCardsIndexer.class).initIndex(true);
			return ok(request,response,"done in " + c.stop() +" s");
		}, transformer);

		get("/admin/recognize/caching/:setId", URLTools.HEADER_JSON, (request, response) -> {
			MTG.getEnabledPlugin(MTGCardRecognition.class).downloadCardsData(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(request.params(":setId")));
			return ok(request,response,request.params(":setId") + "card's data recognition downloaded");
		}, transformer);

		get("/admin/recognize/list", URLTools.HEADER_JSON, (request, response) -> {

			MTG.getEnabledPlugin(MTGCardRecognition.class).loadAllCachedData();
			return MTG.getEnabledPlugin(MTGCardRecognition.class).getDataList().keySet();

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
			MTG.getPlugin(MTGConstants.MTG_APP_NAME,MTGExternalShop.class).getTransactionById(Long.valueOf(request.params(":id")))
		, transformer);


		get("/extShop/products/:provider/:search", URLTools.HEADER_JSON, (request, response) ->
			getPlugin(request.params(PROVIDER),MTGExternalShop.class).listProducts(request.params(":search"))
		, transformer);


		get("/extShop/list/stock/:provider", URLTools.HEADER_JSON, (request, response) ->
			 getCached(request.pathInfo(), new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return getPlugin(request.params(PROVIDER),MTGExternalShop.class).listStock("");
				}
			})
		 , transformer);

		get("/extShop/stock/:provider/:id", URLTools.HEADER_JSON, (request, response) ->
				getPlugin(request.params(PROVIDER),MTGExternalShop.class).getStockById(null,Long.parseLong(request.params(":id")))
		, transformer);

		get("/extShop/transactions/from/:provider", URLTools.HEADER_JSON, (request, response) ->
			getPlugin(request.params(PROVIDER),MTGExternalShop.class).listTransaction()
		, transformer);

		get("/extShop/transactions/:provider/:id", URLTools.HEADER_JSON, (request, response) ->
			getPlugin(request.params(PROVIDER),MTGExternalShop.class).getTransactionById(Long.valueOf(request.params(":id")))
		, transformer);

		post("/extShop/transactions/:to/save", URLTools.HEADER_JSON, (request, response) ->{

			MTGExternalShop extShop  = MTG.getPlugin(request.params(":to"), MTGExternalShop.class);

			List<Transaction> ret = converter.fromJsonList(new InputStreamReader(request.raw().getInputStream()), Transaction.class);
			var arr = new HashMap<String, List<Transaction>>();

			arr.put("ok", new ArrayList<>());
			arr.put(ERROR, new ArrayList<>());

			for(Transaction p : ret)
			{
				try {
					extShop.saveOrUpdateTransaction(p);
					arr.get("ok").add(p);
				}catch(Exception e)
				{
					logger.error(e);
					arr.get(ERROR).add(p);
				}
			}
			return arr;

		}, transformer);

		put("/favorites/:classename/:idContact/"+ID_ANNOUNCE, URLTools.HEADER_JSON, (request, response) -> {
			try{
				MTG.getEnabledPlugin(MTGDao.class).saveFavorites(Integer.parseInt(request.params(ID_CONTACT)), Integer.parseInt(request.params(ID_ANNOUNCE)),request.params(CLASSENAME));
				return ok(request, response,"favorites saved for announces " + request.params(ID_ANNOUNCE));
			}catch(Exception e)
			{
				return e;
			}
		}, transformer);


		delete("/favorites/:classename/:idContact/"+ID_ANNOUNCE, URLTools.HEADER_JSON, (request, response) -> {
			try{
				MTG.getEnabledPlugin(MTGDao.class).deleteFavorites(Integer.parseInt(request.params(ID_CONTACT)), Integer.parseInt(request.params(ID_ANNOUNCE)),request.params(CLASSENAME));
				return ok(request, response,"favorites deleted for announces " + request.params(ID_ANNOUNCE));
				}
			catch(Exception e)
				{
					return e;
				}
		}, transformer);


		get("/favorites/:classename/:idContact", URLTools.HEADER_JSON, (request, response) -> {
			try{
				var cont = MTG.getEnabledPlugin(MTGDao.class).getContactById(Integer.parseInt(request.params(ID_CONTACT)));
				return MTG.getEnabledPlugin(MTGDao.class).listFavorites(cont,request.params(CLASSENAME));
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

		get("/announces/stats", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(-1,STATUS.ACTIVE).stream().collect(Collectors.groupingBy(Announce::getCategorie, Collectors.counting())), transformer);

		get("/announces/list", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(), transformer);

		get("/announces/last/:qty", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(Integer.parseInt(request.params(":qty")),STATUS.ACTIVE), transformer);

		get("/announces/keyword/:search", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(URLTools.decode(request.params(":search"))), transformer);

		get("/announces/category/:type", URLTools.HEADER_JSON, (request, response) -> MTG.getEnabledPlugin(MTGDao.class).listAnnounces(EnumItems.valueOf(request.params(TYPE))), transformer);

		get("/announces/contact/:id", URLTools.HEADER_JSON, (request, response) -> {
			var c = new Contact();
			c.setId(Integer.parseInt(request.params(":id")));
			return MTG.getEnabledPlugin(MTGDao.class).listAnnounces(c).stream().filter(a->a.getStatus()==STATUS.ACTIVE).toList();
		}, transformer);

		get("/webshop/:dest/categories", URLTools.HEADER_JSON, (request, response) ->MTG.getPlugin(request.params(":dest"), MTGExternalShop.class).listCategories(), transformer);

		post("/webshop/user/connect", URLTools.HEADER_JSON, (request, response) ->MTG.getEnabledPlugin(MTGExternalShop.class).getContactByLogin(request.queryParams("email"),request.queryParams("password")), transformer);

		post("/webshop/transaction/cancel/:id", URLTools.HEADER_JSON, (request, response) -> {

			Contact c=converter.fromJson(request.queryParams("user"), Contact.class);

			var t =MTG.getEnabledPlugin(MTGExternalShop.class).getTransactionById(Long.valueOf(request.params(":id")));

			if(t.getContact().getId()==c.getId())
			{
				t.setStatut(TransactionStatus.CANCELATION_ASK);
				MTG.getEnabledPlugin(MTGExternalShop.class).saveOrUpdateTransaction(t);
				return ok(request, response,t);
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


		get("/share/announce/:id",URLTools.HEADER_HTML,(request,response) -> {
			response.type(URLTools.HEADER_HTML);
			var report = new ReportNotificationManager();
			var announce = MTG.getEnabledPlugin(MTGDao.class).getAnnounceById(Integer.parseInt(request.params(":id")));
			return report.generate(FORMAT_NOTIFICATION.HTML, announce, "share");
		});



		get("/robots.txt",URLTools.HEADER_TEXT,(req,res) ->robotVars);

		if(getBoolean("INDEX_ROUTES")) {
			get("/",URLTools.HEADER_HTML,(request,response) -> {

				var temp = new StringBuilder();
				response.type(URLTools.HEADER_HTML);

				Spark.routes().stream().filter(rm->rm.getHttpMethod()!=HttpMethod.after && rm.getHttpMethod()!=HttpMethod.before && rm.getHttpMethod()!=HttpMethod.options).sorted(Comparator.comparing(RouteMatch::getMatchUri)).forEach(rm->{
					temp.append(rm.getHttpMethod());
					temp.append("&nbsp;");
					temp.append("<a href='").append(rm.getMatchUri()).append("'>").append(rm.getMatchUri()).append("</a>");
					temp.append("<br/>");
				});

				return temp.toString();
			});
		}
	}


	private JsonObject readJsonObject(Request request) throws IOException {
		return converter.fromJson(new InputStreamReader(request.raw().getInputStream()), JsonObject.class);
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

		TechnicalServiceManager.inst().storeAll();
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
		var map = super.getDefaultAttributes();

		map.put(SERVER_PORT, "8080");
		map.put(AUTOSTART, FALSE);
		map.put(ENABLE_GZIP, TRUE);
		map.put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		map.put(ACCESS_CONTROL_REQUEST_METHOD, "GET,PUT,POST,DELETE,OPTIONS");
		map.put(ACCESS_CONTROL_ALLOW_HEADERS,"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		map.put("THREADS",String.valueOf(Runtime.getRuntime().availableProcessors()));
		map.put(ENABLE_SSL,FALSE);
		map.put(KEYSTORE_URI, new File(MTGConstants.DATA_DIR,"jetty.jks").getAbsolutePath());
		map.put(KEYSTORE_PASS, "changeit");
		map.put("INDEX_ROUTES", TRUE);
		map.put("PRETTY_PRINT", FALSE);
		map.put("JWT_SECRET",JWTServices.generateRandomSecret());
		map.put("JWT_EXPIRATION_MINUTES", "60");
		map.put("JWT_REFRESH_EXPIRATION_MINUTES","21600");
		return map;
	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(Spark.class, "/META-INF/maven/com.sparkjava/spark-core/pom.properties");
	}

}
