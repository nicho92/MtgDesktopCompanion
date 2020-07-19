package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.criterias.CardAttribute;
import org.magic.api.criterias.CardAttribute.TYPE_FIELD;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.tools.TCache;

public abstract class AbstractCardsProvider extends AbstractMTGPlugin implements MTGCardsProvider {

	public static final String SET_FIELD = "set";
	public static final String COLLECTION_FIELD = "collection";
	public static final String ALL = "all";
	
	
	protected TCache<MagicCard> cacheCards;
	private TCache<MagicEdition> cacheEditions;
	private TCache<List<MagicCard>> cacheCardsByEdition;
	
	

	public AbstractCardsProvider() {
		
		cacheCards = new TCache<>("cards");
		cacheCardsByEdition = new TCache<>("cardsByEdition");
		cacheEditions = new TCache<>("editions");
	}
	
	@Override
	public int hashCode() {
		return (getType()+getName()).hashCode();
	}
	
	@Override
	protected String getConfigDirectoryName() {
		return "cardsProviders";
	}
	
	protected abstract List<CardAttribute> loadQueryableAttributs();
	public abstract List<MagicEdition> loadEditions() throws IOException;

	
	
	@Override
	public CardAttribute[] getQueryableAttributs() {
		
		List<CardAttribute> atts = loadQueryableAttributs();
				atts.add(new CardAttribute(SET_FIELD, TYPE_FIELD.STRING));
				atts.add(new CardAttribute(COLLECTION_FIELD, TYPE_FIELD.STRING));
				atts.add(new CardAttribute(ALL, TYPE_FIELD.STRING));
		return atts.stream().toArray(CardAttribute[]::new);
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
	public MagicCard getCardById(String id) throws IOException {
		return getCardById(id,null);
	}
	
	@Override
	public List<MagicCard> searchCardByEdition(MagicEdition ed) throws IOException {
		try {
			return cacheCardsByEdition.get(ed.getId(), new Callable<List<MagicCard>>() {
				
				@Override
				public List<MagicCard> call() throws Exception {
					return searchCardByCriteria(SET_FIELD, ed.getId(), null, false);
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
		return listEditions().stream().filter(ed->ed.getSet().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	@Override
	public MagicEdition getSetById(String id) {
		
		try {
			MagicEdition ed = cacheEditions.get(id, new Callable<MagicEdition>() {
				
				@Override
				public MagicEdition call() throws Exception {
					return listEditions().stream().filter(ed->ed.getId().equalsIgnoreCase(id)).findAny().orElse(new MagicEdition(id,id));
				}
			});
			
			return (MagicEdition) BeanUtils.cloneBean(ed);
		} catch (Exception e) {
			return new MagicEdition(id,id);
		} 
		
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
				if (mc.getCurrentSet().getRarity()==MTGRarity.COMMON && !mc.isBasicLand())
					common.add(mc);

				if (mc.getCurrentSet().getRarity()==MTGRarity.UNCOMMON)
					uncommon.add(mc);

				if (mc.getCurrentSet().getRarity()==MTGRarity.RARE)
					rare.add(mc);
				
				if (mc.getCurrentSet().getRarity()==MTGRarity.MYTHIC)
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

	
	
	@Override
	public List<MagicEdition> listEditions() throws IOException {
		if(cacheEditions.isEmpty())
		{
			logger.debug("cacheEditions not loaded. Filling it");
			loadEditions().forEach(ed->cacheEditions.put(ed.getId(), ed));
		}
		
		return cacheEditions.values();
		
	}
	
	
}
