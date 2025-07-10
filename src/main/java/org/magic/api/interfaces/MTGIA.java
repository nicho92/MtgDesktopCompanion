package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;

public interface MTGIA extends MTGPlugin {
	

	public String suggestDeckWith(List<MTGCard> cards) throws IOException;
	public MTGCard generateRandomCard(String description, MTGEdition mtgEdition, String number) throws IOException;
	
}
