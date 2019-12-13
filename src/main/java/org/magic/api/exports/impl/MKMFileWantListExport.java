package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class MKMFileWantListExport extends AbstractFormattedFileCardExport {

	
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		new MKMFileWantListExport().importDeck(new File("D:\\Desktop\\export.txt"));
	}

	@Override
	public MagicDeck importDeck(String f,String dname) throws IOException {

		MagicDeck deck = new MagicDeck();
		deck.setName(dname);
		
		matches(f).forEach(m->{
			
			
			Integer qty = Integer.parseInt(m.group(1));
			String cname = m.group(2);
			
			MagicEdition ed = null;
			try {			   
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(m.group(3));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for " + m.group(3));
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
				deck.getMap().put(mc, qty);
				notify(mc);
			}
		});
		return deck;

	}

	@Override
	public String getFileExtension() {
		return ".txt";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {

			StringBuilder temp = new StringBuilder();
			
			for (MagicCard mc : deck.getMap().keySet()) {
				temp.append(deck.getMap().get(mc)).append(" ").append(mc.getName()).append(" (").append(mc.getCurrentSet().getSet()).append(")\n");
				notify(mc);
			}
			
			for (MagicCard mc : deck.getMapSideBoard().keySet()) 
			{
				if (mc.getCurrentSet().getMkmName() != null)
					temp.append(deck.getMapSideBoard().get(mc)).append(getSeparator()).append(mc.getName()).append(getSeparator()).append("(").append(mc.getCurrentSet().getMkmName()).append(")\n");
				else
					temp.append(deck.getMapSideBoard().get(mc)).append(getSeparator()).append(mc.getName()).append(getSeparator()).append("(").append(mc.getCurrentSet().getSet()).append(")\n");
				notify(mc);
			}
			FileUtils.write(dest, temp.toString(),MTGConstants.DEFAULT_ENCODING);
		
	}

	@Override
	public String getName() {
		return "MKM File WantList";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MKMFileWantListExport.class.getResource("/icons/plugins/magiccardmarket.png"));
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
		return "(\\d+)"+getSeparator()+"(.*?)"+getSeparator()+"\\((.*?)\\)";
	}

	@Override
	protected String getSeparator() {
		return " ";
	}

}
