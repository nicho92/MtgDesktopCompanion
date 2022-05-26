package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.tools.FileTools;

public class Apprentice2DeckExport extends AbstractFormattedFileCardExport {


	@Override
	public String getName() {
		return "Apprentice";
	}

	@Override
	public String getFileExtension() {
		return ".dec";
	}

	@Override
	public boolean skipFirstLine() {
		return false;
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();
		for (var mc : deck.getMain().keySet()) {
			temp.append("MD,");
			temp.append(deck.getMain().get(mc) + getSeparator());
			temp.append("\"" + mc.getName() + "\",");
			temp.append(mc.getCurrentSet().getId());
			temp.append("\n");
			notify(mc);
		}
		for (var mc : deck.getSideBoard().keySet()) {
			temp.append("SB,");
			temp.append(deck.getSideBoard().get(mc) + getSeparator());
			temp.append("\"" + mc.getName() + "\",");
			temp.append(mc.getCurrentSet().getId());
			temp.append(System.lineSeparator());
			notify(mc);
		}
		
		FileTools.saveFile(dest, temp.toString());
		
	}
	
	@Override
	public MagicDeck importDeck(String f,String name) throws IOException {
		var deck = new MagicDeck();
			deck.setName(name);
			
			
			for(Matcher m : matches(f,true))
			{
				var mc = parseMatcherWithGroup(m, 3, 4, true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);
				var qte = Integer.parseInt(m.group(2));
				
				if(mc!=null) {
					if (m.group(1).startsWith("SB"))
						deck.getSideBoard().put(mc, qte);
					else
						deck.getMain().put(mc, qte);
				}
			}
			return deck;
	}


	@Override
	public String[] skipLinesStartWith() {
		return new String[] {"//"};
	}

	@Override
	public String getStringPattern() {
		return "(MD|SB)"+getSeparator()+"(\\d+)"+getSeparator()+"(\"[^\"]*\")"+getSeparator()+"{0,1}([^\"]*){0,1}$";
	}

	@Override
	public String getSeparator() {
		return getString("SEPARATOR");
	}

}
