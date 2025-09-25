package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

import nl.basjes.parse.useragent.yauaa.shaded.org.apache.commons.lang3.ArrayUtils;

public class ManaBoxExport extends AbstractFormattedFileCardExport {

	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}
	
	@Override
	public String getStockFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "Manabox";
	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		
		var d = new MTGDeck();
			 d.setName(name);
			 d.setDescription("Imported from " + getName());
		
			 
		var p = Pattern.compile(aliases.getRegexFor(this, "deck"));
		var map = d.getMain();
		
		
		for(String s : UITools.stringLineSplit(f, false))
		{
				var m = p.matcher(s);
				
				if(s.contains("SIDEBOARD"))
					map=d.getSideBoard();
			
				
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
	public List<MTGCardStock> importStock(String content) throws IOException {
		
		var list = new ArrayList<MTGCardStock>();
		
		
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
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		StringBuilder tmp = new StringBuilder();
						    tmp.append("Name,Set code,Set name,Collector number,Foil,Rarity,Quantity,ManaBox ID,Scryfall ID,Purchase price,Misprint,Altered,Condition,Language,Purchase price currency").append(System.lineSeparator());
						    
						    for(var mcs : stock)
						    {
						    	if(mcs.getProduct().getFullName().contains(","))
						    	{
						    		tmp.append("\"").append(mcs.getProduct().getFullName()).append("\"").append(getSeparator());	
						    	}
						    	else
						    	{
						    		tmp.append(mcs.getProduct().getFullName()).append(getSeparator());	
						    	}
						    	tmp.append(mcs.getProduct().getEdition().getId()).append(getSeparator());
						    	tmp.append(mcs.getProduct().getEdition().getSet()).append(getSeparator());
						    	tmp.append(mcs.getProduct().getNumber()).append(getSeparator());
						    	tmp.append(mcs.isFoil()?"foil":mcs.isEtched()?"etched":"normal").append(getSeparator());
						    	tmp.append(mcs.getProduct().getRarity().toPrettyString().toLowerCase()).append(getSeparator());
						    	tmp.append(mcs.getQte()).append(getSeparator());
						    	tmp.append(mcs.getTiersAppIds(getName())!=null?mcs.getTiersAppIds(getName()):"").append(getSeparator());
						    	tmp.append(mcs.getProduct().getScryfallId()).append(getSeparator());
						    	tmp.append(UITools.formatDouble(mcs.getValue().doubleValue(),'.')).append(getSeparator());
						    	tmp.append(false).append(getSeparator());
						    	tmp.append(mcs.isAltered()).append(getSeparator());
						    	tmp.append(aliases.getConditionFor(this, mcs.getCondition())).append(getSeparator());
						    	tmp.append(mcs.getLanguage()).append(getSeparator());
						    	tmp.append(getString("CURRENCY"));
						    	tmp.append(System.lineSeparator());
						    	notify(mcs.getProduct());
						    }
					
						    FileTools.saveFile(f, tmp.toString());
	}
	
	
	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		
		StringBuilder  temp = new StringBuilder();
		
		build(deck.getMain(),temp);
		
		if(!deck.getSideBoard().isEmpty()) {
			temp.append(System.lineSeparator());
			temp.append("// SIDEBOARD").append(System.lineSeparator());
			build(deck.getSideBoard(),temp);
		}
		
		FileTools.saveFile(dest, temp.toString());
		
	}

	private void build(Map<MTGCard, Integer> map, StringBuilder temp) {
		for(var e : map.entrySet())
		{
			temp.append(e.getValue()).append(" ")
					.append(e.getKey().getFullName()).append(" ")
					.append("(").append(e.getKey().getEdition().getId()).append(") ")
					.append(e.getKey().getNumber())
					.append(System.lineSeparator());
			notify(e.getKey());
		}
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put("CURRENCY", new MTGProperty(Currency.getInstance(Locale.getDefault()).getCurrencyCode(), "Choose current currency for your prices",ArrayUtils.toStringArray(Currency.getAvailableCurrencies().stream().map(c->c.getCurrencyCode()).toArray())));
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
