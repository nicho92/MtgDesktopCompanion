package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;

public class MagicWorkStationDeckExport extends AbstractFormattedFileCardExport {


	@Override
	public String getStockFileExtension() {
		return ".mwDeck";
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}
	

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();
		temp.append("// MAIN\n");
		for (MTGCard mc : deck.getMain().keySet()) {
			temp.append("    ");
			temp.append(deck.getMain().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getEdition().getId().toUpperCase()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
			notify(mc);
		}
		temp.append("// Sideboard\n");
		for (MTGCard mc : deck.getSideBoard().keySet()) {
			temp.append("SB: ");
			temp.append(deck.getMain().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getEdition().getId()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
			notify(mc);
		}

		try (var out = new FileWriter(dest)) {
			out.write(temp.toString());
		}

	}

	@Override
	public MTGDeck importDeck(String f,String name) throws IOException {
			var deck = new MTGDeck();
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
						logger.warn("No card found for {}",m.group());
					}

			});

			return deck;
	
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
	protected String getSeparator() {
		return null;
	}
	
}
