package org.magic.services;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;

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
import java.util.TreeMap;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
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

	public File getDirectory() {
		return directory;
	}

	public static Map<MTGEdition, Integer> analyse(MTGCollection collection) throws IOException
	{
		var ret = new TreeMap<MTGEdition, Integer>();

		try {
			var temp = getEnabledPlugin(MTGDao.class).getCardsCountGlobal(collection);
			for (MTGEdition me : getEnabledPlugin(MTGCardsProvider.class).listEditions()) {
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

	public void clearUICache()
	{
		cache.clear();
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

	public List<MTGEdition> getEditions()
	{
		List<MTGEdition> eds = new ArrayList<>();
		try {
			getEnabledPlugin(MTGDao.class).listEditionsIDFromCollection(collection).forEach(key->{
				try {
					MTGEdition ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(key);
					eds.add(ed);
				}catch(Exception e)
				{
					logger.error("error get edition {}",key,e);
				}});
		} catch (SQLException e) {
			logger.error("error sql get editions ",e);
		}
		return eds;
		}


	public Map<MTGCard,CardShake> prices()
	{
		Map<MTGCard,CardShake> ret = new HashMap<>();
		getEditions().forEach(ed->
			prices(ed).entrySet().forEach(entry->
					ret.put(entry.getKey(), entry.getValue())
					)
		);
		return ret;
	}

	public boolean hasCache(MTGEdition ed)
	{
		return new File(directory,ed.getId()+PRICE_JSON).exists();
	}


	public Date getCacheDate(MTGEdition ed)
	{
		var fich = new File(directory,ed.getId()+PRICE_JSON);
		if(fich.exists())
		{
			EditionsShakers r = loadFromCache(ed);
			if(!r.isEmpty())
				return r.getDate();
		}
		return null;
	}


	public synchronized Map<MTGCard,CardShake> prices(MTGEdition ed)
	{

		if(cache.get(ed)!=null)
			return cache.get(ed);

		logger.trace("caculate prices for {}",ed);


		Map<MTGCard,CardShake> ret = new HashMap<>();
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
			List<MTGCard> cards = getEnabledPlugin(MTGDao.class).listCardsFromCollection(collection, ed);
			for(MTGCard mc : cards)
			{
					Optional<CardShake> cs = list.getShakes().stream().filter(sk->sk.getName().equals(mc.getName())).findFirst();
					if(cs.isPresent())
					{

						CardShake shak = cs.get();
						shak.setCard(mc);

						if(shak.getPrice()>=minPrice)
							ret.put(mc, shak);
					}
					else
					{
						var csn = new CardShake();
						csn.setName(mc.getName());
						csn.setCard(mc);
						csn.setPrice(0.0);

						if(csn.getPrice()>=minPrice)
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

	public void export(File f) throws IOException{

		var temp = new StringBuilder("EDITION;CARDNAME;PRICE");
		temp.append(System.lineSeparator());
		for(Entry<MTGCard, CardShake> e : prices().entrySet())
		{
			if(e.getValue()!=null)
			{
				temp.append(e.getKey().getEdition()).append(";").append(e.getKey().getName()).append(";").append(e.getValue().getPrice()).append(System.lineSeparator());
			}
			else
			{
				temp.append(e.getKey().getEdition()).append(";").append(e.getKey().getName()).append(";").append("NC").append(System.lineSeparator());
			}
		}
		FileTools.saveFile(f, temp.toString());

	}


	public Double total(MTGEdition ed) {
		return prices(ed).values().stream().mapToDouble(CardShake::getPrice).sum();
	}

	public Double total() {
		double total=0.0;
		for(MTGEdition ed : getEditions())
			total=total+total(ed);

		return total;
	}

	public void setMinPrice(int i) {
		this.minPrice=i;

	}
}
