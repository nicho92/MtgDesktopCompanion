package org.magic.api.exports.impl;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.adapters.ColorAdapter;
import org.magic.services.adapters.DeckAdapter;
import org.magic.services.adapters.FileAdapter;
import org.magic.services.adapters.InstantAdapter;
import org.magic.services.adapters.MTGProductAdapter;
import org.magic.services.adapters.MTGStockItemAdapter;
import org.magic.services.adapters.NetworkInfoAdapter;
import org.magic.services.adapters.StackTraceElementAdapter;
import org.magic.services.adapters.UserAgentAdapter;
import org.magic.services.network.URLTools;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.POMReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import nl.basjes.parse.useragent.UserAgent;

public class JsonExport extends AbstractCardExport {


	private Gson gson;

	
	private GsonBuilder init()
	{
		return new GsonBuilder()
				.registerTypeAdapter(MTGStockItem.class, new MTGStockItemAdapter())
				.registerTypeAdapter(Instant.class, new InstantAdapter())
				.registerTypeAdapter(UserAgent.class, new UserAgentAdapter())
				.registerTypeAdapter(StackTraceElement.class, new StackTraceElementAdapter())
				.registerTypeHierarchyAdapter(NetworkInfo.class, new NetworkInfoAdapter())
				.registerTypeHierarchyAdapter(File.class, new FileAdapter())
				.registerTypeHierarchyAdapter(Color.class, new ColorAdapter())
				.registerTypeHierarchyAdapter(MTGDeck.class, new DeckAdapter())
				.registerTypeAdapter(MTGProduct.class, new MTGProductAdapter())
				.setDateFormat("yyyy-MM-dd hh:mm");
	}
	
	


	public Gson getEngine() {
		return gson;
	}
	
	public JsonExport() {
		gson=init()
				.setPrettyPrinting()
				.create();
	}

	public void removePrettyString() {
		gson=init().create();
	}

	public String toJson(Object o)
	{
		return gson.toJson(o);
	}

	public JsonElement toJsonElement(Object o)
	{
		return gson.toJsonTree(o);
	}

	public JsonArray toJsonArray(Object o)
	{
		return toJsonElement(o).getAsJsonArray();
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
		
		if(json==null)
			json=new JsonArray();
		
		json.forEach(el->list.add(gson.fromJson(el.toString(),classe)));
		return list;
	}


	public <T> List<T> fromJsonList(String s,Class<T> classe)
	{
		List<T> list = new ArrayList<>();
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
	public MTGDeck importDeck(String f,String name)  {
		return gson.fromJson(f, MTGDeck.class);
	}

	@Override
	public String getStockFileExtension() {
		return ".json";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		FileTools.saveFile(dest, gson.toJson(deck));
	}



	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

		var jsonparams = new JsonArray();

		for (MTGCardStock mc : stock) {
			jsonparams.add(gson.toJsonTree(mc));
			notify(mc.getProduct());
		}
		try (var out = new FileWriter(f)) {
			out.write(jsonparams.toString());
		}
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		var root = URLTools.toJson(content).getAsJsonArray();
		List<MTGCardStock> list = new ArrayList<>();
		for (var i = 0; i < root.size(); i++) {
			var line = root.get(i).getAsJsonObject();
			var mc = gson.fromJson(line, MTGCardStock.class);
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


	@Override
	public String getName() {
		return "Json";
	}


}
