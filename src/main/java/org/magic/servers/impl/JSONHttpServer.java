package org.magic.servers.impl;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.notFound;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.put;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.sorters.CardsEditionSorter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class JSONHttpServer extends AbstractMTGServer {

	private ResponseTransformer transformer;
	private MTGDeckManager manager;
	private ByteArrayOutputStream baos;
	private boolean running = false;
	private static final String RETURN_OK = "{\"result\":\"OK\"}";
	private Gson converter;

	private String error(String msg) {
		return "{\"error\":\"" + msg + "\"}";
	}

	public JSONHttpServer() {
		super();

		manager = new MTGDeckManager();
		converter = new Gson();
		transformer = new ResponseTransformer() {
			@Override
			public String render(Object model) throws Exception {
				return converter.toJson(model);
			}
		};

	}

	@Override
	public void start() throws IOException {
		initVars();
		initRoutes();
		Spark.init();
		running = true;
	}

	private void initVars() {

		port(getInt("SERVER-PORT"));

		initExceptionHandler(e -> {
			running = false;
			logger.error(e);
		});

		exception(Exception.class, (Exception exception, Request req, Response res) -> {
			logger.error("Error :" + req.headers("Referer") + ":" + exception.getMessage(), exception);
			res.status(500);
			res.body(error(exception.getMessage()));
		});

		notFound((req, res) -> {
			res.status(404);
			return error("Not Found");
		});

		after((request, response) -> {
			if (getBoolean("ENABLE_GZIP")) {
				response.header("Content-Encoding", "gzip");
			}
		});

		options("/*", (request, response) -> {
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}
			return RETURN_OK;
		});

	}

	private void initRoutes() {

		before("/*", (request, response) -> {
			response.type(getString("MIME"));
			response.header("Access-Control-Allow-Origin", getWhiteHeader(request));
			response.header("Access-Control-Request-Method", getString("Access-Control-Request-Method"));
			response.header("Access-Control-Allow-Headers", getString("Access-Control-Allow-Headers"));
		});

		get("/cards/search/:att/:val", getString("MIME"),
				(request, response) -> MTGControler.getInstance().getEnabledCardsProviders()
						.searchCardByCriteria(request.params(":att"), request.params(":val"), null, false),
				transformer);

		get("/cards/name/:idEd/:cName", getString("MIME"), (request, response) -> {

			MagicEdition ed = MTGControler.getInstance().getEnabledCardsProviders().getSetById(request.params(":idEd"));
			return MTGControler.getInstance().getEnabledCardsProviders().searchCardByCriteria("name",
					request.params(":cName"), ed, true);
		}, transformer);

		put("/cards/move/:from/:to/:id", getString("MIME"), (request, response) -> {
			MagicCollection from = new MagicCollection(request.params(":from"));
			MagicCollection to = new MagicCollection(request.params(":to"));
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().getCardById(request.params(":id"));
			MTGControler.getInstance().getEnabledDAO().moveCard(mc, from,to);
			return RETURN_OK;
		}, transformer);

		put("/cards/add/:id", getString("MIME"), (request, response) -> {
			MagicCollection from = new MagicCollection(MTGControler.getInstance().get("default-library"));
			MagicCollection to = new MagicCollection(request.params(":to"));
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().getCardById(request.params(":id"));
			MTGControler.getInstance().getEnabledDAO().removeCard(mc, from);
			MTGControler.getInstance().getEnabledDAO().saveCard(mc, to);
			return RETURN_OK;
		}, transformer);

		put("/cards/add/:to/:id", getString("MIME"), (request, response) -> {
			MagicCollection to = new MagicCollection(request.params(":to"));
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().getCardById(request.params(":id"));
			MTGControler.getInstance().getEnabledDAO().saveCard(mc, to);
			return RETURN_OK;
		}, transformer);

		get("/cards/list/:col/:idEd", getString("MIME"), (request, response) -> {
			MagicCollection col = new MagicCollection(request.params(":col"));
			MagicEdition ed = new MagicEdition();
			ed.setId(request.params(":idEd"));
			ed.setSet(request.params(":idEd"));
			return MTGControler.getInstance().getEnabledDAO().listCardsFromCollection(col, ed);
		}, transformer);

		get("/cards/:id", getString("MIME"), (request, response) -> MTGControler.getInstance().getEnabledCardsProviders()
				.getCardById(request.params(":id")), transformer);

		get("/cards/:idSet/cards", getString("MIME"), (request, response) -> {
			List<MagicCard> ret = MTGControler.getInstance().getEnabledCardsProviders().searchCardByCriteria("set",
					request.params(":idSet"), null, false);
			Collections.sort(ret, new CardsEditionSorter());

			return ret;
		}, transformer);

		get("/collections/:name/count", getString("MIME"), (request, response) -> MTGControler.getInstance()
				.getEnabledDAO().getCardsCountGlobal(new MagicCollection(request.params(":name"))), transformer);

		get("/collections/list", getString("MIME"),
				(request, response) -> MTGControler.getInstance().getEnabledDAO().getCollections(), transformer);

		get("/collections/cards/:idcards", getString("MIME"), (request, response) -> {
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().getCardById(request.params(":idcards"));
			return MTGControler.getInstance().getEnabledDAO().listCollectionFromCards(mc);
		}, transformer);

		get("/collections/:name", getString("MIME"), (request, response) -> MTGControler.getInstance().getEnabledDAO()
				.getCollection(request.params(":name")), transformer);

		put("/collections/add/:name", getString("MIME"), (request, response) -> {
			MTGControler.getInstance().getEnabledDAO().saveCollection(new MagicCollection(request.params(":name")));
			return RETURN_OK;
		});

		get("/editions/list", getString("MIME"),
				(request, response) -> MTGControler.getInstance().getEnabledCardsProviders().loadEditions(), transformer);

		get("/editions/:idSet", getString("MIME"), (request, response) -> MTGControler.getInstance()
				.getEnabledCardsProviders().getSetById(request.params(":idSet")), transformer);

		get("/editions/list/:colName", getString("MIME"), (request, response) -> {
			List<MagicEdition> eds = new ArrayList<>();
			List<String> list = MTGControler.getInstance().getEnabledDAO()
					.getEditionsIDFromCollection(new MagicCollection(request.params(":colName")));
			for (String s : list)
				eds.add(MTGControler.getInstance().getEnabledCardsProviders().getSetById(s));

			Collections.sort(eds);
			return eds;

		}, transformer);

		get("/prices/:idSet/:name", getString("MIME"), (request, response) -> {
			MagicEdition ed = MTGControler.getInstance().getEnabledCardsProviders().getSetById(request.params(":idSet"));
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
					.searchCardByCriteria("name", request.params(":name"), ed, false).get(0);
			List<MagicPrice> pricesret = new ArrayList<>();
			for (MTGPricesProvider prices : MTGControler.getInstance().getEnabledPricers())
				pricesret.addAll(prices.getPrice(ed, mc));

			return pricesret;

		}, transformer);

		get("/alerts/list", getString("MIME"),
				(request, response) -> MTGControler.getInstance().getEnabledDAO().listAlerts(), transformer);

		get("/alerts/:idCards", getString("MIME"), (request, response) -> {
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().getCardById(request.params(":idCards"));
			return MTGControler.getInstance().getEnabledDAO().hasAlert(mc);

		}, transformer);

		put("/alerts/add/:idCards", (request, response) -> {
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().getCardById(request.params(":idCards"));
			MagicCardAlert alert = new MagicCardAlert();
			alert.setCard(mc);
			alert.setPrice(0.0);
			MTGControler.getInstance().getEnabledDAO().saveAlert(alert);
			return RETURN_OK;
		});

		put("/stock/add/:idCards", (request, response) -> {
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().getCardById(request.params(":idCards"));
			MagicCardStock stock = new MagicCardStock();
			stock.setQte(1);
			stock.setMagicCard(mc);

			MTGControler.getInstance().getEnabledDAO().saveOrUpdateStock(stock);
			return RETURN_OK;
		});

		get("/stock/list", getString("MIME"),
				(request, response) -> MTGControler.getInstance().getEnabledDAO().listStocks(), transformer);

		get("/dash/collection", getString("MIME"), (request, response) -> {
			List<MagicEdition> eds = MTGControler.getInstance().getEnabledCardsProviders().loadEditions();
			MagicEditionsTableModel model = new MagicEditionsTableModel();
			model.init(eds);

			JsonArray arr = new JsonArray();
			double pc = 0;
			for (MagicEdition ed : eds) {
				JsonObject obj = new JsonObject();
				obj.add("edition", converter.toJsonTree(ed));
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

		get("/dash/card/:idCard", getString("MIME"), (request, response) -> {
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().getCardById(request.params(":idCard"));

			JsonArray arr = new JsonArray();
			Map<Date, Double> res = MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(mc,
					mc.getCurrentSet());

			for (Entry<Date, Double> val : res.entrySet()) {
				JsonObject obj = new JsonObject();
				obj.add("date", new JsonPrimitive(val.getKey().getTime()));
				obj.add("value", new JsonPrimitive(val.getValue()));

				arr.add(obj);
			}

			return arr;
		});

		get("/dash/edition/:idEd", getString("MIME"), (request, response) -> {
			MagicEdition ed = new MagicEdition();
			ed.setId(request.params(":idEd"));
			return MTGControler.getInstance().getEnabledDashBoard().getShakeForEdition(ed);
		}, transformer);

		get("/dash/format/:format", getString("MIME"), (request, response) -> MTGControler.getInstance()
				.getEnabledDashBoard().getShakerFor(MTGFormat.valueOf(request.params(":format"))), transformer);

		get("/pics/cards/:idEd/:name", getString("MIME"), (request, response) -> {

			baos = new ByteArrayOutputStream();

			MagicEdition ed = MTGControler.getInstance().getEnabledCardsProviders().getSetById(request.params(":idEd"));
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
					.searchCardByCriteria("name", request.params(":name"), ed, true).get(0);
			BufferedImage im = MTGControler.getInstance().getEnabledPicturesProvider().getPicture(mc, null);
			ImageIO.write(im, "png", baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			response.type("image/png");

			return imageInByte;
		});

		get("/pics/cardname/:name", getString("MIME"), (request, response) -> {

			baos = new ByteArrayOutputStream();
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
					.searchCardByCriteria("name", request.params(":name"), null, true).get(0);
			BufferedImage im = MTGControler.getInstance().getEnabledPicturesProvider().getPicture(mc, null);
			ImageIO.write(im, "png", baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			response.type("image/png");

			return imageInByte;
		});

		get("/decks/list", getString("MIME"), (request, response) -> {

			JsonArray arr = new JsonArray();
			JsonExport exp = new JsonExport();

			for (MagicDeck d : manager.listDecks()) {
				arr.add(exp.toJson(d));
			}
			return arr;
		}, transformer);

		get("/deck/:name", getString("MIME"),
				(request, response) -> new JsonExport().toJson(manager.getDeck(request.params(":name"))), transformer);

		get("/deck/stats/:name", getString("MIME"), (request, response) -> {

			MagicDeck d = manager.getDeck(request.params(":name"));

			JsonObject obj = new JsonObject();

			obj.add("cmc", converter.toJsonTree(manager.analyseCMC(d.getAsList())));
			obj.add("types", converter.toJsonTree(manager.analyseTypes(d.getAsList())));
			obj.add("rarity", converter.toJsonTree(manager.analyseRarities(d.getAsList())));
			obj.add("colors", converter.toJsonTree(manager.analyseColors(d.getAsList())));
			obj.add("legalities", converter.toJsonTree(manager.analyseLegalities(d)));
			obj.add("drawing", converter.toJsonTree(manager.analyseDrawing(d)));
			return obj;

		}, transformer);

		get("/admin/plugins/list", getString("MIME"), (request, response) -> {

			JsonObject obj = new JsonObject();
			
			obj.add(PLUGINS.PROVIDER.name(), convert(MTGControler.getInstance().getCardsProviders()));
			obj.add(PLUGINS.PICTURES.name(), convert(MTGControler.getInstance().getPicturesProviders()));
			obj.add(PLUGINS.PRICER.name(), convert(MTGControler.getInstance().getPricerProviders()));
			obj.add(PLUGINS.CACHE.name(), convert(MTGControler.getInstance().getCachesProviders()));
			obj.add(PLUGINS.DAO.name(), convert(MTGControler.getInstance().getDaoProviders()));
			obj.add(PLUGINS.DASHBOARD.name(), convert(MTGControler.getInstance().getDashboardsProviders()));
			obj.add(PLUGINS.DECKS.name(), convert(MTGControler.getInstance().getDeckSnifferProviders()));
			obj.add(PLUGINS.EXPORT.name(), convert(MTGControler.getInstance().getImportExportProviders()));
			obj.add(PLUGINS.NEWS.name(), convert(MTGControler.getInstance().getNewsProviders()));
			obj.add(PLUGINS.WALLPAPER.name(), convert(MTGControler.getInstance().getWallpaperProviders()));
			obj.add(PLUGINS.SHOPPER.name(), convert(MTGControler.getInstance().getShoppersProviders()));
			obj.add(PLUGINS.SERVER.name(), convert(MTGControler.getInstance().getServers()));
			obj.add(PLUGINS.NOTIFIER.name(), convert(MTGControler.getInstance().getNotifierProviders()));
			return obj;
		}, transformer);

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
		setProperty("MIME", "application/json");
		setProperty("ENABLE_GZIP", "false");
		setProperty("Access-Control-Allow-Origin", "*");
		setProperty("Access-Control-Request-Method", "GET,PUT,POST,DELETE,OPTIONS");
		setProperty("Access-Control-Allow-Headers",
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		setProperty("PASSWORD-TOKEN", "");
	}

	@Override
	public String getVersion() {
		return "2.0";
	}

	// TODO filter allowed header
	private String getWhiteHeader(Request request) {
		logger.debug("request :" + request.pathInfo() + " from " + request.ip());

		for (String k : request.headers())
			logger.trace("---" + k + "=" + request.headers(k));

		return getString("Access-Control-Allow-Origin");
	}

	private <T extends MTGPlugin> JsonArray convert(List<T> l) {
		JsonArray arr = new JsonArray();
		for (MTGPlugin plug : l) {
			JsonObject obj = new JsonObject();
			obj.addProperty("name", plug.getName());
			obj.addProperty("type", plug.getType().toString());
			obj.addProperty("enabled", plug.isEnable());
			obj.addProperty("version", plug.getVersion());
			obj.addProperty("status", plug.getStatut().name());
			obj.add("config", converter.toJsonTree(plug.getProperties()));
			arr.add(obj);
		}
		return arr;
	}

}
