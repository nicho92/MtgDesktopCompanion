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
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.DeckBuilderGUI;
import org.magic.services.MagicFactory;

public class MTGODeckExport extends AbstractCardExport  {
	
	@Override
	public String getName() {
		return "MTGO";
	}

	@Override
	public String getFileExtension()
	{
		return ".dec";
	}

	public MTGODeckExport() {
		super();
		if(!new File(MagicFactory.CONF_DIR, "exp-"+getName()+".conf").exists()){
			props.put("VERSION", "1.0");
			save();
		}
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


	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		StringBuffer temp = new StringBuffer();
		temp.append("\n//MAIN\n");
		for(MagicCard mc : cards)
		{
			temp.append(1).append(" ").append(mc.getName()).append("\n");
		}

		FileWriter out = new FileWriter(f);
		out.write(temp.toString());
		out.close();

	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGODeckExport.class.getResource("/res/mtgo.png"));
	}
}
