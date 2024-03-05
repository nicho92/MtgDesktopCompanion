package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class ForgeDeckExport extends AbstractFormattedFileCardExport {

	@Override
	public String getFileExtension() {
		return ".dck";
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();

		temp.append("[metadata]\n");
		temp.append("Name=").append(deck.getName()).append("\n");

		if(deck.getCommander()!=null)
		{
			temp.append("[Commander]\n");
			temp.append("1 ").append(deck.getCommander().getName()).append("|").append(deck.getCommander().getEdition().getId().toUpperCase()).append("\n");
		}


		temp.append("[Main]\n");
		deck.getMain().entrySet().stream().filter(mc->mc.getKey()!=deck.getCommander()).sorted((e1,e2)->e1.getKey().getName().compareTo(e2.getKey().getName())).forEach(e->temp.append(e.getValue()).append(" ").append(e.getKey().getName()).append("|").append(e.getKey().getEdition().getId().toUpperCase()).append("|1\n"));

		temp.append("[Sideboard]\n");
		deck.getSideBoard().entrySet().stream().sorted((e1,e2)->e1.getKey().getName().compareTo(e2.getKey().getName())).forEach(e->temp.append(e.getValue()).append(" ").append(e.getKey().getName()).append("|").append(e.getKey().getEdition().getId().toUpperCase()).append("|1\n"));


		FileTools.saveFile(dest, temp.toString());

	}

	@Override
	public MTGDeck importDeck(String content, String name) throws IOException {
		var d = new MTGDeck();
				  d.setName(name);

				  var deckNameTag ="Name=";
		for(String s : splitLines(content,true))
		{
			if(s.startsWith(deckNameTag))
			{
				d.setName(s.substring(s.indexOf(deckNameTag)+deckNameTag.length()));
				break;
			}
		}



		var side=false;
		var commander = false;
		for(Matcher m : matches(content, true))
		{
			if(m.group().equalsIgnoreCase("[Sideboard]"))
			{
				side=true;
			}
			if(m.group().equalsIgnoreCase("[Commander]"))
			{
				commander=true;
			}


			if(m.groupCount()>1)
			{
				try {
				MTGCard mc = parseMatcherWithGroup(m, 3, 4, true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);
				var qty = Integer.parseInt(m.group(2));

					if(mc!=null)
					{
						if(side)
							d.getSideBoard().put(mc, qty);
						else
							d.getMain().put(mc, qty);

						if(commander)
						{
							d.setCommander(mc);
							commander=false;
						}


						notify(mc);

					}
					else
					{
						logger.error("no card for {}",m!=null?m.group():"");
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
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[0];
	}

	@Override
	protected String getSeparator() {
		return null;
	}

}
