package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class ManaBoxExport extends AbstractFormattedFileCardExport {

	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "Manabox";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected String getStringPattern()
	{
		return aliases.getRegexFor(this, "stock");
	}
	
	
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		StringBuilder tmp = new StringBuilder();
						    tmp.append("Name,Set code,Set name,Collector number,Foil,Rarity,Quantity,ManaBox ID,Scryfall ID,Purchase price,Misprint,Altered,Condition,Language,Purchase price currency").append(System.lineSeparator());
						    
						    for(var mcs : stock)
						    {
						    	
						    	if(mcs.getProduct().isDoubleFaced())
						    	{
						    		tmp.append(mcs.getProduct().getName()).append("//").append(mcs.getProduct().getRotatedCard().getName()).append(getSeparator());
						    	}
						    	else
						    	{
						    		tmp.append(mcs.getProduct().getName()).append(getSeparator());
						    	}
						    	
						    	
						    	tmp.append(mcs.getProduct().getCurrentSet().getId()).append(getSeparator());
						    	tmp.append(mcs.getProduct().getCurrentSet().getSet()).append(getSeparator());
						    	tmp.append(mcs.getProduct().getCurrentSet().getNumber()).append(getSeparator());
						    	tmp.append(mcs.isFoil()?"foil":"normal").append(getSeparator());
						    	tmp.append(mcs.getProduct().getRarity().toPrettyString()).append(getSeparator());
						    	tmp.append(mcs.getQte()).append(getSeparator());
						    	tmp.append("").append(getSeparator());
						    	tmp.append(mcs.getProduct().getScryfallId()).append(getSeparator());
						    	tmp.append(UITools.formatDouble(mcs.getPrice())).append(getSeparator());
						    	tmp.append(false).append(getSeparator());
						    	tmp.append(mcs.isAltered()).append(getSeparator());
						    	tmp.append(mcs.getCondition().toPrettyString()).append(getSeparator());
						    	tmp.append(mcs.getLanguage()).append(getSeparator());
						    	tmp.append(getString("CURRENCY")).append(getSeparator());
						    	tmp.append(System.lineSeparator());
						    	notify(mcs);
						    }
					
						    FileTools.saveFile(f, tmp.toString());
	}
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put("CURRENCY", Currency.getInstance(Locale.getDefault()).getCurrencyCode());
		
		return m;
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
	protected String getSeparator() {
		return ",";
	}

}
