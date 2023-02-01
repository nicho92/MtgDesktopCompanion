package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGIA extends MTGPlugin {

	
	public String ask(String prompt) throws IOException;
	public String suggestDeckWith(List<MagicCard> cards) throws IOException;
	public String describe(MagicCard card) throws IOException;
	public String describe(MagicEdition ed) throws IOException;
	
	
}
