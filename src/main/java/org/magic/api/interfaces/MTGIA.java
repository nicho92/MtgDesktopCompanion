package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;

public interface MTGIA extends MTGPlugin {

	public MTGCard generateRandomCard(String description, MTGEdition mtgEdition, String number) throws IOException;
	public List<MTGCard> generateSet(String description, MTGEdition mtgEdition, int qty) throws IOException;
	public MTGDeck generateDeck(String description)throws IOException;
}
