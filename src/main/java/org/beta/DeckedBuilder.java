package org.beta;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;

public class DeckedBuilder extends AbstractFormattedFileCardExport {

	
	private final String columns ="Total Qty,Reg Qty,Foil Qty,Card,Set,Mana Cost,Card Type,Color,Rarity,Mvid,Single Price,Single Foil Price,Total Price,Price Source,Notes";
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "DeckedBuilder";
	}
	
	public static void main(String[] args) throws IOException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		new DeckedBuilder().importDeckFromFile(new File("D:\\Téléchargements\\ELD.csv"));
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicDeck importDeck(String f, String dname) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(dname);
		
		matches(f,true).forEach(m->{
			
			
			System.out.println(m.group());
			
			Integer qty = Integer.parseInt(m.group(1));
			MagicCard mc = parseMatcherWithGroup(m, 10, 5, true, FORMAT_SEARCH.NAME,FORMAT_SEARCH.MULTIVID);
			
			if(mc==null)
			{
				logger.warn("MULTIVID not found, trying with cardname");
				mc = parseMatcherWithGroup(m, 4, 5, true, FORMAT_SEARCH.NAME,FORMAT_SEARCH.NAME);
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
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[0];
	}

	@Override
	protected String getStringPattern() {
		return "(\\d+),(\\d+),(\\d+),((?=\\\")\\\".*?\\\"|.*?),(.*?),(.*?),(.*?),(.*?),(.*?),(\\d+),(\\d+.\\d+?),(\\d+.\\d+?),(\\d+.\\d+?)?,(.*?),";
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

}
