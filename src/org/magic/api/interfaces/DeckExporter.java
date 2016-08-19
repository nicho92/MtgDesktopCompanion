package org.magic.api.interfaces;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicDeck;

public interface DeckExporter {

	public String getFileExtension();

	public void export(MagicDeck deck, File dest) throws IOException;
	
	public MagicDeck importDeck(File f) throws Exception;
	
	public String getName();

}