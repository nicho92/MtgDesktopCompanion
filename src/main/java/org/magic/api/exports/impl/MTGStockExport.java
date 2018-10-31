package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
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
		for(MagicCard mc : deck.getMap().keySet())
		{
			String name=mc.getName();
			if(mc.getName().contains("'"))
				name="\""+mc.getName()+"\"";
			
			String line= name+","+mc.getCurrentSet().getId()+"\n";
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			notify(mc);
		}
		
	}

	
	@Override
	public MagicDeck importDeck(String f,String dname) throws IOException {
		try (BufferedReader read = new BufferedReader(new StringReader(f))) {
			MagicDeck deck = new MagicDeck();
			deck.setName(dname);

			String line = read.readLine(); //skip first line
			
			line=read.readLine();
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
				MagicEdition edition = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(ed);
				MagicCard card = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(name, edition, true).get(0);
				deck.getMap().put(card, qty);
				notify(card);
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
