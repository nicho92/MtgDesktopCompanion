package org.magic.exports.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.DeckExporter;

public class MTGODeckExport implements DeckExporter {
	
	@Override
	public String getName() {
		return "MTGO";
	}
	
	@Override
	public String getFileExtension()
	{
		return ".dec";
	}
	
	
	@Override
	public void export(MagicDeck deck , File dest) throws IOException
	{
		StringBuffer temp = new StringBuffer();
		
		temp.append("//NAME: "+deck.getName()+" from MTGDeskTopCompanion\n");
		temp.append("\n//MAIN\n");
		for(MagicCard mc : deck.getMap().keySet())
		{
			temp.append(deck.getMap().get(mc)).append(" ").append(mc.getName()).append("\n");
		}
		temp.append("\n//Sideboard\n");
		for(MagicCard mc : deck.getMapSideBoard().keySet())
		{
			temp.append("SB: ").append(deck.getMapSideBoard().get(mc)).append(" ").append(mc.getName()).append("\n");
		}
	
		FileWriter out = new FileWriter(dest);
		out.write(temp.toString());
		out.close();
		
		
	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
