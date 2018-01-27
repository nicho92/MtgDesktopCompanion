package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;


public class MKMFileWantListExport extends AbstractCardExport {

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	public MKMFileWantListExport() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			save();
		}
	}
	
	@Override
	public MagicDeck importDeck(File f) throws Exception {
		
		try(BufferedReader read = new BufferedReader(new FileReader(f)))
		{
			MagicDeck deck = new MagicDeck();
			deck.setName(f.getName().substring(0,f.getName().indexOf('.')));
			
			String line = read.readLine();
			
			while(line!=null)
			{
				int qte = Integer.parseInt(line.substring(0, line.indexOf(' ')));
				String name = line.substring(line.indexOf(' '),line.indexOf('('));
				
				deck.getMap().put(MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name.trim(), null,true).get(0), qte);
				line=read.readLine();
			}
			return deck;
		}
		
	}

	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		
		try(BufferedWriter bw=new BufferedWriter(new FileWriter(f)))
		{
			for (MagicCard mc : cards){
				StringBuilder temp = new StringBuilder();
				
				temp.append("1").append(" ").append(mc.getName()).append(" (").append(mc.getEditions().get(0).getSet()).append(")");
				bw.write(temp.toString()+"\n");
			}
		}
	}

	@Override
	public String getFileExtension() {
		return ".txt";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		
		try(BufferedWriter bw=new BufferedWriter(new FileWriter(dest)))
		{
			for (MagicCard mc : deck.getMap().keySet()){
				StringBuilder temp = new StringBuilder();
				temp.append(deck.getMap().get(mc)).append(" ").append(mc.getName()).append(" (").append(mc.getEditions().get(0).getSet()).append(")");
				bw.write(temp.toString()+"\n");
			}
			for (MagicCard mc : deck.getMapSideBoard().keySet()){
				StringBuilder temp = new StringBuilder();
				
				if(mc.getEditions().get(0).getMkm_name()!=null)
					temp.append(deck.getMapSideBoard().get(mc)).append(" ").append(mc.getName()).append(" (").append(mc.getEditions().get(0).getMkm_name()).append(")");
				else
					temp.append(deck.getMapSideBoard().get(mc)).append(" ").append(mc.getName()).append(" (").append(mc.getEditions().get(0).getSet()).append(")");
				
				
				bw.write(temp.toString()+"\n");
			}
		}
	}

	@Override
	public String getName() {
		return "MKM File WantList";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MKMFileWantListExport.class.getResource("/icons/mkm.png"));
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
