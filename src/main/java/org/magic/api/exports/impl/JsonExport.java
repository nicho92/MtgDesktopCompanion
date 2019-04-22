package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

public class JsonExport extends AbstractCardExport {

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
		gson=new GsonBuilder().create();
	}
	
	
	public String toJson(Object o)
	{
		return gson.toJson(o);
	}
	
	public JsonElement toJsonElement(Object o)
	{
		return gson.toJsonTree(o);
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
		JsonReader reader = new JsonReader(new StringReader(f));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();

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

		
		if (!root.get(TAGS).isJsonNull()) {
			JsonArray arr = root.get(TAGS).getAsJsonArray();
			for (int i = 0; i < arr.size(); i++)
				deck.getTags().add(arr.get(i).getAsString());
		}

		JsonArray main = root.get("main").getAsJsonArray();

		for (int i = 0; i < main.size(); i++) {
			JsonObject line = main.get(i).getAsJsonObject();
			int qte = line.get("qty").getAsInt();
			MagicCard mc = gson.fromJson(line.get("card"), MagicCard.class);
			notify(mc);
			deck.getMap().put(mc, qte);
		}

		JsonArray side = root.get("side").getAsJsonArray();

		for (int i = 0; i < side.size(); i++) {
			JsonObject line = side.get(i).getAsJsonObject();
			int qte = line.get("qty").getAsInt();
			MagicCard mc = gson.fromJson(line.get("card"), MagicCard.class);
			notify(mc);
			deck.getMapSideBoard().put(mc, qte);

		}

		return deck;
	}

	@Override
	public String getFileExtension() {
		return ".json";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		FileUtils.writeStringToFile(dest, toJsonDeck(deck).toString(), MTGConstants.DEFAULT_ENCODING);
	}


	public JsonObject toJsonDeck(MagicDeck deck) {
		JsonObject json = new JsonObject();
		json.addProperty(NAME, deck.getName());
		json.addProperty(DESCRIPTION, deck.getDescription());
		json.addProperty(COLORS, deck.getColors());
		json.addProperty("averagePrice", deck.getAveragePrice());
		json.add(COMMANDER,toJsonElement(deck.getCommander()));
		json.add(CREATION_DATE, new JsonPrimitive(deck.getDateCreation().getTime()));
		json.add(UPDATE_DATE, new JsonPrimitive(deck.getDateUpdate().getTime()));
		JsonArray tags = new JsonArray();
		for (String s : deck.getTags())
			tags.add(s);

		json.add(TAGS, tags);
		
		JsonArray main = new JsonArray();

		for (MagicCard mc : deck.getMap().keySet()) {
			JsonObject card = new JsonObject();
			card.addProperty("qty", (Number) deck.getMap().get(mc));
			card.add("card", toJsonElement(mc));
			main.add(card);
			notify(mc);
		}

		JsonArray side = new JsonArray();

		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			JsonObject card = new JsonObject();
			card.addProperty("qty", (Number) deck.getMapSideBoard().get(mc));
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
			notify(mc);
		}
		try (FileWriter out = new FileWriter(f)) {
			out.write(jsonparams.toString());
		}
	}

	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonArray root = new JsonParser().parse(reader).getAsJsonArray();
		List<MagicCardStock> list = new ArrayList<>();
		for (int i = 0; i < root.size(); i++) {
			JsonObject line = root.get(i).getAsJsonObject();
			MagicCardStock mc = new Gson().fromJson(line, MagicCardStock.class);
			notify(mc);
			list.add(mc);
		}

		return list;
	}


	@Override
	public String getVersion() {
		return "1.1";
	}

}
