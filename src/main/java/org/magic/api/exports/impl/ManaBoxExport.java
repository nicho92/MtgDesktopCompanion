package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;

public class ManaBoxExport extends AbstractFormattedFileCardExport {

	
	private static final String columns = "Name,Set code,Set name,Collector number,Foil,Rarity,Quantity,ManaBox ID,Scryfall ID,Purchase price,Misprint,Altered,Condition,Language,Purchase price currency";
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "Manabox";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[0];
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

}
