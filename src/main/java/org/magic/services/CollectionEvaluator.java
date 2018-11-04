package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.utils.patterns.observer.Observable;

import com.google.gson.JsonArray;

public class CollectionEvaluator extends Observable
{
	private static final String PRICE_JSON = "_price.json";
	protected static Logger logger = MTGLogger.getLogger(CollectionEvaluator.class);
	private MagicCollection collection ;
	private File directory;
	private JsonExport serialiser;
	private Map<MagicEdition,Map<MagicCard,CardShake>> cache;
	
	public File getDirectory() {
		return directory;
	}
	
	public CollectionEvaluator() throws IOException {
		init();
	}
	
	public CollectionEvaluator(MagicCollection c) throws IOException {
		collection=c;
		init();
	}
	
	
	public void setCollection(MagicCollection collection) {
		this.collection = collection;
		cache.clear();

	}
	
	private void init() throws IOException
	{
		cache = new HashMap<>();
		directory = Paths.get(MTGConstants.CONF_DIR.getAbsolutePath(), "caches","prices").toFile();
		if(!directory.exists())
			FileUtils.forceMkdir(directory);
	
		serialiser= new JsonExport();
	}
	
	
	public void initCache() throws IOException
	{
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions().forEach(ed->{
			try {
				initCache(ed);
			} catch (IOException e) {
				logger.error("couldn't load " + ed,e);
			}
		});
	}
	
	public void clearUICache()
	{
		cache.clear();
	}
	
	public void initCache(MagicEdition edition,List<CardShake> ret) throws IOException
	{
			try {
				FileUtils.write(new File(directory,edition.getId()+PRICE_JSON), serialiser.toJsonElement(ret).toString(),MTGConstants.DEFAULT_ENCODING,false);
			} catch (IOException e) {
				logger.error(edition.getId() + " is not found",e);
			}
			
	}
	
	
	
	public List<CardShake> initCache(MagicEdition edition) throws IOException
	{
		List<CardShake> ret = new ArrayList<>();
			try {
				logger.debug("init cache for " + edition);
				ret= MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakesForEdition(edition);
				initCache(edition,ret);
			} catch (IOException e) {
				logger.error(edition.getId() + " is not found",e);
			}
			return ret;
	}
	
	public List<MagicEdition> getEditions()
	{
		List<MagicEdition> eds = new ArrayList<>();
		try {
			MTGControler.getInstance().getEnabled(MTGDao.class).listEditionsIDFromCollection(collection).forEach(key->{
				try {
					MagicEdition ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(key);
					eds.add(ed);
				}catch(Exception e)
				{
					logger.error("error get edition " + key,e);
				}});
		} catch (SQLException e) {
			logger.error("error sql get editions ",e);
		}
		return eds;
		}
	
	
	public Map<MagicCard,CardShake> prices()
	{
		Map<MagicCard,CardShake> ret = new HashMap<>();
		getEditions().forEach(ed->
			prices(ed).entrySet().forEach(entry->
					ret.put(entry.getKey(), entry.getValue())
					)
		);
		return ret;
	}
	
	public boolean hasCache(MagicEdition ed)
	{
		return new File(directory,ed.getId()+PRICE_JSON).exists();
	}
	
	
	public Date getCacheDate(MagicEdition ed)
	{
		File fich = new File(directory,ed.getId()+PRICE_JSON);
		if(fich.exists())
		{
			List<CardShake> r = loadFromCache(ed);
			if(!r.isEmpty())
				return r.get(0).getDateUpdate();
		}
		return null;
	}
	
	
	public synchronized Map<MagicCard,CardShake> prices(MagicEdition ed)
	{
		
		if(cache.get(ed)!=null)
			return cache.get(ed);
		
		logger.trace("caculate prices for" + ed);
		
		
		Map<MagicCard,CardShake> ret = new HashMap<>();
		try {
			File fich = new File(directory,ed.getId()+PRICE_JSON);
			List<CardShake> list;
			if(fich.exists())
			{
				list=loadFromCache(ed);
			}
			else
			{
				logger.trace(fich + " is not found for " + ed.getId() +" : " + ed.getSet());
				list=new ArrayList<>();
				
			}	
			List<MagicCard> cards = MTGControler.getInstance().getEnabled(MTGDao.class).listCardsFromCollection(collection, ed);
			for(MagicCard mc : cards) 
			{
					Optional<CardShake> cs = list.stream().filter(sk->sk.getName().equals(mc.getName())).findFirst();
					if(cs.isPresent())
					{
						cs.get().setCard(mc);
						ret.put(mc, cs.get());
					}
					else
					{
						CardShake csn = new CardShake();
						csn.setName(mc.getName());
						csn.setCard(mc);
						csn.setPrice(0.0);
						ret.put(mc, csn);
					}
			}
			
			setChanged();
			notifyObservers(ed);
			
			
		} catch (SQLException e) {
			logger.error(e);
		}
		cache.put(ed, ret);
		return ret;
	}
	
	public List<CardShake> loadFromCache(MagicEdition ed) {
		
		List<CardShake> list = new ArrayList<>();
		try {
			if(new File(directory,ed.getId()+PRICE_JSON).exists()) {	
				JsonArray json= serialiser.fromJson(FileUtils.readFileToString(new File(directory,ed.getId()+PRICE_JSON),MTGConstants.DEFAULT_ENCODING),JsonArray.class);
				json.forEach(el->list.add(serialiser.fromJson(el.toString(),CardShake.class)));
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
		
		
		
		return list;
	}

	public void export(File f) throws IOException{
		
		FileUtils.write(f, "EDITION;CARDNAME;PRICE\n",MTGConstants.DEFAULT_ENCODING, true);
		for(Entry<MagicCard, CardShake> e : prices().entrySet())
		{
			if(e.getValue()!=null) 
			{
				FileUtils.write(f, e.getKey().getCurrentSet()+";"+e.getKey().getName()+";"+e.getValue().getPrice()+"\n",MTGConstants.DEFAULT_ENCODING, true);
			}
			else 
			{
				FileUtils.write(f, e.getKey().getCurrentSet()+";"+e.getKey().getName()+";NC\n",MTGConstants.DEFAULT_ENCODING, true);
			}
		}
	}
	
	
	public Double total(MagicEdition ed) {
		Double price=0.0;
		for(CardShake cs : prices(ed).values())
			price=price+cs.getPrice();
		
		return price;
	}

	public Double total() {
		Double total=0.0;
		for(MagicEdition ed : getEditions())
			total=total+total(ed);
		
		return total;
	}
}
