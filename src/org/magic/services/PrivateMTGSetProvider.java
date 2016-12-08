package org.magic.services;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class PrivateMTGSetProvider implements MagicCardsProvider {

	
	File confdir = new File(MTGDesktopCompanionControler.CONF_DIR,"sets");
	private boolean enabled;
	
	public boolean removeEdition(MagicEdition me)
	{
		return new File(confdir,me.getId()+".json").delete();

	}
	
	public PrivateMTGSetProvider() {
		if(!confdir.exists())
			confdir.mkdir();
	}
	
	public List<MagicCard> getCards(MagicEdition me) throws IOException
	{
		JsonReader reader = new JsonReader(new FileReader(new File(confdir,me.getId()+".json")));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		JsonArray arr = (JsonArray) root.get("cards");
		return (List<MagicCard>)new Gson().fromJson(arr, List.class);
	}
	
	public void addCard(MagicEdition me, MagicCard mc) throws IOException
	{
		File f = new File(confdir,me.getId()+".json");
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		JsonArray cards = root.get("cards").getAsJsonArray();
				  cards.add(new Gson().toJsonTree(mc));
		reader.close();
		
		
		FileWriter out = new FileWriter(f);
		out.write(root.toString());
		out.close();
	}
	
	private MagicEdition getEdition(File f) throws JsonSyntaxException, JsonIOException, IOException
	{
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		reader.close();
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
	
	
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException {
		PrivateMTGSetProvider manager = new PrivateMTGSetProvider();
		
		System.out.println(manager.getEdition(new File(manager.confdir,"p3ED.json")));
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicCard getCardById(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<MagicEdition> loadEditions() throws Exception {

		List<MagicEdition> ret = new ArrayList<MagicEdition>();
		for(File f : confdir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
			}))
			{
					ret.add(getEdition(f));
			}
		
		return ret;
	}

	@Override
	public MagicEdition getSetById(String id) throws Exception {
		return getEdition(new File(confdir,id+".json"));
	}

	@Override
	public String[] getLanguages() {
		return new String[]{"French"};
	}

	@Override
	public String[] getQueryableAttributs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> openBooster(MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		return "0.5";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://github.com/nicho92/MtgDesktopCompanion");
	}

	@Override
	public void enable(boolean enabled) {
		this.enabled=enabled;
		
	}

	@Override
	public boolean isEnable() {
		return enabled;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public String getName() {
		return "Personnal Data Set";
	}
	
	
	
}
