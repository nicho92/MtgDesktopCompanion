package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class MTGStockExport extends AbstractCardExport {

	@Override
	public String getFileExtension() {
		return ".mtgstock";
	}
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		
		int val=0;
		
		for(MagicCard mc : deck.getMap().keySet())
		{
			String name=mc.getName();
			if(mc.getName().contains("'"))
				name="\""+mc.getName()+"\"";
			
			String line= name+","+mc.getCurrentSet().getId()+"\n";
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			setChanged();
			notifyObservers(val++);
		}
		
	}

	
	@Override
	public MagicDeck importDeck(File f) throws IOException {
		try (BufferedReader read = new BufferedReader(new FileReader(f))) {
			MagicDeck deck = new MagicDeck();
			deck.setName(f.getName().substring(0, f.getName().indexOf('.')));

			String line = read.readLine(); //skip first line
			
			line=read.readLine();
			int val=0;
			while (line != null) {
				line = line.trim();
				try {
				Pattern p = Pattern.compile("\"([^\"]*)\"");
				Matcher m = p.matcher(line);
				String name="";
				String ed="";
				int index=0;
				if(m.find())
				{
					name = m.group(1);
					m.find();
					ed=m.group(1);
					index=m.end();
				}
				
				line = line.substring(index+1);
				int qty = Integer.parseInt(line.substring(0, line.indexOf(',')));
				MagicEdition edition = MTGControler.getInstance().getEnabledCardsProviders().getSetByName(ed);
				MagicCard card = MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(name, edition, true).get(0);
				deck.getMap().put(card, qty);
				setChanged();
				notifyObservers(val++);
				}
				catch(Exception e)
				{
					logger.error("Error parsing " + line,e);
				}
				
				
				line = read.readLine();
			}
			return deck;
		}
	}

	@Override
	public String getName() {
		return "MTGStocks";
	}

	
	
	
}
