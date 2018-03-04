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
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
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
			setProperty("VERSION", "2.0");
			setProperty("SEPARATOR", ",");
			save();
		}
	}

	@Override
	public void export(MagicDeck deck , File dest) throws IOException
	{
		StringBuilder temp = new StringBuilder();
		int c=0;
		for(MagicCard mc : deck.getMap().keySet())
		{
			temp.append("MD,");
			temp.append(deck.getMap().get(mc)+",");
			temp.append("\""+mc.getName()+"\",");
			temp.append(mc.getEditions().get(0).getId());
			temp.append("\n");
			setChanged();
			notifyObservers(c++);
		}
		for(MagicCard mc : deck.getMapSideBoard().keySet())
		{
			temp.append("SB,");
			temp.append(deck.getMapSideBoard().get(mc)+",");
			temp.append("\""+mc.getName()+"\",");
			temp.append(mc.getEditions().get(0).getId());
			temp.append("\n");
			setChanged();
			notifyObservers(c++);
		}

		try(FileWriter out = new FileWriter(dest))
		{
			out.write(temp.toString());
		}


	}

	@Override
	public MagicDeck importDeck(File f) throws IOException{
		try(BufferedReader read = new BufferedReader(new FileReader(f)))
		{
			MagicDeck deck = new MagicDeck();
				deck.setName(f.getName().substring(0,f.getName().indexOf('.')));
				
				String line = read.readLine();
				int ecart=0;
				
				while(line!=null)
				{
					line=line.trim();
					if(!line.startsWith("//"))
					{
						String[] elements = line.split(getProperty("SEPARATOR"));
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
						MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name ,ed,true).get(0);
						Integer qte = Integer.parseInt(elements[1-ecart]);
					
						if(line.startsWith("SB"))
							deck.getMapSideBoard().put(mc, qte);
						else
							deck.getMap().put(mc, qte);
						
					}
					line=read.readLine();
				}
				return deck;
		}	
		
	}


	@Override
	public Icon getIcon() {
		return new ImageIcon(Apprentice2DeckExport.class.getResource("/icons/apprentice.png"));
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());
		
		for(MagicCardStock mcs : stock)
		{
			d.getMap().put(mcs.getMagicCard(), mcs.getQte());
		}
		
		export(d, f);

		
	}

	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		return importFromDeck(importDeck(f));
	}
}
