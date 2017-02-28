package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;

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
		if(!new File(confdir, getName()+".conf").exists()){
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
	public MagicDeck importDeck(File f) throws Exception {
		BufferedReader read = new BufferedReader(new FileReader(f));
		MagicDeck deck = new MagicDeck();
		deck.setName(f.getName().substring(0,f.getName().indexOf(".")));
		
		String line = read.readLine();
		
		while(line!=null)
		{
			if(!line.startsWith("//") && line.length()>0)
			{
				int sep = line.indexOf(" ");
				String name = line.substring(sep, line.length()).trim();
				String qte =  line.substring(0, sep).trim();
			
				if(line.startsWith("SB: "))
				{
					line=line.replaceAll("SB: ", "");
					sep = line.indexOf(" ");
					name = line.substring(sep, line.length()).trim();
					qte =  line.substring(0, sep).trim();
					List<MagicCard> list = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name,null);
					deck.getMapSideBoard().put(list.get(0),Integer.parseInt(qte));
				}
				else
				{
					List<MagicCard> list = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name,null);
					deck.getMap().put(list.get(0),Integer.parseInt(qte));
				}
			}
			line=read.readLine();
			
		}
		
		read.close();
		return deck;
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
