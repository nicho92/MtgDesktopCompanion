package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class MTGStockExport extends AbstractFormattedFileCardExport {

	
	private String columns="\"Card\",\"Set\",\"Quantity\",\"Price\",\"Condition\",\"Language\",\"Foil\",\"Signed\"";
	
	@Override
	public String getFileExtension() {
		return ".mtgstock";
	}
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	


	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		for(MagicCard mc : deck.getMap().keySet())
		{
			String name=mc.getName();
			if(mc.getName().contains("'"))
				name="\""+mc.getName()+"\"";
			
			String line= deck.getMap().get(mc) + " " + name+","+mc.getCurrentSet().getId()+"\n";
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			notify(mc);
		}
		
	}
	
	private EnumCondition reverse(String condition)
	{
		switch (condition)
		{
		 case "M": return EnumCondition.MINT;
		 case "NM": return EnumCondition.NEAR_MINT;
		 case "EXC": return EnumCondition.NEAR_MINT;
		 case "GD": return EnumCondition.LIGHTLY_PLAYED;
		 case "FIN": return EnumCondition.PLAYED;
		 case "PR": return EnumCondition.POOR;
		 case "": return null;
		 default : EnumCondition.valueOf(condition.toUpperCase());
		}
		return EnumCondition.valueOf(condition.toUpperCase());
	}
	
	
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		
		List<MagicCardStock> ret = new ArrayList<>();
		
		matches(content).forEach(m->{
			String cname = cleanName(m.group(1));
			MagicEdition ed = null;
			try {			   
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(m.group(2));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for " + m.group(2));
			}

			MagicCard card=null;
			try {
				card = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(cname, ed, true).get(0);
			} catch (IOException e) {
				logger.error("no card found for" + cname + "/"+ ed);
			}
			

			if(card!=null)
			{
				Integer qty = Integer.parseInt(m.group(3));
				MagicCardStock st = MTGControler.getInstance().getDefaultStock();
				st.setMagicCard(card);
				st.setQte(qty);
				st.setPrice(Double.parseDouble(m.group(4)));
				st.setLanguage(m.group(6));
				st.setFoil(m.group(7).equalsIgnoreCase("yes"));
				st.setSigned(m.group(8).equalsIgnoreCase("yes"));
				st.setCondition(reverse(m.group(5)));
				ret.add(st);
				notify(card);
			}
		});
		
		return ret;
	}
	
	
	@Override
	public MagicDeck importDeck(String f,String dname) throws IOException 
	{
		
		MagicDeck deck = new MagicDeck();
		deck.setName(dname);
		
		matches(f).forEach(m->{
			String cname = cleanName(m.group(1));
			MagicEdition ed = null;
			try {			   
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(m.group(2));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for " + m.group(2));
			}

			MagicCard card=null;
			try {
				card = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(cname, ed, true).get(0);
			} catch (IOException e) {
				logger.error("no card found for" + cname + "/"+ ed);
			}
			
			
			if(card!=null)
			{
				Integer qty = Integer.parseInt(m.group(3));
				deck.getMap().put(card, qty);
				notify(card);
			}
			
		});
		return deck;
		
	}

	@Override
	public String getName() {
		return "MTGStocks";
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
		return "\"(.*?)\",\\\"(.*?)\\\",(\\d+),(\\d+.\\d+?),(M|NM|EXC|GD|FIN|PR),(.*?),(Yes|No),(Yes|No)";
	}


	@Override
	protected String getSeparator() {
		return ",";
	}

	
	
	
}
