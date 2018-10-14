package org.beta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
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
	protected static Logger logger = MTGLogger.getLogger(CollectionEvaluator.class);
	private MTGDao dao ;
	private MTGCardsProvider provider ;
	private MTGDashBoard board;
	private MagicCollection collection ;
	private File directory;
	private JsonExport serialiser;
	
	public CollectionEvaluator(MagicCollection col) throws IOException {
		directory = Paths.get(MTGConstants.CONF_DIR.getAbsolutePath(), "caches","prices").toFile();
		if(!directory.exists())
			FileUtils.forceMkdir(directory);
	
		serialiser= new JsonExport();
		collection = col;
		board = MTGControler.getInstance().getEnabled(MTGDashBoard.class);
		provider = MTGControler.getInstance().getEnabled(MTGCardsProvider.class);
		dao = MTGControler.getInstance().getEnabled(MTGDao.class);
	}
	
	public void initAllCache() throws IOException
	{
		provider.loadEditions().forEach(ed->{
			try {
				initCachePricesForEdition(ed);
			} catch (IOException e) {
				logger.error("couldn't load " + ed,e);
			}
		});
	}
	
	
	
	
	public void initCachePricesForEdition(MagicEdition edition) throws IOException
	{
			try {
				List<CardShake> ret= board.getShakeForEdition(edition);
				FileUtils.write(new File(directory,edition.getId()+"_price.json"), new JsonExport().toJsonElement(ret).toString(),MTGConstants.DEFAULT_ENCODING);
			} catch (IOException e) {
				logger.error(edition.getId() + " is not found",e);
			}
	}
	
	public Map<MagicCard,CardShake> prices() throws SQLException
	{
		Map<MagicCard,CardShake> ret = new HashMap<>();
		dao.getEditionsIDFromCollection(collection).forEach(key->{
			try {
				MagicEdition ed = provider.getSetById(key);
				
				prices(ed).entrySet().forEach(entry->{
					ret.put(entry.getKey(), entry.getValue());
				});
				
			} catch (IOException e) {
				logger.error("error loading " + key,e);
			}
			
		});
		return ret;
	}
	
	
	
	public Map<MagicCard,CardShake> prices(MagicEdition ed)
	{
		Map<MagicCard,CardShake> ret = new HashMap<>();
		try {
			File fich = new File(directory,ed.getId()+"_price.json");
			if(fich.exists())
			{
				JsonArray json= serialiser.fromJson(JsonArray.class, FileUtils.readFileToString(fich,MTGConstants.DEFAULT_ENCODING));
				
				List<CardShake> list = new ArrayList<>();
				json.forEach(el->list.add(serialiser.fromJson(CardShake.class,el.toString())));
				List<MagicCard> cards = dao.listCardsFromCollection(collection, ed);
				
				
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
			}
			else
			{
				logger.error(fich + " is not found for ed " + ed);
			}
			
		} catch (IOException|SQLException e) {
			logger.error(e);
		}
		return ret;
	}
	
	
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		
		CollectionEvaluator eval = new CollectionEvaluator(new MagicCollection("Library"));
		
		Map<MagicCard,CardShake> map = eval.prices();
		
		File total = new File("total.csv");
		
		FileUtils.write(total, "EDITION;CARDNAME;PRICE\n",MTGConstants.DEFAULT_ENCODING, true);
		
		for(Entry<MagicCard, CardShake> e : map.entrySet())
		{
			if(e.getValue()!=null) {
				FileUtils.write(total, e.getKey().getCurrentSet()+";"+e.getKey().getName()+";"+e.getValue().getPrice()+"\n",MTGConstants.DEFAULT_ENCODING, true);
			}
			else {
				FileUtils.write(total, e.getKey().getCurrentSet()+";"+e.getKey().getName()+";NC\n",MTGConstants.DEFAULT_ENCODING, true);
			}
		}
	}

}
