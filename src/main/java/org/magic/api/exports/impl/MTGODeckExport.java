package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;

public class MTGODeckExport extends AbstractCardExport  {
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
	
	@Override
	public String getName() {
		return "MTGO";
	}

	@Override
	public String getFileExtension()
	{
		return ".dek";
	}

	public MTGODeckExport() {
		super();
	
	}

	@Override
	public void export(MagicDeck deck , File dest) throws IOException
	{
		StringBuilder temp = new StringBuilder();

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

		FileUtils.writeStringToFile(dest, temp.toString(),"UTF-8");
	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		try(BufferedReader read = new BufferedReader(new FileReader(f)))
		{
			MagicDeck deck = new MagicDeck();
			deck.setName(f.getName().substring(0,f.getName().indexOf('.')));
			
			String line = read.readLine();
			
			while(line!=null)
			{
				if(!line.startsWith("//") && line.length()>0)
				{
					int sep = line.indexOf(' ');
					String name = line.substring(sep, line.length()).trim();
					String qte =  line.substring(0, sep).trim();
				
					if(line.startsWith("SB: "))
					{
						line=line.replaceAll("SB: ", "");
						sep = line.indexOf(' ');
						name = line.substring(sep, line.length()).trim();
						qte =  line.substring(0, sep).trim();
						List<MagicCard> list = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name,null,true);
						deck.getMapSideBoard().put(list.get(0),Integer.parseInt(qte));
					}
					else
					{
						List<MagicCard> list = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name,null,true);
						deck.getMap().put(list.get(0),Integer.parseInt(qte));
					}
				}
				line=read.readLine();
				
			}
			return deck;
		}
		
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGODeckExport.class.getResource("/icons/mtgo.png"));
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



	@Override
	public void initDefault() {
		setProperty("VERSION", "1.0");
		
	}
}
