package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.tools.FileTools;
import org.magic.tools.URLTools;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonExport extends AbstractCardExport {

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
		super();
		gson=new GsonBuilder().setPrettyPrinting().create();
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
	

	public <U,V> Map<U,V> fromJsonCollection(String json, Class<U> class1,Class<V> class2) {
		return gson.fromJson(json, new TypeToken<Map<U, V>>() 
		{
			private static final long serialVersionUID = 1L;
		}.getType());
		
	}
	
			
	public <T> T fromJson(String s,Class<T> classe)
	{
		return gson.fromJson(s, classe);
	}
	
	
	public <T> List<T> fromJsonList(String s,Class<T> classe)
	{
		ArrayList<T> list = new ArrayList<>();
		JsonArray json= gson.fromJson(s,JsonArray.class);
		json.forEach(el->list.add(gson.fromJson(el.toString(),classe)));
		return list;
	}
	

	@Override
	public MagicDeck importDeck(String f,String name) throws IOException {
		JsonObject root = URLTools.toJson(f).getAsJsonObject();

		MagicDeck deck = new MagicDeck();
			deck.setName(name);
			
		if (!root.get(NAME).isJsonNull())
			deck.setName(root.get(NAME).getAsString());

		if (!root.get(DESCRIPTION).isJsonNull())
			deck.setDescription(root.get(DESCRIPTION).getAsString());

		if (!root.get(CREATION_DATE).isJsonNull())
			deck.setCreationDate(new Date(root.get(CREATION_DATE).getAsLong()));

		if (!root.get(UPDATE_DATE).isJsonNull())
			deck.setDateUpdate(new Date(root.get(UPDATE_DATE).getAsLong()));

		if (root.get(COMMANDER)!=null)
			deck.setCommander(gson.fromJson(root.get(COMMANDER), MagicCard.class));

		if (root.get(AVERAGE_PRICE)!=null)
			deck.setAveragePrice(root.get(AVERAGE_PRICE).getAsDouble());

		
		if (!root.get(TAGS).isJsonNull()) {
			JsonArray arr = root.get(TAGS).getAsJsonArray();
			for (var i = 0; i < arr.size(); i++)
				deck.getTags().add(arr.get(i).getAsString());
		}

		JsonArray main = root.get("main").getAsJsonArray();

		for (var i = 0; i < main.size(); i++) {
			JsonObject line = main.get(i).getAsJsonObject();
			int qte = line.get("qty").getAsInt();
			MagicCard mc = gson.fromJson(line.get("card"), MagicCard.class);
			notify(mc);
			deck.getMain().put(mc, qte);
		}

		JsonArray side = root.get("side").getAsJsonArray();

		for (var i = 0; i < side.size(); i++) {
			JsonObject line = side.get(i).getAsJsonObject();
			int qte = line.get("qty").getAsInt();
			MagicCard mc = gson.fromJson(line.get("card"), MagicCard.class);
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
		JsonObject json = new JsonObject();
		json.addProperty(NAME, deck.getName());
		json.addProperty(DESCRIPTION, deck.getDescription());
		json.addProperty(COLORS, deck.getColors());
		json.addProperty(AVERAGE_PRICE, deck.getAveragePrice());
		json.add(COMMANDER,toJsonElement(deck.getCommander()));
		json.add(CREATION_DATE, new JsonPrimitive(deck.getDateCreation().getTime()));
		json.add(UPDATE_DATE, new JsonPrimitive(deck.getDateUpdate().getTime()));
		JsonArray tags = new JsonArray();
		for (String s : deck.getTags())
			tags.add(s);

		json.add(TAGS, tags);
		
		JsonArray main = new JsonArray();

		for (MagicCard mc : deck.getMain().keySet()) {
			JsonObject card = new JsonObject();
			card.addProperty("qty", (Number) deck.getMain().get(mc));
			card.add("card", toJsonElement(mc));
			main.add(card);
			notify(mc);
		}

		JsonArray side = new JsonArray();

		for (MagicCard mc : deck.getSideBoard().keySet()) {
			JsonObject card = new JsonObject();
			card.addProperty("qty", (Number) deck.getSideBoard().get(mc));
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

		JsonArray jsonparams = new JsonArray();

		for (MagicCardStock mc : stock) {
			jsonparams.add(new Gson().toJsonTree(mc));
			notify(mc.getMagicCard());
		}
		try (FileWriter out = new FileWriter(f)) {
			out.write(jsonparams.toString());
		}
	}

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		JsonArray root = URLTools.toJson(content).getAsJsonArray();
		List<MagicCardStock> list = new ArrayList<>();
		for (var i = 0; i < root.size(); i++) {
			JsonObject line = root.get(i).getAsJsonObject();
			MagicCardStock mc = new Gson().fromJson(line, MagicCardStock.class);
			notify(mc.getMagicCard());
			list.add(mc);
		}

		return list;
	}


	@Override
	public String getVersion() {
		return "1.1";
	}

	
	public <T extends MTGPlugin> JsonArray convert(List<T> l) {
		var arr = new JsonArray();
		for (MTGPlugin plug : l) {
			var obj = new JsonObject();
			obj.addProperty("name", plug.getName());
			obj.addProperty("type", plug.getType().toString());
			obj.addProperty("enabled", plug.isEnable());
			obj.addProperty("version", plug.getVersion());
			obj.addProperty("status", plug.getStatut().name());
			//obj.add("config", toJsonElement(plug.getProperties()));
			arr.add(obj);
		}
		return arr;
	}

	
}
