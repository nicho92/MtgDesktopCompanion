package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;

public class MagicWorkStationDeckExport extends AbstractFormattedFileCardExport {

	
	@Override
	public String getFileExtension() {
		return ".mwDeck";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();
		temp.append("// MAIN\n");
		for (MagicCard mc : deck.getMain().keySet()) {
			temp.append("    ");
			temp.append(deck.getMain().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getCurrentSet().getId().toUpperCase()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
			notify(mc);
		}
		temp.append("// Sideboard\n");
		for (MagicCard mc : deck.getSideBoard().keySet()) {
			temp.append("SB: ");
			temp.append(deck.getMain().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getCurrentSet().getId()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
			notify(mc);
		}

		try (var out = new FileWriter(dest)) {
			out.write(temp.toString());
		}

	}
	
	@Override
	public MagicDeck importDeck(String f,String name) throws IOException {
		try (var read = new BufferedReader(new StringReader(f))) {
			var deck = new MagicDeck();
			deck.setName(name);
			matches(f,true).forEach(m->{
				var mc = parseMatcherWithGroup(m, 4, 3, true, FORMAT_SEARCH.ID,FORMAT_SEARCH.NAME); 
				var qte = Integer.parseInt(m.group(2));
					if(mc!=null) 
					{
						if(m.group(1)!=null && m.group(1).trim().startsWith("SB"))
							deck.getSideBoard().put(mc, qte);
						else
							deck.getMain().put(mc, qte);
						
						notify(mc);
					}
					else
					{
						logger.warn("No card found for " + m.group());
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
	protected boolean skipFirstLine() {
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {"//","<br>"};
	}

	@Override
	protected String getStringPattern() {
		return "(SB:\\s+|\\s)?(\\d+)(?: )?\\[(.*?)\\](.*)";
	}

	@Override
	protected String getSeparator() {
		return null;
	}


}
