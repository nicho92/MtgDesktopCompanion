package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.adapters.FileAdapter;
import org.magic.services.adapters.InstantAdapter;
import org.magic.services.adapters.MTGStockItemAdapter;
import org.magic.services.adapters.NetworkInfoAdapter;
import org.magic.services.adapters.StackTraceElementAdapter;
import org.magic.services.adapters.UserAgentAdapter;
import org.magic.services.network.URLTools;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.POMReader;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nl.basjes.parse.useragent.UserAgent;

public class JsonExport extends AbstractCardExport {

	private static final String ID = "id";
	private static final String AVERAGE_PRICE = "averagePrice";
	private static final String COMMANDER = "commander";
	private static final String UPDATE_DATE = "updateDate";
	private static final String CREATION_DATE = "creationDate";
	private static final String COLORS = "colors";
	private static final String NAME = "name";
	private static final String TAGS = "tags";
	private static final String DESCRIPTION = "description";
	private Gson gson;

	public JsonExport() {
		gson=new GsonBuilder()
				.registerTypeAdapter(MTGStockItem.class, new MTGStockItemAdapter())
				.registerTypeAdapter(Instant.class, new InstantAdapter())
				.registerTypeAdapter(UserAgent.class, new UserAgentAdapter())
				.registerTypeAdapter(StackTraceElement.class, new StackTraceElementAdapter())
				.registerTypeHierarchyAdapter(NetworkInfo.class, new NetworkInfoAdapter())
				.registerTypeHierarchyAdapter(File.class, new FileAdapter())
				.setDateFormat("yyyy-MM-dd hh:mm").setPrettyPrinting()
				.create();
	}

	public void removePrettyString() {
		gson=new GsonBuilder()
				.registerTypeAdapter(MTGStockItem.class, new MTGStockItemAdapter())
				.registerTypeAdapter(Instant.class, new InstantAdapter())
				.registerTypeAdapter(UserAgent.class, new UserAgentAdapter())
				.registerTypeAdapter(StackTraceElement.class, new StackTraceElementAdapter())
				.registerTypeHierarchyAdapter(NetworkInfo.class, new NetworkInfoAdapter())
				.setDateFormat("yyyy-MM-dd HH:mm")
				.create();
	}



	public String toJson(Object o)
	{

		return gson.toJson(o);
	}

	public JsonElement toJsonElement(Object o)
	{
		return gson.toJsonTree(o);
	}

	public JsonArray toJsonArray(Object o,String arrAtts)
	{
		return toJsonElement(o).getAsJsonObject().get(arrAtts).getAsJsonArray();
	}

	public JsonArray toJsonArray(Object o)
	{
		return toJsonElement(o).getAsJsonArray();
	}


	public <U,V> Map<U,V> fromJsonCollection(String json) {
		return gson.fromJson(json, new TypeToken<Map<U, V>>()
		{
			private static final long serialVersionUID = 1L;
		}.getType());

	}


	public <T> T fromJson(String s,Class<T> classe)
	{
		return gson.fromJson(s, classe);
	}


	public <T> T fromJson(Reader reader, Class<T> classItem) {
		return gson.fromJson(reader, classItem);
	}


	public <T> List<T> fromJsonList(Reader s,Class<T> classe)
	{
		ArrayList<T> list = new ArrayList<>();
		var json= gson.fromJson(s,JsonArray.class);
		json.forEach(el->list.add(gson.fromJson(el.toString(),classe)));
		return list;
	}


	public <T> List<T> fromJsonList(String s,Class<T> classe)
	{
		ArrayList<T> list = new ArrayList<>();
		try{
			var json= gson.fromJson(s,JsonArray.class);
			json.forEach(el->list.add(gson.fromJson(el.toString(),classe)));
		}
		catch(Exception e)
		{
			logger.error(e);
		}

		return list;
	}

