package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

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

public class DeckBoxExport extends AbstractFormattedFileCardExport {

	private String columns="Count,Tradelist Count,Name,Edition,Card Number,Condition,Language,Foil,Signed,Artist Proof,Altered Art,Misprint,Promo,Textless,My Price\n";
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	@Override
	public String getFileExtension() {
		return ".deckbox";
	}

	private String translate(EnumCondition condition)
	{
		switch (condition)
		{
		 case LIGHTLY_PLAYED : return "Good (Lightly Played)";
		 case NEAR_MINT : return "Near Mint";
		 case PROXY : return "";
		 default : return condition.name(); 
		}
	}
	
	private EnumCondition reverse(String condition)
	{
		
		
		switch (condition)
		{
		 case "Good (Lightly Played)": return EnumCondition.LIGHTLY_PLAYED;
		 case "Near Mint":  return EnumCondition.NEAR_MINT;
		 case "": return null;
		 default : EnumCondition.valueOf(condition.toUpperCase());
		}
		return EnumCondition.valueOf(condition.toUpperCase());
	}
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File dest) throws IOException {
		FileUtils.write(dest, columns, MTGConstants.DEFAULT_ENCODING,false);
		for(MagicCardStock mc : stock)
		{
			String name=mc.getMagicCard().getName();
			if(mc.getMagicCard().getName().contains(getSeparator()))
				name="\""+mc.getMagicCard().getName()+"\"";
			
			StringBuilder line = new StringBuilder();
			line.append(mc.getQte()).append(getSeparator());
			line.append(mc.getQte()).append(getSeparator());
			line.append(name).append(getSeparator());
			line.append(mc.getMagicCard().getCurrentSet().getSet()).append(getSeparator());
			line.append(mc.getMagicCard().getCurrentSet().getNumber()).append(getSeparator());
			line.append(translate(mc.getCondition())).append(getSeparator());
			line.append(mc.getLanguage()).append(getSeparator());
			line.append(mc.isFoil()?"foil":"").append(getSeparator());
			line.append(mc.isSigned()?"signed":"").append(getSeparator());
			line.append(getSeparator());
			line.append(mc.isAltered()?"altered":"").append(getSeparator());
			line.append(getSeparator());
			line.append(getSeparator());
			line.append(getSeparator());
			line.append(mc.getPrice()).append("\n");
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			notify(mc.getMagicCard());
		}
	}
	
	
	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		FileUtils.write(dest, columns, MTGConstants.DEFAULT_ENCODING,false);
		
		for(MagicCard mc : deck.getMap().keySet())
		{
			String name=mc.getName();
			if(mc.getName().contains(","))
				name="\""+mc.getName()+"\"";
			
			StringBuilder line = new StringBuilder();
			line.append(deck.getMap().get(mc)).append(getSeparator());
			line.append(deck.getMap().get(mc)).append(getSeparator());
			line.append(name).append(getSeparator());
			line.append(mc.getCurrentSet().getSet()).append(getSeparator());
			line.append(mc.getCurrentSet().getNumber()).append(getSeparator());
			line.append("Near Mint,,,,,,,,,0\n");
			
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			notify(mc);
		}

	}
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		
		List<MagicCardStock> list = new ArrayList<>();
	
		matches(content).forEach(m->{
			
			MagicEdition ed = null;
			
			try {			   
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(m.group(4));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for " + m.group(4));
			}
			
			String cname = cleanName(m.group(3));
			
			String number=null;
			try {
				number = m.group(5);
			}
			catch(IndexOutOfBoundsException e)
			{
				//do nothing
			}
			
			MagicCard mc=null;
			
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

			if(mc!=null) {
				MagicCardStock mcs = new MagicCardStock();
					   mcs.setQte(Integer.parseInt(m.group(1)));
					   mcs.setMagicCard(mc);
					   mcs.setCondition(reverse(m.group(6)));
					   
					   if(!m.group(7).isEmpty())
						   mcs.setLanguage(m.group(7));
					   
					   mcs.setFoil(m.group(8)!=null);	
					   mcs.setSigned(m.group(9)!=null);
					   mcs.setAltered(m.group(11)!=null);
					   
					   if(!m.group(15).isEmpty())
						   mcs.setPrice(Double.parseDouble(m.group(15)));
		
			   list.add(mcs);
			}
			else
			{
				logger.error("No cards found for " + cname);
			}
			
			
		});
		
		return list;
	}
	
	@Override
	public MagicDeck importDeck(String content, String name) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(name);
		
		for(Matcher m : matches(content))
		{
			MagicEdition ed = null;
			try {
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(m.group(4));
			} catch (Exception e) {
				ed = null;
				
			}
			String cname = cleanName(m.group(3));
			MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( cname, ed, true).get(0);
			Integer qte = Integer.parseInt(m.group(1));
			
			deck.getMap().put(mc, qte);
			
		}
		return deck;
	}
	
	@Override
	public String getName() {
		return "DeckBox";
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
		return "(\\d+)"+getSeparator()+
			   "(\\d+)"+getSeparator()+
			   "((?=\")\".*?\"|.*?)"+getSeparator()+
			   "(.*?)"+getSeparator()+
			   "(\\d+)?"+getSeparator()+
			   "(.*?)"+getSeparator()+
			   "(.*?)"+getSeparator()+
			   "(foil)?"+getSeparator()+
			   "(signed)?"+getSeparator()+
			   "(proof)?"+getSeparator()+
			   "(altered)?"+getSeparator()+
			   "(misprint)?"+getSeparator()+
			   "(promo)?"+getSeparator()+
			   "(textless)?"+getSeparator()+
			   "(\\d+(\\.\\d{1,2})?)";
	}

	@Override
	public String getSeparator() {
		return ",";
	}

}
