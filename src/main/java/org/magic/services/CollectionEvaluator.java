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
	
	public CollectionEvaluator() throws IOException {
		init();
	}
	
	public CollectionEvaluator(MagicCollection c) throws IOException {
		collection=c;
		init();
	}
	
	
	public void setCollection(MagicCollection collection) {
		this.collection = collection;

	}
	
	private void init() throws IOException
	{
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
	
	
	
	
	public void initCache(MagicEdition edition) throws IOException
	{
			try {
				List<CardShake> ret= MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakeForEdition(edition);
				FileUtils.write(new File(directory,edition.getId()+PRICE_JSON), serialiser.toJsonElement(ret).toString(),MTGConstants.DEFAULT_ENCODING);
			} catch (IOException e) {
				logger.error(edition.getId() + " is not found",e);
			}
	}
	
	public Map<MagicCard,CardShake> prices() throws SQLException
	{
		Map<MagicCard,CardShake> ret = new HashMap<>();
		MTGControler.getInstance().getEnabled(MTGDao.class).getEditionsIDFromCollection(collection).forEach(key->{
			try {
				MagicEdition ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(key);
				
				prices(ed).entrySet().forEach(entry->{
					ret.put(entry.getKey(), entry.getValue());
				});
				
			} catch (IOException e) {
				logger.error("error loading " + key,e);
			}
			
		});
		return ret;
	}
	
	public Date getCacheDate(MagicEdition ed) throws IOException
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
	
	
	public Map<MagicCard,CardShake> prices(MagicEdition ed)
	{
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
				list = MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakeForEdition(ed);
				initCache(ed);
			}	
			
			
			List<MagicCard> cards = MTGControler.getInstance().getEnabled(MTGDao.class).listCardsFromCollection(collection, ed);
			for(MagicCard mc : cards) 
			{
					Optional<CardShake> cs = list.stream().filter(sk->sk.getName().startsWith(mc.getName())).findFirst();
					if(cs.isPresent())
					{
						cs.get().setCard(mc);
						ret.put(mc, cs.get());
					}
					else
					{
						ret.put(mc, null);
					}
				}
			
			
			
		} catch (IOException|SQLException e) {
			logger.error(e);
		}
		return ret;
	}
	
	
	
	
	public List<CardShake> loadFromCache(MagicEdition ed) throws IOException {
		List<CardShake> list = new ArrayList<>();
		JsonArray json= serialiser.fromJson(JsonArray.class, FileUtils.readFileToString(new File(directory,ed.getId()+PRICE_JSON),MTGConstants.DEFAULT_ENCODING));
		json.forEach(el->list.add(serialiser.fromJson(CardShake.class,el.toString())));
		
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

}