	@Override
	public MagicDeck importDeck(String f,String name)  {
		var root = URLTools.toJson(f).getAsJsonObject();

		var deck = new MagicDeck();
			deck.setName(name);

		if (root.get(ID)!=null)
			deck.setId(root.get(ID).getAsInt());

		if (!root.get(NAME).isJsonNull())
			deck.setName(root.get(NAME).getAsString());

		if (!root.get(DESCRIPTION).isJsonNull())
			deck.setDescription(root.get(DESCRIPTION).getAsString());

		if (!root.get(CREATION_DATE).isJsonNull())
		{
			try {
				deck.setCreationDate(new Date(root.get(CREATION_DATE).getAsLong()));
			}catch(Exception e)
			{
				logger.error(e);
			}
		}

		if (!root.get(UPDATE_DATE).isJsonNull())
		{
			try {
				deck.setDateUpdate(new Date(root.get(UPDATE_DATE).getAsLong()));
			}catch(Exception e)
			{
				logger.error(e);
			}

		}

		if (root.get(COMMANDER)!=null)
			deck.setCommander(gson.fromJson(root.get(COMMANDER), MagicCard.class));

		if (root.get(AVERAGE_PRICE)!=null)
			deck.setAveragePrice(root.get(AVERAGE_PRICE).getAsDouble());


		if (!root.get(TAGS).isJsonNull()) {
			var arr = root.get(TAGS).getAsJsonArray();
			for (var i = 0; i < arr.size(); i++)
				deck.getTags().add(arr.get(i).getAsString());
		}

		var main = root.get("main").getAsJsonArray();

		for (var i = 0; i < main.size(); i++) {
			var line = main.get(i).getAsJsonObject();
			var qte = line.get("qty").getAsInt();
			MagicCard mc = gson.fromJson(line.get("card"), MagicCard.class);
			notify(mc);
			deck.getMain().put(mc, qte);
		}

		var side = root.get("side").getAsJsonArray();

		for (var i = 0; i < side.size(); i++) {
			var line = side.get(i).getAsJsonObject();
			var qte = line.get("qty").getAsInt();
			var mc = gson.fromJson(line.get("card"), MagicCard.class);
			notify(mc);
			deck.getSideBoard().put(mc, qte);

		}

		return deck;
	}

	@Override
	public String getFileExtension() {
		return ".json";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		FileTools.saveFile(dest, toJsonDeck(deck).toString());
	}


	public JsonObject toJsonDeck(MagicDeck deck) {
		var json = new JsonObject();
		json.addProperty(ID, deck.getId());
		json.addProperty(NAME, deck.getName());
		json.addProperty(DESCRIPTION, deck.getDescription());
		json.addProperty(COLORS, deck.getColors());
		json.addProperty(AVERAGE_PRICE, deck.getAveragePrice());
		json.add(COMMANDER,toJsonElement(deck.getCommander()));
		json.addProperty(CREATION_DATE, deck.getDateCreation().getTime());
		json.addProperty(UPDATE_DATE, deck.getDateUpdate().getTime());
		var tags = new JsonArray();
		for (String s : deck.getTags())
			tags.add(s);

		json.add(TAGS, tags);

		var main = new JsonArray();

		for (MagicCard mc : deck.getMain().keySet()) {
			var card = new JsonObject();
			card.addProperty("qty", deck.getMain().get(mc));
			card.add("card", toJsonElement(mc));
			main.add(card);
			notify(mc);
		}

		var side = new JsonArray();

		for (MagicCard mc : deck.getSideBoard().keySet()) {
			var card = new JsonObject();
			card.addProperty("qty", deck.getSideBoard().get(mc));
			card.add("card", toJsonElement(mc));
			side.add(card);
			notify(mc);
		}
		json.add("main", main);
		json.add("side", side);
		return json;
	}

	@Override
	public String getName() {
		return "Json";
	}


	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

		var jsonparams = new JsonArray();

		for (MagicCardStock mc : stock) {
			jsonparams.add(gson.toJsonTree(mc));
			notify(mc.getProduct());
		}
		try (var out = new FileWriter(f)) {
			out.write(jsonparams.toString());
		}
	}

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		var root = URLTools.toJson(content).getAsJsonArray();
		List<MagicCardStock> list = new ArrayList<>();
		for (var i = 0; i < root.size(); i++) {
			var line = root.get(i).getAsJsonObject();
			var mc = gson.fromJson(line, MagicCardStock.class);
			mc.setId(-1);
			notify(mc.getProduct());
			list.add(mc);
		}

		return list;
	}


	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(Gson.class, "/META-INF/maven/com.google.code.gson/gson/pom.properties");
	}



	public <T extends MTGPlugin> JsonArray convert(List<T> l) {
		var arr = new JsonArray();
		for (MTGPlugin plug : l) {
			arr.add(plug.toJson());
		}
		return arr;
	}


}
