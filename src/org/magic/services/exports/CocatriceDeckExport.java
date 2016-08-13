package org.magic.services.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;

public class CocatriceDeckExport {

	
	public static void export(MagicDeck deck , File dest) throws IOException
	{
		StringBuffer temp = new StringBuffer();
		
		temp.append("<?xml version='1.0' encoding='UTF-8'?>");
		temp.append("<cockatrice_deck version='1'>");
		temp.append("<deckname>").append(deck.getName()).append("</deckname>");
		temp.append("<comments>").append(deck.getDescription()).append("</comments>");
		temp.append("<zone name='main'>");
		for(MagicCard mc : deck.getMap().keySet())
		{
			temp.append("<card number='").append(deck.getMap().get(mc)).append("' price='0' name=\"").append(mc.getName()).append("\"/>");
		}
		temp.append("</zone>");
		temp.append("<zone name='side'>");
		for(MagicCard mc : deck.getMapSideBoard().keySet())
		{
			temp.append("<card number='").append(deck.getMapSideBoard().get(mc)).append("' price='0' name=\"").append(mc.getName()).append("\"/>");
		}
		temp.append("</zone>");
		
		
		
		temp.append("</cockatrice_deck>");
		
		FileWriter out = new FileWriter(dest);
		out.write(temp.toString());
		out.close();
		
		
	}
}
