package org.magic.api.interfaces;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.criterias.CardAttribute;
import org.magic.api.criterias.MTGCrit;

public interface MTGCardsProvider extends MTGPlugin {


	public void init();

	public MagicCard getCardById(String id) throws IOException;

	public MagicCard getCardById(String id,MagicEdition ed) throws IOException;
	
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact) throws IOException;
	
	public List<MagicCard> searchCardByEdition(MagicEdition ed) throws IOException;
	
	public List<MagicCard> searchCardByName(String name, MagicEdition me, boolean exact) throws IOException;

	public List<MagicCard> listAllCards() throws IOException;
	
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException;

	public MagicCard getCardByNumber(String id, String idMe) throws IOException;
	
	public List<MagicEdition> listEditions() throws IOException;

	public MagicEdition getSetById(String id) throws IOException;
	
	public MagicEdition getSetByName(String name) throws IOException;

	public String[] getLanguages();

	public CardAttribute[] getQueryableAttributs();

	public Booster generateBooster(MagicEdition me) throws IOException;

	public URL getWebSite() throws MalformedURLException;

	public List<MagicCard> searchByCriteria(MTGCrit<?>... crits) throws IOException;


}
