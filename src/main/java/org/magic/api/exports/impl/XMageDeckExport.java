package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.tools.FilesTools;

public class XMageDeckExport extends AbstractFormattedFileCardExport {


	@Override
	public String getName() {
		return "XMage";
	}

	@Override
	public String getFileExtension() {
		return ".dck";
	}
	
	
	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();
		temp.append("NAME: " + deck.getName() + "\n");
		for (MagicCard mc : deck.getMap().keySet()) {
			temp.append(deck.getMap().get(mc)).append(" ").append("[").append(mc.getCurrentSet().getId())
					.append(":").append(mc.getCurrentSet().getNumber()).append("]").append(" ")
					.append(mc.getName()).append("\n");
			notify(mc);
		}
		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			temp.append("SB: ").append(deck.getMapSideBoard().get(mc)).append(" ").append("[")
					.append(mc.getCurrentSet().getId()).append(":").append(mc.getCurrentSet().getNumber())
					.append("]").append(" ").append(mc.getName()).append("\n");
			notify(mc);
		}
		FilesTools.saveFile(dest, temp.toString());
	}
	
	@Override
	public MagicDeck importDeck(String f,String dname) throws IOException {
			MagicDeck deck = new MagicDeck();
			deck.setName(dname);

			matches(f,true).forEach(m->{
			
				String cname = cleanName(m.group(5));
				MagicEdition ed = null;
				try {			   
					ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(m.group(3));
				}
				catch(Exception e)
				{
					logger.error("Edition not found for " + m.group(3));
				}
				
				String number=null;
				try {
					number = m.group(4);
				}
				catch(IndexOutOfBoundsException e)
				{
					//do nothing
				}
				
				MagicCard mc = null;
				if(number!=null && ed !=null)
				{
					try {
						mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getCardByNumber(number, ed);
					} catch (Exception e) {
						logger.error("no card found with number " + number + "/"+ ed);
					}
				}
				
				if(mc==null)
				{
					try {
						mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(cname, ed,true).get(0);
					} catch (Exception e) {
						logger.error("no card found for" + cname + "/"+ ed);
					}
				
				}
				
				
				if(m.group(1)!=null)
				{
					deck.getMapSideBoard().put(mc, Integer.parseInt(m.group(2)));
				}
				else
				{
					deck.getMap().put(mc, Integer.parseInt(m.group(2)));
				}
				
				notify(mc);
			});
			
			return deck;
		

	}

	@Override
	protected boolean skipFirstLine() {
		return false;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {"LAYOUT ","NAME:"};
	}

	@Override
	protected String getStringPattern() {
		return "(SB: )?(\\d+) \\[(.*?):(\\d+)\\] (.*?)$";
	}

	@Override
	protected String getSeparator() {
		return null;
	}

}
