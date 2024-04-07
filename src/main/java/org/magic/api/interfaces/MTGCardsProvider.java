package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGBooster;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCardVariation;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;

public interface MTGCardsProvider extends MTGPlugin {


	public void init();

	public MTGCard getCardById(String id) throws IOException;
	
	public MTGCard getCardByScryfallId(String crit) throws IOException;

	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact) throws IOException;

	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact, EnumCardVariation extra ) throws IOException;

	public List<MTGCard> searchCardByEdition(MTGEdition ed) throws IOException;

	public List<MTGCard> searchCardByName(String name, MTGEdition me, boolean exact) throws IOException;

	public List<MTGCard> searchCardByName(String name, MTGEdition me, boolean exact, EnumCardVariation extra ) throws IOException;

	public List<MTGCard> listAllCards() throws IOException;

	public MTGCard getCardByNumber(String num, MTGEdition me) throws IOException;

	public MTGCard getCardByNumber(String num, String idMe) throws IOException;

	public List<MTGEdition> listEditions() throws IOException;

	public MTGEdition getSetById(String id) throws IOException;

	public MTGEdition getSetByName(String name) throws IOException;

	public String[] getLanguages();

	public QueryAttribute[] getQueryableAttributs();

	public List<MTGBooster> generateBooster(MTGEdition me, EnumExtra typeBooster,int qty) throws IOException;
	
	public List<MTGCard> searchByCriteria(MTGCrit<?>... crits) throws IOException;

	public List<MTGCard>  searchByCriteria(List<MTGCrit> crits) throws IOException;

	public MTGQueryBuilder<?> getMTGQueryManager();

	public MTGCard getCardByArenaId(String id) throws IOException;
}
