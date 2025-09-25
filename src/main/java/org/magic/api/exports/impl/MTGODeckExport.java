package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class MTGODeckExport extends AbstractFormattedFileCardExport {


	@Override
	public String getName() {
		return "MTGO";
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}
	
	@Override
	public String getStockFileExtension() {
		return ".dek";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		var temp = new StringBuilder();

		temp.append("//NAME: " + deck.getName() + " from MTGDeskTopCompanion\n");
		temp.append("//MAIN\n");
		for (MTGCard mc : deck.getMain().keySet()) {
			temp.append(deck.getMain().get(mc)).append(" ").append(mc.getName()).append("\n");
			notify(mc);
		}
		temp.append("\n//Sideboard\n");
		for (MTGCard mc : deck.getSideBoard().keySet()) {
			temp.append(deck.getSideBoard().get(mc)).append(" ").append(mc.getName()).append("\n");
			notify(mc);
		}

		FileTools.saveFile(dest, temp.toString());
	}

	@Override
	public MTGDeck importDeck(String f, String deckName) throws IOException {
		var deck = new MTGDeck();
			deck.setName(deckName);
			var side=false;

			for(Matcher m : matches(f,false))
			{
				if(m.group().isEmpty())
				{
					side=true;
				}
				else
				{
					String cname = m.group(2);

					MTGEdition ed = null;
					try {
					if(m.group(4)!=null)
							ed=getEnabledPlugin(MTGCardsProvider.class).getSetById(m.group(4));
					}
					catch(Exception _)
					{
						logger.error("{} isn't a valid set",m.group(4));
					}


					try{
						var mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cname.trim(), ed, true).get(0);
						var qty = Integer.parseInt(m.group(1));

						if(side)
						{
							deck.getSideBoard().put(mc, qty);
						}
						else
						{
							deck.getMain().put(mc, qty);
						}

						notify(mc);
					}
					catch(Exception e)
					{
						logger.error("{} is not found : {}",cname,e.getMessage());
					}
				}
			}
			return deck;


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
	protected String getSeparator() {
		return " ";
	}



}
