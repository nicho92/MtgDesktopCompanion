package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.TCache;

public abstract class AbstractCardsProvider extends AbstractMTGPlugin implements MTGCardsProvider {

	protected TCache<MagicCard> cacheCards;
	protected TCache<MagicEdition> cacheEditions;
	private TCache<List<MagicCard>> cacheCardsByEdition;
	
	

	public AbstractCardsProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "cardsProviders");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
		
		
		cacheCards = new TCache<>("cards");
		cacheCardsByEdition = new TCache<>("cardsByEdition");
		cacheEditions = new TCache<>("editions");

	}
	
	
	@Override
	public int hashCode() {
		return (getType()+getName()).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	public TCache<MagicCard> getCacheCards()
	{
		return cacheCards;
	}
	
	public TCache<MagicEdition> getCacheEditions()
	{
		return cacheEditions;
	}
	
	public TCache<List<MagicCard>> getCacheCardsEdition()
	{
		return cacheCardsByEdition;
	}
	
	
	
	@Override
	public MagicCard getCardByNumber(String id, String idMe) throws IOException {
		return getCardByNumber(id, getSetById(idMe));
	}
	
	
	
	@Override
	public List<MagicCard> searchCardByEdition(MagicEdition ed) throws IOException {
		try {
			return cacheCardsByEdition.get(ed.getId(), new Callable<List<MagicCard>>() {
				
				@Override
				public List<MagicCard> call() throws Exception {
					return searchCardByCriteria("set", ed.getId(), null, false);
				}
			});
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
	
	}
	
	@Override
	public List<MagicCard> searchCardByName(String name, MagicEdition me, boolean exact) throws IOException {
		return searchCardByCriteria("name",name, me, exact);
	}
	
	@Override
	public MagicEdition getSetByName(String name) throws IOException {
		return loadEditions().stream().filter(ed->ed.getSet().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	@Override
	public List<MagicCard> listAllCards() throws IOException {
		return searchCardByName("", null, false);
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.PROVIDER;
	}

	public Booster generateBooster(MagicEdition me) throws IOException {

		logger.debug("opening booster for " + me);
		List<MagicCard> common = new ArrayList<>();
		List<MagicCard> uncommon = new ArrayList<>();
		List<MagicCard> rare = new ArrayList<>();
		List<MagicCard> lands = new ArrayList<>();
		Booster b = new Booster();
	
		try {
			for (MagicCard mc : searchCardByEdition(me))
			{
				if (mc.getCurrentSet().getRarity().equalsIgnoreCase("common") && !mc.isBasicLand())
					common.add(mc);

				if (mc.getCurrentSet().getRarity().equalsIgnoreCase("uncommon"))
					uncommon.add(mc);

				if (mc.getCurrentSet().getRarity().toLowerCase().contains("rare"))
					rare.add(mc);
				
				if (mc.getCurrentSet().getRarity().toLowerCase().contains("mythic"))
					rare.add(mc);
				
				
				if (mc.isBasicLand())
					lands.add(mc);

			}
			Collections.shuffle(lands);
			Collections.shuffle(common);
			Collections.shuffle(uncommon);
			Collections.shuffle(rare);
		} catch (Exception e) {
			logger.error("Error opening booster", e);
		}

		List<MagicCard> resList = new ArrayList<>();
		resList.addAll(common.subList(0, 11));
		resList.addAll(uncommon.subList(0, 3));
		resList.add(rare.get(0));

		if (!lands.isEmpty())
			resList.addAll(lands.subList(0, 1));

		b.setCards(resList);
		b.setEdition(me);
		
		logger.trace(b.getEdition() + ":" + b + ":" + b.getCards());

		return b;
	}
	
	
}
