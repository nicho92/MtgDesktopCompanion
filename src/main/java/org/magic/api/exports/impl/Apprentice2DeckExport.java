package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class Apprentice2DeckExport extends AbstractFormattedFileCardExport {

	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}

	@Override
	public String getName() {
		return "Apprentice";
	}

	@Override
	public String getFileExtension() {
		return ".deck";
	}

	@Override
	public boolean skipFirstLine() {
		return false;
	}
	
	@Override
	public String getVersion() {
		return "2.0";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();
		for (var mc : deck.getMain().keySet()) {
			temp.append("MD").append(getSeparator());
			temp.append(deck.getMain().get(mc)).append(getSeparator());
			temp.append(formatName(mc)).append(getSeparator());
			temp.append(mc.getEdition().getId()).append(System.lineSeparator());
			notify(mc);
		}
		for (var mc : deck.getSideBoard().keySet()) {
			temp.append("SB").append(getSeparator());
			temp.append(deck.getSideBoard().get(mc)).append(getSeparator());
			temp.append(formatName(mc)).append(getSeparator());
			temp.append(mc.getEdition().getId()).append(System.lineSeparator());
			notify(mc);
		}

		FileTools.saveFile(dest, temp.toString());

	}

	private String formatName(MTGCard mc) {
		if(mc.getName().indexOf(' ')>-1)
			return "\""+mc.getName()+"\"";
		
		return commated(mc.getName());
			
	}

	@Override
	public MTGDeck importDeck(String f,String name) throws IOException {
		var deck = new MTGDeck();
			deck.setName(name);

			
		
			for(var m : matches(f,true))
			{
				var mc = parseMatcherWithGroup(m, 3, 4, true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);
				var qte = Integer.parseInt(m.group(2));
				
				if(mc!=null)
				{
					if(m.group(1)==null || m.group(1).equalsIgnoreCase("MD"))
						deck.getMain().put(mc, qte);
					else
						deck.getSideBoard().put(mc, qte);
			
					notify(mc);
				}

				
			}
			return deck;
	}


	@Override
	public String[] skipLinesStartWith() {
		return new String[] {"//"};
	}
	
	@Override
	public String getSeparator() {
		return ",";
	}

}
