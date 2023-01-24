package org.magic.api.interfaces;

import java.io.IOException;

import org.magic.api.beans.MagicDeck;

public interface MTGIA extends MTGPlugin {

	
	public String ask(String prompt) throws IOException;
	public void suggestDeckWith(MagicDeck cards) throws IOException;
	
	
}
