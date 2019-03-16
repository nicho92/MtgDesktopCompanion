package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractCardsProvider extends AbstractMTGPlugin implements MTGCardsProvider {

	protected Map<String, MagicCard> cacheCards;
	protected Map<String, List<MagicCard>> cacheBoosterCards;
	protected Map<String, MagicEdition> cacheEditions;

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
		cacheCards = new HashMap<>();
		cacheBoosterCards = new HashMap<>();
		cacheEditions = new TreeMap<>();

	}
	
	@Override
	public List<MagicCard> searchCardByEdition(MagicEdition ed) throws IOException {
		
		if(cacheBoosterCards.get(ed.getId()) != null)
			return cacheBoosterCards.get(ed.getId());
		
		cacheBoosterCards.put(ed.getId(), searchCardByCriteria("set", ed.getId(), null, false));
		
		return cacheBoosterCards.get(ed.getId());
	}
	
	
	@Override
	public List<MagicCard> searchCardByName(String name, MagicEdition me, boolean exact) throws IOException {
		return searchCardByCriteria("name",name, me, exact);
	}
	
	
	@Override
	public MagicEdition getSetByName(String name) throws IOException {
		return loadEditions().parallelStream().filter(ed->ed.getSet().equalsIgnoreCase(name)).findFirst().orElse(null);
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
			if (cacheBoosterCards.get(me.getId()) == null)
				cacheBoosterCards.put(me.getId(), searchCardByEdition(me));

			for (MagicCard mc : cacheBoosterCards.get(me.getId())) {
				if (mc.getCurrentSet().getRarity().equalsIgnoreCase("common"))
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

		return b;
	}
	
	
}
