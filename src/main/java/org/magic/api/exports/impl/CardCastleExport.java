package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

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
		FileUtils.write(dest, build.toString(),MTGConstants.DEFAULT_ENCODING);
	}
	
	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(name);

		
		matches(f,true).forEach(m->{
			
			String cname = cleanName(m.group(1));
			MagicEdition ed = null;
			try {			   
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(m.group(2));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for " + m.group(4));
			}
			
			
			MagicCard mc = null;
			try 
			{
				mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(cname, ed,true).get(0);
			} catch (Exception e) {
				logger.error("no card found for" + cname + "/"+ ed);
			}
			
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
