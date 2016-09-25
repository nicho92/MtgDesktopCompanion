package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MagicFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class JsonExport  extends AbstractCardExport {

	
	public static void main(String[] args) throws Exception {
		JsonExport exp = new JsonExport();
		exp.export(new MTGDesktopCompanionExport().importDeck(new File(MagicFactory.CONF_DIR,"\\decks\\RW Angels.deck")),new File("c:/test.json"));
	}
	
	public JsonExport() {
		super();
	}
	
	
	@Override
	public MagicDeck importDeck(File f) throws Exception {
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		
		MagicDeck deck  = new MagicDeck();
				  
				  if(!root.get("name").isJsonNull())
					  deck.setName(root.get("name").getAsString());
				  
				  if(!root.get("description").isJsonNull())
					  deck.setDescription(root.get("description").getAsString());
				  
		JsonArray main = root.get("main").getAsJsonArray();
		
		for(int i = 0;i<main.size();i++)
		{
			JsonObject line = main.get(i).getAsJsonObject();
			int qte = line.get("qty").getAsInt();
			MagicCard mc = new Gson().fromJson(line.get("card"), MagicCard.class);
			deck.getMap().put(mc, qte);
		}
		
		JsonArray side = root.get("side").getAsJsonArray();
		
		for(int i = 0;i<side.size();i++)
		{
			JsonObject line = side.get(i).getAsJsonObject();
			int qte = line.get("qty").getAsInt();
			MagicCard mc = new Gson().fromJson(line.get("card"), MagicCard.class);
			deck.getMapSideBoard().put(mc, qte);
			
		}
		
		
		
		
		return deck;
	}
	
	
	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		JsonArray jsonparams = new JsonArray();
		   
		for(MagicCard mc : cards)
		{
			jsonparams.add(new Gson().toJsonTree(mc));
		}
		
		FileWriter out = new FileWriter(f);
		out.write(jsonparams.toString());
		out.close();

	}

	@Override
	public String getFileExtension() {
		return ".json";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		JsonObject json = new JsonObject();
		   		   json.addProperty("name", deck.getName());
		   		   json.addProperty("description", deck.getDescription());
		   		   json.addProperty("colors", deck.getColors());
	
		JsonArray main = new JsonArray();
		   		   
		for(MagicCard mc : deck.getMap().keySet())
		{
			JsonObject card = new JsonObject();
				card.addProperty("qty",(Number)deck.getMap().get(mc));
				card.add("card", new Gson().toJsonTree(mc));
				main.add(card);
		}
		
		JsonArray side = new JsonArray();
		   
		for(MagicCard mc : deck.getMapSideBoard().keySet())
		{
			JsonObject card = new JsonObject();
				card.addProperty("qty",(Number)deck.getMapSideBoard().get(mc));
				card.add("card", new Gson().toJsonTree(mc));
				side.add(card);
		}
		json.add("main", main);
		json.add("side", side);
		
		FileWriter out = new FileWriter(dest);
		out.write(json.toString());
		out.close();
	}

	@Override
	public String getName() {
		return "Json";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(CSVExport.class.getResource("/res/json.png"));
	}

}
