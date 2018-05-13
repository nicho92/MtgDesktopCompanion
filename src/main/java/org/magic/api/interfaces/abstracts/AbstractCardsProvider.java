package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
		return searchCardByCriteria("set", ed.getId(), null, false);
	}
	
	
	@Override
	public List<MagicCard> searchCardByName(String name, MagicEdition me, boolean exact) throws IOException {
		return searchCardByCriteria("name",name, me, exact);
	}
	

	@Override
	public PLUGINS getType() {
		return PLUGINS.PROVIDER;
	}

}
