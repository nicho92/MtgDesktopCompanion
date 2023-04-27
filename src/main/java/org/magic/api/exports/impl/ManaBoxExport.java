package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class ManaBoxExport extends AbstractFormattedFileCardExport {

	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "Manabox";
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		
		var d = new MagicDeck();
			 d.setName(name);
			 d.setDescription("Imported from " + getName());
		
			 
		var p = Pattern.compile(aliases.getRegexFor(this, "deck"));
		var map = d.getMain();
		
		
		for(String s : UITools.stringLineSplit(f, false))
		{
				var m = p.matcher(s);
				
				if(s.contains("SIDEBOARD"))
					map=d.getSideBoard();
				else if(s.contains("MAYBEBOARD"))
					map=d.getMaybeBoard();
				
				
				if(m.find())
				{
					var card = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(m.group(4),m.group(3));
					
					if(card!=null)
					{	
						var qty = Integer.parseInt(m.group(1));
						map.put(card, qty);
					}
					else
					{
						logger.error("No card found for {}", s);
					}
				}
		}
		
		return d;
	}
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		
		var list = new ArrayList<MagicCardStock>();
		
		
		matches(content, true, aliases.getRegexFor(this, "stock")).forEach(m->{
			var stock = MTGControler.getInstance().getDefaultStock();
			var mc = parseMatcherWithGroup(m, 4, 2,true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NUMBER);
			
			if(mc!=null){
				stock.setProduct(mc);
				stock.setFoil(m.group(5).equalsIgnoreCase("foil"));
				stock.setEtched(m.group(5).equalsIgnoreCase("etched"));
				stock.setQte(Integer.parseInt(m.group(7)));
				stock.setAltered(m.group(12).equals("true"));
				stock.setCondition(aliases.getReversedConditionFor(this, m.group(13), EnumCondition.NEAR_MINT));
				stock.setLanguage(m.group(14));
				stock.setPrice(UITools.parseDouble(m.group(10)));
				stock.getTiersAppIds().put(getName(), m.group(8));
				list.add(stock);
				notify(mc);
			}
			else
			{
				logger.error("No card found for {}",m );
			}
		});
		
		return list;
		
	}
	
	
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		StringBuilder tmp = new StringBuilder();
						    tmp.append("Name,Set code,Set name,Collector number,Foil,Rarity,Quantity,ManaBox ID,Scryfall ID,Purchase price,Misprint,Altered,Condition,Language,Purchase price currency").append(System.lineSeparator());
						    
						    for(var mcs : stock)
						    {
						    	if(mcs.getProduct().getName().contains("\""))
						    	{
						    		tmp.append("\"").append(mcs.getProduct().getFullName()).append("\"").append(getSeparator());	
						    	}
						    	else
						    	{
						    		tmp.append(mcs.getProduct().getFullName()).append(getSeparator());	
						    	}
						    	tmp.append(mcs.getProduct().getCurrentSet().getId()).append(getSeparator());
						    	tmp.append(mcs.getProduct().getCurrentSet().getSet()).append(getSeparator());
						    	tmp.append(mcs.getProduct().getCurrentSet().getNumber()).append(getSeparator());
						    	tmp.append(mcs.isFoil()?"foil":mcs.isEtched()?"etched":"normal").append(getSeparator());
						    	tmp.append(mcs.getProduct().getRarity().toPrettyString()).append(getSeparator());
						    	tmp.append(mcs.getQte()).append(getSeparator());
						    	tmp.append("").append(getSeparator());
						    	tmp.append(mcs.getProduct().getScryfallId()).append(getSeparator());
						    	tmp.append(UITools.formatDouble(mcs.getPrice())).append(getSeparator());
						    	tmp.append(false).append(getSeparator());
						    	tmp.append(mcs.isAltered()).append(getSeparator());
						    	tmp.append(aliases.getConditionFor(this, mcs.getCondition())).append(getSeparator());
						    	tmp.append(mcs.getLanguage()).append(getSeparator());
						    	tmp.append(getString("CURRENCY")).append(getSeparator());
						    	tmp.append(System.lineSeparator());
						    	notify(mcs.getProduct());
						    }
					
						    FileTools.saveFile(f, tmp.toString());
	}
	
	
	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		
		StringBuilder  temp = new StringBuilder();
		
		build(deck.getMain(),temp);
		
		if(!deck.getSideBoard().isEmpty()) {
			temp.append(System.lineSeparator());
			temp.append("// SIDEBOARD").append(System.lineSeparator());
			build(deck.getSideBoard(),temp);
		}
		
		if(!deck.getMaybeBoard().isEmpty()) {
			temp.append(System.lineSeparator());
			temp.append("// MAYBEBOARD").append(System.lineSeparator());
			build(deck.getMaybeBoard(),temp);
		}
		
		FileTools.saveFile(dest, temp.toString());
		
	}

	private void build(Map<MagicCard, Integer> map, StringBuilder temp) {
		for(var e : map.entrySet())
		{
			temp.append(e.getValue()).append(" ")
					.append(e.getKey().getFullName()).append(" ")
					.append("(").append(e.getKey().getCurrentSet().getId()).append(") ")
					.append(e.getKey().getCurrentSet().getNumber())
					.append(System.lineSeparator());
			notify(e.getKey());
		}
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
