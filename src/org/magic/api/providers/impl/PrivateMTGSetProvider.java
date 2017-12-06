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
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class PrivateMTGSetProvider implements MagicCardsProvider {
	
	public static File confdir = new File(MTGControler.CONF_DIR,"sets");
	private boolean enabled;
	Logger logger = MTGLogger.getLogger(this.getClass());
	public void removeEdition(MagicEdition me)
	{
		File f = new File(confdir,me.getId()+".json");
		try {
			logger.debug("delete : " + f);
			FileUtils.forceDelete(f);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	public boolean removeCard(MagicEdition me,MagicCard mc) throws IOException
	{
		File f = new File(confdir,me.getId()+".json");
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		JsonArray cards = root.get("cards").getAsJsonArray();
		
		for(int i=0;i<cards.size();i++)
		{
			JsonElement el = cards.get(i);
			if(el.getAsJsonObject().get("id").getAsString().equals(mc.getId()))
			{
				cards.remove(el);
				FileWriter out = new FileWriter(new File(confdir,me.getId()+".json"));
				out.write(root.toString());
				out.close();
				return true;
			}
		}
		return false;
	}
	
	
	public PrivateMTGSetProvider() {
		if(!confdir.exists())
			confdir.mkdir();
	}
	
	public List<MagicCard> getCards(MagicEdition me) throws IOException
	{
		FileReader fr = new FileReader(new File(confdir,me.getId()+".json"));
		JsonReader reader = new JsonReader(fr);
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		JsonArray arr = (JsonArray) root.get("cards");
		Type listType = new TypeToken<ArrayList<MagicCard>>(){}.getType();
		fr.close();
		reader.close();
		return (List<MagicCard>)new Gson().fromJson(arr,listType);
	}
	
	public void addCard(MagicEdition me, MagicCard mc) throws IOException
	{
		File f = new File(confdir,me.getId()+".json");
		JsonReader reader = new JsonReader(new FileReader(f));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		JsonArray cards = root.get("cards").getAsJsonArray();
		
		int index = indexOf(mc,cards);
		
		if(index>-1)
		{
			cards.set(index, new Gson().toJsonTree(mc));
		}
		else
		{
			cards.add(new Gson().toJsonTree(mc));
			me.setCardCount(me.getCardCount()+1);
			root.addProperty("cardCount", me.getCardCount());
		}
		reader.close();
		
		
		FileWriter out = new FileWriter(f);
		out.write(root.toString());
		out.close();
	}
	
	private int indexOf(MagicCard mc, JsonArray arr) {
		for(int i=0;i<arr.size();i++)
			if(arr.get(i).getAsJsonObject().get("id")!=null)
				if(arr.get(i).getAsJsonObject().get("id").getAsString().equals(mc.getId()))
					return i;
		
		return -1;
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
		int cardCount=0;
		try{
			cardCount=getCards(me).size();
			
		}
		catch(Exception e)
		{	}
		
		me.setCardCount(cardCount);
		
		JsonObject jsonparams = new JsonObject();
				   jsonparams.add("main",new Gson().toJsonTree(me));
				  
				   if(cardCount==0)
					   jsonparams.add("cards",new JsonArray());
				   else
					   jsonparams.add("cards",new Gson().toJsonTree(getCards(me)));
		
		FileWriter out = new FileWriter(new File(confdir,me.getId()+".json"));
		out.write(jsonparams.toString());
		out.close();
	}
	
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public MagicCard getCardById(String id,MagicEdition ed){
		try {
			return searchCardByCriteria("id", id, ed).get(0);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public MagicCard getCardById(String id){
		try {
			return searchCardByCriteria("id", id, null).get(0);
		} catch (Exception e) {
			return null;
		}
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
	public Booster generateBooster(MagicEdition me) throws Exception {
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
