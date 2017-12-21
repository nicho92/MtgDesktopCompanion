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
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;

public class Apprentice2DeckExport extends AbstractCardExport  {
	
	

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	
	@Override
	public String getName() {
		return "Apprentice";
	}

	@Override
	public String getFileExtension()
	{
		return ".dec";
	}

	public Apprentice2DeckExport() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("VERSION", "2.0");
			props.put("SEPARATOR", ",");
			save();
		}
	}

	@Override
	public void export(MagicDeck deck , File dest) throws IOException
	{
		StringBuffer temp = new StringBuffer();

		for(MagicCard mc : deck.getMap().keySet())
		{
			temp.append("MD,");
			temp.append(deck.getMap().get(mc)+",");
			temp.append("\""+mc.getName()+"\",");
			temp.append(mc.getEditions().get(0).getId());
			temp.append("\n");
		}
		for(MagicCard mc : deck.getMapSideBoard().keySet())
		{
			temp.append("SB,");
			temp.append(deck.getMapSideBoard().get(mc)+",");
			temp.append("\""+mc.getName()+"\",");
			temp.append(mc.getEditions().get(0).getId());
			temp.append("\n");
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
		int ecart=0;
		
		while(line!=null)
		{
			line=line.trim();
			if(!line.startsWith("//"))
			{
				String[] elements = line.split(props.getProperty("SEPARATOR"));
				MagicEdition ed = null;
				try{
				ed = new MagicEdition();
				ed.setId(elements[3]);
				}
				catch(Exception e)
				{
				ed=null;
				ecart=1;
				}
				String name=elements[2-ecart].replaceAll("\"", "");
				MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name ,ed).get(0);
				Integer qte = Integer.parseInt(elements[1-ecart]);
			
				if(line.startsWith("SB"))
					deck.getMapSideBoard().put(mc, qte);
				else
					deck.getMap().put(mc, qte);
				
			}
			line=read.readLine();
		}
		
		read.close();
		return deck;
	}


	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		StringBuffer temp = new StringBuffer();
		for(MagicCard mc : cards)
		{
			temp.append("MD,");
			temp.append("1,");
			temp.append("\""+mc.getName()+"\",");
			temp.append(mc.getEditions().get(0).getId());
			temp.append("\n");
		}
		FileWriter out = new FileWriter(f);
		out.write(temp.toString());
		out.close();

	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(Apprentice2DeckExport.class.getResource("/icons/apprentice.png"));
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws Exception {
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());
		
		for(MagicCardStock mcs : stock)
		{
			d.getMap().put(mcs.getMagicCard(), mcs.getQte());
		}
		
		export(d, f);

		
	}

	@Override
	public List<MagicCardStock> importStock(File f) throws Exception {
		return importFromDeck(importDeck(f));
	}
}
