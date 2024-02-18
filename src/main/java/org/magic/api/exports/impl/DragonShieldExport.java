package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class DragonShieldExport extends AbstractFormattedFileCardExport {

	private static final String COLUMNS_STOCK ="Folder Name,Quantity,Trade Quantity,Card Name,Set Code,Set Name,Card Number,Condition,Printing,Language,Price Bought,Date Bought";
	private static final String COLUMNS_DECK ="Card Type,Quantity,Card Name,Mana Cost,Min Price (AVG)";
	
	@Override
	public void exportDeck(MTGDeck deck, File f) throws IOException {
		var temp = new StringBuilder();
		   				   temp.append(COLUMNS_DECK).append(System.lineSeparator());
		 
		   				   deck.getMain().entrySet().forEach(e->{
		   					   	temp.append(e.getKey().getTypes().get(0)).append(getSeparator());
		   					   	temp.append(e.getValue()).append(getSeparator());
		   					   	temp.append(commated(e.getKey().getName())).append(getSeparator());
		   					   	temp.append(e.getKey().getCost()).append(getSeparator());
		   					   	temp.append("0.0").append(System.lineSeparator());
		   				   });			   
		   				   
		   				   deck.getSideBoard().entrySet().forEach(e->{
			   					temp.append("Sideboard").append(getSeparator());
		   					   	temp.append(e.getValue()).append(getSeparator());
		   					   	temp.append(commated(e.getKey().getName())).append(getSeparator());
		   					   	temp.append(e.getKey().getCost()).append(getSeparator());
		   					   	temp.append("0.0").append(System.lineSeparator());
		   				   });
		   				   
		   FileTools.saveFile(f, temp.toString());
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.WEBSITE;
	}
	
	@Override
	public MTGDeck importDeck(String content, String name) throws IOException {
		
		var d = new MTGDeck();
		
		
		matches(content, true, aliases.getRegexFor(this,"deck")).forEach(m->{
			var mc = parseMatcherWithGroup(m, 4, -1,true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);
			
			if(mc!=null) {
				if(m.group(1).equalsIgnoreCase("sideboard"))
					d.getSideBoard().put(mc, Integer.parseInt(m.group(2)));
				else
					d.getMain().put(mc, Integer.parseInt(m.group(2)));
				
				notify(mc);
			}
			else
			{
				logger.error("cant't get card {}",m.group(4));
			}
		});
			
		return d;
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		StringBuilder temp = new StringBuilder();
						   temp.append(COLUMNS_STOCK).append(System.lineSeparator());
						   
				stock.forEach(mcs->{
					   temp.append(mcs.getMagicCollection()).append(getSeparator());
					   temp.append(mcs.getQte()).append(getSeparator());
					   temp.append(0).append(getSeparator());
					   temp.append(commated(mcs.getProduct().getName())).append(getSeparator());
					   temp.append(mcs.getProduct().getCurrentSet().getId()).append(getSeparator());
					   temp.append(mcs.getProduct().getCurrentSet().getSet()).append(getSeparator());
					   temp.append(mcs.getProduct().getNumber()).append(getSeparator());
					   temp.append(aliases.getConditionFor(this, mcs.getCondition())).append(getSeparator());
					   temp.append(mcs.isFoil()?"Foil":"Normal").append(getSeparator());
					   temp.append(mcs.getLanguage()).append(getSeparator());
					   temp.append(UITools.formatDouble(mcs.getPrice(),"#0.0#",'.')).append(getSeparator());
					   temp.append(UITools.formatDate(new Date(), "MM/dd/yyyy")).append(System.lineSeparator());
					   notify(mcs.getProduct());
				});	   
		FileTools.saveFile(f, temp.toString());
	}
	
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

	var list = new ArrayList<MTGCardStock>();
		
		
		matches(content, true, aliases.getRegexFor(this,"stock")).forEach(m->{
			var stock = MTGControler.getInstance().getDefaultStock();
			var mc = parseMatcherWithGroup(m, 7, 5,false, FORMAT_SEARCH.ID, FORMAT_SEARCH.NUMBER);
			if(mc!=null){
				stock.setProduct(mc);
				stock.setQte(Integer.parseInt(m.group(2)));
				stock.setLanguage(m.group(10));
				stock.setMagicCollection(new MTGCollection(m.group(1)));
				stock.setPrice(UITools.parseDouble(m.group(11)));
				stock.setFoil(m.group(9).equalsIgnoreCase("true"));
				stock.setCondition(aliases.getReversedConditionFor(this, m.group(8), EnumCondition.NEAR_MINT));
				list.add(stock);
				notify(mc);
			}
		});
		
		return list;
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
	
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "DragonShield";
	}
	
	

}
