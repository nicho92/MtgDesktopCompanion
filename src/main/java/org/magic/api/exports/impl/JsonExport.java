package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class JsonExport  extends AbstractCardExport {

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	@Override
	public MagicDeck importDeck(File f) throws IOException {
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		
		MagicDeck deck  = new MagicDeck();
				  deck.setDateCreation(new Date());
				  
				  if(!root.get("name").isJsonNull())
					  deck.setName(root.get("name").getAsString());
				  
				  if(!root.get("description").isJsonNull())
					  deck.setDescription(root.get("description").getAsString());
				  
				  if(!root.get("tags").isJsonNull())
				  {
					  JsonArray arr = root.get("tags").getAsJsonArray();
					  for(int i=0;i<arr.size();i++)
						  deck.getTags().add(arr.get(i).getAsString());
				  }
				  
				  
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
	public String getFileExtension() {
		return ".json";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		JsonObject json = new JsonObject();
		   		   json.addProperty("name", deck.getName());
		   		   json.addProperty("description", deck.getDescription());
		   		   json.addProperty("colors", deck.getColors());
		   		   json.addProperty("averagePrice", deck.getAveragePrice());
		   		   
		   		   JsonArray tags= new JsonArray();
		   		   for(String s : deck.getTags())
		   			   tags.add(s);
		   		   
		   		   json.add("tags", tags);
	
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
		
		FileUtils.writeStringToFile(dest, json.toString(),"UTF-8");
	}

	@Override
	public String getName() {
		return "Json";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(CSVExport.class.getResource("/icons/json.png"));
	}


	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
				
			JsonArray jsonparams = new JsonArray();
			   
			for(MagicCardStock mc : stock)
			{
				jsonparams.add(new Gson().toJsonTree(mc));
			}
			try(FileWriter out = new FileWriter(f))
			{
				out.write(jsonparams.toString());
			}
	}


	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonArray root = new JsonParser().parse(reader).getAsJsonArray();
		List<MagicCardStock> list = new ArrayList<>();
		for(int i = 0;i<root.size();i++)
		{
			JsonObject line = root.get(i).getAsJsonObject();
			MagicCardStock mc = new Gson().fromJson(line, MagicCardStock.class);
			list.add(mc);
		}
		
		return list;
	}


	@Override
	public void initDefault() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getVersion() {
		return "1.1";
	}

}
