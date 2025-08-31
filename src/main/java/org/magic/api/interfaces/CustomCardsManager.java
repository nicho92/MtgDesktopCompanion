package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;

public interface CustomCardsManager extends MTGPlugin {

	List<MTGCard> listCustomsCards(MTGEdition me) throws IOException;
	void saveCustomCard(MTGEdition me, MTGCard mc) throws IOException;
	boolean deleteCustomCard(MTGCard mc) throws IOException;
	public void rebuild(MTGEdition me) throws IOException;
	
	List<MTGEdition> listCustomSets() throws IOException;
	void saveCustomSet(MTGEdition ed, List<MTGCard> cards)throws IOException;
	void saveCustomSet(MTGEdition me) throws IOException;
	void deleteCustomSet(MTGEdition me)throws IOException;
	MTGEdition getCustomSet(String id);

}