package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;

public class DelverLensExport extends AbstractFormattedFileCardExport{

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
	}
	
	@Override
	public MagicDeck importDeck(String content, String name) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(name);
		
		for(Matcher m : matches(content,true))
		{
			MagicCard mc = parseMatcherWithGroup(m, 3, 4, true, false);
			Integer qte = Integer.parseInt(m.group(1));
			if(mc!=null) {
				notify(mc);
				deck.getMap().put(mc, qte);
			}
			
		}
		return deck;
	}

	@Override
	public String getName() {
		return "DelverLens";
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
	protected String getStringPattern() {
		return "\"(\\d+)\",\"(\\d+)\",\"(.*?)\",\"(.*?)\",\"(\\d+)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\"";
	}

	@Override
	protected String getSeparator() {
		return getString("SEPARATOR");
	}
	
	@Override
	public void initDefault() {
		setProperty("SEPARATOR", ",");
	}
	

}
