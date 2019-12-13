package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;

public class MagicWorkStationDeckExport extends AbstractFormattedFileCardExport {

	
	@Override
	public String getVersion() {
		return "2.0";
	}
	
	@Override
	public String getFileExtension() {
		return ".mwDeck";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();
		temp.append("// MAIN\n");
		for (MagicCard mc : deck.getMap().keySet()) {
			temp.append("    ");
			temp.append(deck.getMap().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getCurrentSet().getId().toUpperCase()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
			notify(mc);
		}
		temp.append("// Sideboard\n");
		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			temp.append("SB: ");
			temp.append(deck.getMap().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getCurrentSet().getId()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
			notify(mc);
		}

		try (FileWriter out = new FileWriter(dest)) {
			out.write(temp.toString());
		}

	}
	
	@Override
	public MagicDeck importDeck(String f,String name) throws IOException {
		try (BufferedReader read = new BufferedReader(new StringReader(f))) {
			MagicDeck deck = new MagicDeck();
			deck.setName(name);
			matches(f).forEach(m->{
				
				MagicEdition ed = null;
				try {			   
					ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(m.group(3));
				}
				catch(Exception e)
				{
					logger.error("Edition not found for " + m.group(3));
				}
				
				String cname=cleanName(m.group(4));
				
				try {
					MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(cname, ed, true).get(0);
					int qte = Integer.parseInt(m.group("2"));
					
					if(m.group(1).trim().startsWith("SB"))
					{
						deck.getMapSideBoard().put(mc, qte);
					}
					else
					{
						deck.getMap().put(mc, qte);
					}
					
					notify(mc);
					
				} catch (Exception e) {
					logger.error("no card found for " + cname);
				}
			});
			
			return deck;
		}
	}



	@Override
	public String getName() {
		return "MagicWorkStation";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}

	@Override
	public void initDefault() {
		// nothing to do

	}

	@Override
	protected boolean skipFirstLine() {
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {"//"};
	}

	@Override
	protected String getStringPattern() {
		return "(SB: |\\s)?(\\d+)(?: )?\\[(.*?)\\](.*)";
	}

	@Override
	protected String getSeparator() {
		return null;
	}


}
