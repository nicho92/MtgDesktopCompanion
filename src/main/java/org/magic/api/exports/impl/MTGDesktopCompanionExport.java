package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

public class MTGDesktopCompanionExport extends AbstractCardExport  {

	
	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
	@Override
	public String getName() {
		return "MTGDesktopCompanion";
	}
	
	public MTGDesktopCompanionExport() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("VERSION", "1.0");
			save();
		}
	}
	
	public void export(MagicDeck deck, File name) throws IOException
	{
		deck.setDateUpdate(new Date());
		if(deck.getDateCreation()==null)
			deck.setDateCreation(new Date());
		
		deck.setDateUpdate(new Date());
		
		FileOutputStream fos = new FileOutputStream(name);
		try(ObjectOutputStream oos = new ObjectOutputStream(fos))
		{
			oos.writeObject(deck);
			oos.flush();
			
		}
	}

	private <T> T read(File f) throws ClassNotFoundException, IOException  {
		try(ObjectInputStream oos = new ObjectInputStream(new FileInputStream(f)))
		{
			return (T)oos.readObject();
		}
	}

	@Override
	public String getFileExtension() {
		return ".deck";
	}

	@Override
	public MagicDeck importDeck(File f) throws Exception {
		return read(f);
	}


	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {

		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f)))
		{
			MagicDeck deck = new MagicDeck();
			deck.setName("Search");
			deck.setDescription("Result of search");
			
			int i=0;
			for(MagicCard mc : cards)
			{
				deck.getMap().put(mc, 1);
				setChanged();
				notifyObservers(i++);
				
			}
			oos.writeObject(deck);
			oos.flush();
		}
	}


	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGDesktopCompanionExport.class.getResource("/icons/logo.gif"));
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
