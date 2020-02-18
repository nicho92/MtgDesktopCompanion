package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.tools.FileTools;

public class DeckedBuilder extends AbstractFormattedFileCardExport {

	
	private static final String columns ="Total Qty,Reg Qty,Foil Qty,Card,Set,Mana Cost,Card Type,Color,Rarity,Mvid,Single Price,Single Foil Price,Total Price,Price Source,Notes";
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "DeckedBuilder";
	}
	
	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
	}

	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
	
		StringBuilder temp = new StringBuilder(columns);
		temp.append(System.lineSeparator());
		stock.forEach(st->{
			temp.append(st.getQte()).append(getSeparator());
			
			if(st.isFoil())
				temp.append(0).append(getSeparator()).append(st.getQte()).append(getSeparator());
			else
				temp.append(st.getQte()).append(getSeparator()).append(0).append(getSeparator());
			
			if(st.getMagicCard().getName().contains(","))
				temp.append("\"").append(st.getMagicCard().getName()).append("\"").append(getSeparator());
			else
				temp.append(st.getMagicCard().getName()).append(getSeparator());
			
			temp.append(st.getMagicCard().getCurrentSet().getSet()).append(getSeparator());
			temp.append(st.getMagicCard().getCost()).append(getSeparator());
			temp.append(st.getMagicCard().getFullType()).append(getSeparator());
			
			
			if(st.getMagicCard().getColors().size()>1)
				temp.append("Gold").append(getSeparator());
			else
				temp.append(st.getMagicCard().getColors()).append(getSeparator());
			
			temp.append(st.getMagicCard().getCurrentSet().getRarity()).append(getSeparator());
			temp.append(st.getMagicCard().getCurrentSet().getMultiverseid()).append(getSeparator());
			
			if(st.isFoil())
				temp.append(0.0).append(getSeparator()).append(st.getPrice()).append(getSeparator());
			else
				temp.append(st.getPrice()).append(getSeparator()).append(0.0).append(getSeparator());
			
			
			temp.append("MtgCompanion").append(getSeparator());
			temp.append("\"").append(st.getComment()).append("\"").append(getSeparator());
			
			
			temp.append(System.lineSeparator());
		});
		
		FileTools.saveFile(f, temp.toString());
		
	}
	
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		
		List<MagicCardStock> stocks = new ArrayList<>();
		
		matches(content,true).forEach(m->{
			Integer qtyRegular = Integer.parseInt(m.group(2));
			Integer qtyFoil = Integer.parseInt(m.group(3));
			MagicCard mc = parseMatcherWithGroup(m, 4, 5, true, FORMAT_SEARCH.NAME,FORMAT_SEARCH.NAME);
			if(mc!=null)
			{
				if(qtyFoil>0)
				{
					MagicCardStock stock = new MagicCardStock();
								   stock.setMagicCard(mc);
								   stock.setPrice(Double.parseDouble(m.group(12)));
								   stock.setFoil(true);
								   stock.setQte(qtyFoil);
					stocks.add(stock);
				}
				
				if(qtyRegular>0)
				{
					MagicCardStock stock = new MagicCardStock();
					   stock.setMagicCard(mc);
					   stock.setPrice(Double.parseDouble(m.group(11)));
					   stock.setFoil(false);
					   stock.setQte(qtyRegular);
					   stocks.add(stock);
				}
				notify(mc);
			}
		});
		return stocks;
	}
	
	@Override
	public MagicDeck importDeck(String f, String dname) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName(dname);
		
		for(MagicCardStock st : importStock(f))
		{
			d.getMap().put(st.getMagicCard(), st.getQte());
		}
		return d;
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
