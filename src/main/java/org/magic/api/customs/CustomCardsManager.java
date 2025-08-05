package org.magic.api.customs;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;

public interface CustomCardsManager  {




	List<MTGCard> listCards(MTGEdition me) throws IOException;
	void addCard(MTGEdition me, MTGCard mc) throws IOException;
	boolean removeCard(MTGEdition me, MTGCard mc) throws IOException;
	public void rebuild(MTGEdition me) throws IOException;
	
	List<MTGEdition> loadEditions() throws IOException;
	void saveEdition(MTGEdition ed, List<MTGCard> cards);
	void saveEdition(MTGEdition me) throws IOException;
	void removeEdition(MTGEdition me);
	MTGEdition getSetById(String id);

}