package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import com.google.gson.JsonArray;

public class CollectionEvaluator 
{
	private static final String PRICE_JSON = "_price.json";
	protected static Logger logger = MTGLogger.getLogger(CollectionEvaluator.class);
	private MagicCollection collection ;
	private File directory;
	private JsonExport serialiser;
	private Map<MagicEdition,Map<MagicCard,CardShake>> cache;
	
	
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
	
	
	
	
	public List<CardShake> initCache(MagicEdition edition) throws IOException
	{
		List<CardShake> ret = new ArrayList<>();
			try {
				ret= MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakeForEdition(edition);
				FileUtils.write(new File(directory,edition.getId()+PRICE_JSON), serialiser.toJsonElement(ret).toString(),MTGConstants.DEFAULT_ENCODING);
			} catch (IOException e) {
				logger.error(edition.getId() + " is not found",e);
			}
			return ret;
	}
	
	public List<MagicEdition> getEditions()
	{
		List<MagicEdition> eds = new ArrayList<>();
		try {
			MTGControler.getInstance().getEnabled(MTGDao.class).getEditionsIDFromCollection(collection).forEach(key->{
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
	
	
	public Map<MagicCard,CardShake> prices() throws SQLException
	{
		Map<MagicCard,CardShake> ret = new HashMap<>();
		getEditions().forEach(ed->
			prices(ed).entrySet().forEach(entry->
					ret.put(entry.getKey(), entry.getValue())
					)
		);
		return ret;
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
				logger.error(fich + " is not found for " + ed.getId() +" : " + ed.getSet());
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
			
			cache.put(ed, ret);
			
		} catch (SQLException e) {
			logger.error(e);
		}
		return ret;
	}
	
	private List<CardShake> loadFromCache(MagicEdition ed) {
		
		List<CardShake> list = new ArrayList<>();
		try {
			if(new File(directory,ed.getId()+PRICE_JSON).exists()) {	
				JsonArray json= serialiser.fromJson(JsonArray.class, FileUtils.readFileToString(new File(directory,ed.getId()+PRICE_JSON),MTGConstants.DEFAULT_ENCODING));
				json.forEach(el->list.add(serialiser.fromJson(CardShake.class,el.toString())));
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
		
		
		
		return list;
	}

	public void export(File f) throws IOException, ClassNotFoundException, SQLException {
		
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
