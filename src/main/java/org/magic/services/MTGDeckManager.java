package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicFormat;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.utils.patterns.observer.Observable;

public class MTGDeckManager extends Observable {

	MTGCardsExport serialis;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	public MTGDeckManager(MTGCardsExport sniff) {
		serialis=sniff;
	}
	
	public MTGDeckManager()
	{
		serialis = new MTGDesktopCompanionExport();
	}
	
	public boolean isLegal(MagicDeck magicDeck,String format) {
		MagicFormat mf = new MagicFormat();
				mf.setFormat(format);
		return magicDeck.isCompatibleFormat(mf);
		
	}
	
	
	public MagicDeck getDeck(String name) throws IOException
	{
		File f = new File(MTGConstants.MTG_DECK_DIRECTORY,name+serialis.getFileExtension());
		return serialis.importDeck(f);
	}
	
	
	public List<MagicDeck> listDecks()
	{
		List<MagicDeck> decks= new ArrayList<>();
		for(File f : MTGConstants.MTG_DECK_DIRECTORY.listFiles() )
		{
			try {
				MagicDeck deck = serialis.importDeck(f);
				decks.add(deck);
				setChanged();
				notifyObservers(deck);
			} catch (Exception e) {
				logger.error("error import deck " + f,e);
			}
		}	
		return decks;
	}
	
	public void saveDeck(MagicDeck deck) throws IOException 
	{
		if (!MTGConstants.MTG_DECK_DIRECTORY.exists())
			MTGConstants.MTG_DECK_DIRECTORY.mkdir();

		serialis.export(deck, new File(MTGConstants.MTG_DECK_DIRECTORY,deck.getName() + serialis.getFileExtension()));
		
	}
	
	public void saveDeck(MagicDeck deck, MTGCardsExport exp) throws IOException 
	{
		if (!MTGConstants.MTG_DECK_DIRECTORY.exists())
			MTGConstants.MTG_DECK_DIRECTORY.mkdir();

		exp.export(deck, new File(MTGConstants.MTG_DECK_DIRECTORY,deck.getName() + exp.getFileExtension()));
		
	}
	
	public void remove(MagicDeck selectedDeck) {
		 FileUtils.deleteQuietly(new File(MTGConstants.MTG_DECK_DIRECTORY,selectedDeck.getName()+".deck"));
	
	}
	
	
}
