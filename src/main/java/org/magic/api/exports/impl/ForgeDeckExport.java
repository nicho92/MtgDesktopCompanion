package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.tools.FileTools;

public class ForgeDeckExport extends AbstractFormattedFileCardExport {

	@Override
	public String getFileExtension() {
		return ".dck";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();
		
		temp.append("[metadata]\n");
		temp.append("Name=").append(deck.getName()).append("\n");
		temp.append("[Avatar]\n\n");
		temp.append("[Main]\n");
		deck.getMain().entrySet().forEach(e->temp.append(e.getValue()).append(" ").append(e.getKey().getName()).append("|").append(e.getKey().getCurrentSet().getId().toUpperCase()).append("|1\n"));
	
		temp.append("[Sideboard]\n");
		deck.getSideBoard().entrySet().forEach(e->temp.append(e.getValue()).append(" ").append(e.getKey().getName()).append("|").append(e.getKey().getCurrentSet().getId().toUpperCase()).append("|1\n"));
		
		temp.append("[Planes]\n\n");
		temp.append("[Schemes]\n\n");
		temp.append("[Conspiracy]\n\n");
		
		
		FileTools.saveFile(dest, temp.toString());

	}
	
	public static void main(String[] args) throws IOException {
		new ForgeDeckExport().importDeckFromFile(new File("D:\\Desktop\\test.forge.dck"));
	}

	

	@Override
	public MagicDeck importDeck(String content, String name) throws IOException {
		MagicDeck d = new MagicDeck();
				  d.setName(name);
		
		boolean side=false;
		for(Matcher m : matches(content, true))
		{
			if(m.group().equalsIgnoreCase("[Sideboard]"))
			{
				side=true;
			}
			
			if(m.groupCount()>1)
			{
				try {
				MagicCard mc = parseMatcherWithGroup(m, 3, 4, true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);
				int qty = Integer.parseInt(m.group(2));
				
					if(mc!=null)
					{
						if(side)
							d.getSideBoard().put(mc, qty);
						else
							d.getMain().put(mc, qty);
						
						
						notify(mc);
						
					}
				}
				catch(NullPointerException e)
				{
					//do nothing
				}
				
			}
		}
		return d;
	}

	@Override
	public String getName() {
		return "Forge";
	}

	@Override
	protected boolean skipFirstLine() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getStringPattern() {
		return "\\[(\\w+)\\]?|(\\d+) (.*?)\\|(\\w+)\\|?(\\d+)?";
	}

	@Override
	protected String getSeparator() {
		return null;
	}


}
