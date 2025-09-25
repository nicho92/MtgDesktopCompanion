package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class MagarenaExport extends AbstractFormattedFileCardExport
{

	@Override
	public String getStockFileExtension() {
		return ".dec";
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		var build = new StringBuilder();
		deck.getMain().entrySet().forEach(e->build.append(e.getValue()).append(" ").append(e.getKey()).append("\n"));

		build.append(">").append(deck.getDescription());

		FileTools.saveFile(dest, build.toString());
	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		var deck = new MTGDeck();
		deck.setName(name);
		matches(f, true).forEach(m->{

			try {
				MTGCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(m.group(4),null,true).get(0);
				var qty = Integer.parseInt(m.group(3));
				deck.getMain().put(mc, qty);

			} catch (Exception _) {
				logger.error("error getting {}",m.group(4));
			}

			if(m.group().startsWith(">"))
				deck.setDescription(m.group());

		});


		return deck;
	}

	@Override
	public String getName() {
		return "Magarena";
	}

	@Override
	protected boolean skipFirstLine() {
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {"#"};
	}

	@Override
	protected String getSeparator() {
		return "";
	}
	
}
