package org.magic.api.exports.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.tools.FileTools;

public class MagarenaExport extends AbstractFormattedFileCardExport
{

	@Override
	public String getFileExtension() {
		return ".dec";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		var build = new StringBuilder();
		deck.getMain().entrySet().forEach(e->build.append(e.getValue()).append(" ").append(e.getKey()).append("\n"));

		build.append(">").append(deck.getDescription());

		FileTools.saveFile(dest, build.toString());
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		var deck = new MagicDeck();
		deck.setName(name);
		matches(f, true).forEach(m->{

			try {
				MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(m.group(4),null,true).get(0);
				var qty = Integer.parseInt(m.group(3));
				deck.getMain().put(mc, qty);

			} catch (Exception e) {
				logger.error("error getting " + m.group(4));
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
	protected String getStringPattern() {
		return "^([\\>].*)?((\\d+) (.*))?";
	}

	@Override
	protected String getSeparator() {
		return "";
	}

}
