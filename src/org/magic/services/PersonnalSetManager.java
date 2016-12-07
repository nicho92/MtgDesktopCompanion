package org.magic.services;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

import com.google.gson.Gson;
import com.google.gson.JsonArray;import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class PersonnalSetManager {

	
	File confdir = new File(MTGDesktopCompanionControler.CONF_DIR,"sets");
	
	public List<MagicEdition> listEditions()
	{
		
		List<MagicEdition> ret = new ArrayList<MagicEdition>();
		for(File f : confdir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
			}))
		{
			try {
				ret.add(getEdition(f));
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	
	public boolean removeEdition(MagicEdition me)
	{
		return new File(confdir,me.getId()+".json").delete();

	}
	
	public PersonnalSetManager() {
		if(!confdir.exists())
			confdir.mkdir();
	}
	
	public void addCard(MagicEdition me, MagicCard mc) throws IOException
	{
		FileWriter out = new FileWriter(new File(confdir,me.getId()));
		
		
	}
	
	
	public MagicEdition getEdition(File f) throws JsonSyntaxException, JsonIOException, FileNotFoundException
	{
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		return new Gson().fromJson(root.get("main"),MagicEdition.class);
	}
	
	
	public void saveEdition(MagicEdition me) throws IOException
	{
		JsonObject jsonparams = new JsonObject();
				   jsonparams.add("main",new Gson().toJsonTree(me));
				   jsonparams.add("cards",new JsonArray());
		
		FileWriter out = new FileWriter(new File(confdir,me.getId()+".json"));
		out.write(jsonparams.toString());
		out.close();
	}
	
	
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		PersonnalSetManager manager = new PersonnalSetManager();
		
		System.out.println(manager.getEdition(new File(manager.confdir,"p3ED.json")));
	}
	
	
	
}
