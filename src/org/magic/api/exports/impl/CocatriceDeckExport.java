package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.CardExporter;
import org.magic.gui.DeckBuilderGUI;

public class CocatriceDeckExport implements CardExporter{

	private boolean enable;
	
	@Override
	public void enable(boolean b) {
		this.enable=b;
		
	}
	@Override
	public String getName() {
		return "Cockatrice";
	}
	
	public String getFileExtension()
	{
		return ".cod";
	}
	
	public void export(MagicDeck deck , File dest) throws IOException
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

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isEnable() {
		return enable;
	}
	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(CocatriceDeckExport.class.getResource("/res/cockatrice_logo.png"));
	}
	
}
