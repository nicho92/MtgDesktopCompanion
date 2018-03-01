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

public class MagicWorkStationDeckExport extends AbstractCardExport {

	
	public MagicWorkStationDeckExport() {
		super();
		

		if(!new File(confdir,  getName()+".conf").exists()){
			save();
		}
	}
	
	
	@Override
	public String getFileExtension() {
		return ".mwDeck";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();
		temp.append("// MAIN\n");
		for(MagicCard mc : deck.getMap().keySet())
		{
			temp.append("    ");
			temp.append(deck.getMap().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getEditions().get(0).getId().toUpperCase()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
		}
		temp.append("// Sideboard\n");
		for(MagicCard mc : deck.getMapSideBoard().keySet())
		{
			temp.append("SB: ");
			temp.append(deck.getMap().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getEditions().get(0).getId()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
		}

		try(FileWriter out = new FileWriter(dest))
		{
			out.write(temp.toString());
		}

	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
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
						int qte=0;
						MagicCard mc = null;
						//TODO line parse
						
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
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(CSVExport.class.getResource("/icons/mtgworkstation.png"));
	}

	@Override
	public String getName() {
		return "MagicWorkStation";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

}
