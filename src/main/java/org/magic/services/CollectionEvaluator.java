package org.magic.services;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.core.Logger;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.FileTools;
import org.utils.patterns.observer.Observable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CollectionEvaluator extends Observable
{
	private static final String PRICE_JSON = "_price.json";
	private MTGCollection collection ;
	private File directory;
	private JsonExport serialiser;
	private Map<MTGEdition,Map<MTGCard,CardShake>> cache;
	private int minPrice=0;
	private static Logger logger = MTGLogger.getLogger(CollectionEvaluator.class);
	public File getDirectory() {
		return directory;
	}

	public static Map<MTGEdition, Integer> analyse(MTGCollection collection) throws IOException
	{
		var ret = new TreeMap<MTGEdition, Integer>();

		try {
			logger.debug("Evaluate collection {}",collection);
			var temp = getEnabledPlugin(MTGDao.class).getCardsCountGlobal(collection);
			for (var me : getEnabledPlugin(MTGCardsProvider.class).listEditions()) {
				ret.put(me, (temp.get(me.getId()) == null) ? 0 : temp.get(me.getId()));
			}
		} catch (SQLException e) {
			logger.error("error in calculation",e);
		}

		return ret;

	}

	public static JsonArray analyseToJson(MTGCollection collection) throws IOException
	{
		var transformer = new JsonExport();
		var arr = new JsonArray();
		analyse(collection).entrySet().forEach(entry->{
			
			if(entry.getValue()>0)
			{
			
			var obj = new JsonObject();
			obj.add("edition", transformer.toJsonElement(entry.getKey()));
			obj.addProperty("set", entry.getKey().getId());
			obj.addProperty("name", entry.getKey().getSet());
			obj.addProperty("release", entry.getKey().getReleaseDate());
			obj.add("qty", new JsonPrimitive(entry.getValue()));
			obj.add("cardNumber", new JsonPrimitive(entry.getKey().getCardCount()));
			obj.addProperty("defaultLibrary", MTGControler.getInstance().get("default-library"));
			double pc = 0;
			if (entry.getKey().getCardCount() > 0)
				pc = entry.getValue().doubleValue() / entry.getKey().getCardCount();
			else
				pc = entry.getValue().doubleValue();

			obj.add("pc", new JsonPrimitive(pc));

			arr.add(obj);
			}
		});
		return arr;

	}




	public CollectionEvaluator() throws IOException {
		init();
	}

	public CollectionEvaluator(MTGCollection c) throws IOException {
		collection=c;
		init();
	}


	public void setCollection(MTGCollection collection) {
		this.collection = collection;
		cache.clear();

	}

	private void init() throws IOException
	{
		cache = new HashMap<>();
		directory = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "prices").toFile();
		if(!directory.exists())
			FileTools.forceMkdir(directory);



		serialiser= new JsonExport();
	}


	public void initCache() throws IOException
	{
		getEnabledPlugin(MTGCardsProvider.class).listEditions().forEach(ed->{
			try {
				initCache(ed);
			} catch (IOException e) {
				logger.error("couldn't load {}",ed,e);
			}
		});
	}

	public void initCache(MTGEdition edition,EditionsShakers ret) throws IOException
	{
		try {
			if(!ret.isEmpty())
				FileTools.saveFile(new File(directory,edition.getId()+PRICE_JSON), serialiser.toJsonElement(ret).toString());
		} catch (IOException e) {
			logger.error("{} is not found",edition.getId(),e);
		}

	}


	public EditionsShakers initCache(MTGEdition edition,String provider) throws IOException
	{
		var ret = new EditionsShakers();
			try {
				ret= getPlugin(provider, MTGDashBoard.class).getShakesForEdition(edition);
			} catch (Exception e) {
				logger.error("{} is not found ",edition.getId(),e);
			}
			return ret;
	}


	public EditionsShakers initCache(MTGEdition edition) throws IOException
	{
		return initCache(edition,getEnabledPlugin(MTGDashBoard.class).getName());
	}

	public Date getCacheDate(MTGEdition ed)
	{
		var fich = new File(directory,ed.getId()+PRICE_JSON);
		if(fich.exists())
		{
			var r = loadFromCache(ed);
			if(!r.isEmpty())
				return r.getDate();
		}
		return null;
	}


	private synchronized Map<MTGCard,CardShake> prices(MTGEdition ed)
	{

		if(cache.get(ed)!=null)
			return cache.get(ed);

		logger.trace("caculate prices for {}",ed);


		var ret = new HashMap<MTGCard,CardShake>();
		try {
			var fich = new File(directory,ed.getId()+PRICE_JSON);
			EditionsShakers list;
			if(fich.exists())
			{
				list=loadFromCache(ed);
			}
			else
			{
				logger.trace("{} is not found for {}: {}",fich,ed.getId(),ed.getSet());
				list= new EditionsShakers();
			}
			var cards = getEnabledPlugin(MTGDao.class).listCardsFromCollection(collection, ed);
			for(MTGCard mc : cards)
			{
					var cs = list.getShakes().stream().filter(sk->sk.getName().equals(mc.getName())).findFirst();
					if(cs.isPresent())
					{
						var shak = cs.get();
						if(shak.getPrice().doubleValue()>=minPrice)
							ret.put(mc, shak);
					}
					else
					{
						var csn = new CardShake();
						csn.setName(mc.getName());
						csn.setPrice(0.0);

						if(csn.getPrice().doubleValue()>=minPrice)
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

	public EditionsShakers loadFromCache(MTGEdition ed) {
		try {
			if(new File(directory,ed.getId()+PRICE_JSON).exists()) {
				return serialiser.fromJson(FileTools.readFile(new File(directory,ed.getId()+PRICE_JSON),MTGConstants.DEFAULT_ENCODING),EditionsShakers.class);
			}
		}
		catch(Exception e)
		{
			logger.error("error loading {}",ed, e);
		}

		var eds = new EditionsShakers();
		eds.setEdition(ed);
		return eds;


	}


	public Double total(MTGEdition ed) {
		return prices(ed).values().stream().mapToDouble(cs->cs.getPrice().doubleValue()).sum();
	}

	public void setMinPrice(int i) {
		this.minPrice=i;

	}
}
