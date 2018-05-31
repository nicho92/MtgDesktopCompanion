package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;

public class DeckBoxExport extends AbstractCardExport {

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
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
	
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File dest) throws IOException {
		String columns="Count,Tradelist Count,Name,Edition,Card Number,Condition,Language,Foil,Signed,Artist Proof,Altered Art,Misprint,Promo,Textless,My Price\n";
		FileUtils.write(dest, columns, MTGConstants.DEFAULT_ENCODING,false);
		int val=0;
		for(MagicCardStock mc : stock)
		{
			String name=mc.getMagicCard().getName();
			if(mc.getMagicCard().getName().contains(","))
				name="\""+mc.getMagicCard().getName()+"\"";
			
			StringBuilder line = new StringBuilder();
			line.append(mc.getQte()).append(",");
			line.append(mc.getQte()).append(",");
			line.append(name).append(",");
			line.append(mc.getMagicCard().getCurrentSet().getSet()).append(",");
			line.append(mc.getMagicCard().getCurrentSet().getNumber()).append(",");
			line.append(translate(mc.getCondition())).append(",");
			line.append(mc.getLanguage()).append(",");
			line.append(mc.isFoil()?"foil":"").append(",");
			line.append(mc.isSigned()?"signed":"").append(",");
			line.append(",");
			line.append(mc.isAltered()?"altered":"").append(",");
			line.append(",");
			line.append(",");
			line.append(",");
			line.append(mc.getPrice()).append("\n");
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			setChanged();
			notifyObservers(val++);
		}
	}
	
	
	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		int val=0;
		
		String columns="Count,Tradelist Count,Name,Edition,Card Number,Condition,Language,Foil,Signed,Artist Proof,Altered Art,Misprint,Promo,Textless,My Price\n";
		FileUtils.write(dest, columns, MTGConstants.DEFAULT_ENCODING,false);
		
		for(MagicCard mc : deck.getMap().keySet())
		{
			String name=mc.getName();
			if(mc.getName().contains(","))
				name="\""+mc.getName()+"\"";
			
			StringBuilder line = new StringBuilder();
			line.append(deck.getMap().get(mc)).append(",");
			line.append(deck.getMap().get(mc)).append(",");
			line.append(name).append(",");
			line.append(mc.getCurrentSet().getSet()).append(",");
			line.append(mc.getCurrentSet().getNumber()).append(",");
			//line.append("Near Mint,French,foil,signed,proof,altered,misprint,,,0\n");
			line.append("Near Mint,,,,,,,,,0\n");
			
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			setChanged();
			notifyObservers(val++);
		}

	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "DeckBox";
	}

}
