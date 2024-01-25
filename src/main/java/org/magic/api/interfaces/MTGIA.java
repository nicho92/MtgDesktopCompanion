package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;

public interface MTGIA extends MTGPlugin {

	
	public String ask(String prompt) throws IOException;
	public String suggestDeckWith(List<MTGCard> cards) throws IOException;
	public String describe(MTGCard card) throws IOException;
	public String describe(MTGEdition ed) throws IOException;
	public MTGCard generateRandomCard(String description) throws IOException;
	
}
