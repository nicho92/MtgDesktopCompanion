package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.tools.FileTools;

public class CardCastleExport extends AbstractFormattedFileCardExport {

	private String header="Count"+getSeparator()+"Card Name"+getSeparator()+"Set Name"+getSeparator()+"Foil";


	@Override
	public String getFileExtension() {
		return ".csv";
	}
	
	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		StringBuilder build = new StringBuilder();
		build.append(header).append("\n");
		
		deck.getMap().entrySet().forEach(entry->{
			
			String name = entry.getKey().getName();
			
			if(name.contains(","))
				name="\""+name+"\"";
			
			
			build.append(entry.getValue()).append(getSeparator());
			build.append(name).append(getSeparator());
			build.append(entry.getKey().getCurrentSet().getSet()).append(getSeparator());
			build.append("false").append("\n");
			notify(entry.getKey());
		});
		FileTools.saveFile(dest, build.toString());
	}
	
	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(name);

		matches(f,true).forEach(m->{
			MagicCard mc = parseMatcherWithGroup(m, 1, 2, true, false);
			if(mc!=null)
			{
				deck.add(mc);
				notify(mc);
			}
		});
		return deck;
	}

	@Override
	public String getName() {
		return "CardCastle";
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
		return "((?=\")\".*?\"|.*?)"+getSeparator()+
			   "(.*?)"+getSeparator()+
			   "(.*?)"+getSeparator()+
			   "(true|false)"+getSeparator()+
			   "(.*?)"+getSeparator()+
			   "(\\d+)"+getSeparator()+
			   "(.*?)"+getSeparator()+
			   "(\\d+.\\d+?)";
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

	
	

}
