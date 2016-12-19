package org.magic.api.providers.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.services.MTGDesktopCompanionControler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class PrivateMTGSetProvider implements MagicCardsProvider {
	
	File confdir = new File(MTGDesktopCompanionControler.CONF_DIR,"sets");
	private boolean enabled;
	static final Logger logger = LogManager.getLogger(PrivateMTGSetProvider.class.getName());

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
		Type listType = new TypeToken<ArrayList<MagicCard>>(){}.getType();
		return (List<MagicCard>)new Gson().fromJson(arr,listType);
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
	
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicCard getCardById(String id) throws Exception {
		return searchCardByCriteria("id", id, null).get(0);
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me) throws Exception {
		
		List<MagicCard> res = new ArrayList<MagicCard>();
		
		if(me==null)
		{
			for(MagicEdition ed : loadEditions())
				for(MagicCard mc : getCards(ed))
					if(hasValue(mc, att, crit))
						res.add(mc);
		}
		else
		{
			for(MagicCard mc : getCards(me))
			{
				if(hasValue(mc, att, crit))
					res.add(mc);
			}
		}
		
		return res;
	}
	
	private boolean hasValue(MagicCard mc,String att, String val)
	{
		try {
			logger.debug(mc +" " + att +" " + val);
			return BeanUtils.getProperty(mc, att).toUpperCase().contains(val.toUpperCase());
		} catch (Exception e) {
			logger.error(e);
			return false;
		} 
	}
	

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception {
		MagicEdition ed = getSetById(me.getId());
		
		for(MagicCard mc : getCards(ed))
			if(mc.getNumber().equals(id))
				return mc;
		
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
		try {
			Set<String> keys = BeanUtils.describe(new MagicCard()).keySet();
			return keys.toArray(new String[keys.size()]);
		} catch (Exception e) {
			logger.error(e);
			return new String[0];
		} 
	}

	@Override
	public List<MagicCard> openBooster(MagicEdition me) throws Exception {
		return null;
	}

	@Override
	public String getVersion() {
		return "0.1";
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
		return "Personnal Data Set Provider";
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
