package org.beta;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;

public class TCGPlayerExport extends AbstractFormattedFileCardExport {

	
	private final String columns="Quantity,Name,Simple Name,Set,Card Number,Set Code,External ID,Printing,Condition,Language,Rarity,Product ID,SKU,Price,Price Each";
	
	
	@Override
	public String getFileExtension() {
		return ".csv";
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
	public String getName() {
		return "TCGPlayer";
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
	protected String getStringPattern() {
		return "(\\d+),((?=\\\")\\\".*?\\\"|.*?),((?=\\\")\\\".*?\\\"|.*?),(.*?),(.*?),(.*?),(\\d+),(.*?),(.*?),(.*?),(.*?),(\\d+),(\\d+),\\$?\\d+(,\\d{3})*\\.?[0-9]?[0-9]?,\\$?\\d+(,\\d{3})*\\.?[0-9]?[0-9]?";
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

	public static void main(String[] args) {
		System.out.print(new TCGPlayerExport().getStringPattern());

	}

}
