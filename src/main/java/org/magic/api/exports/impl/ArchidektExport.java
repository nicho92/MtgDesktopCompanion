package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.tools.FileTools;

public class ArchidektExport extends AbstractFormattedFileCardExport {


	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		
		exportStock(importFromDeck(deck), dest);
	}
	

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		
		List<MagicCardStock> ret = new ArrayList<>();
		matches(content, true).forEach(m->{
			
			MagicCardStock st = MTGControler.getInstance().getDefaultStock();
						   st.setQte(Integer.parseInt(m.group(1)));
						   
			 MagicCard mc = parseMatcherWithGroup(m, 2, 8, true, FORMAT_SEARCH.ID,FORMAT_SEARCH.NAME);
			 
			 if(mc!=null)
			 {
				 
				 st.setProduct(mc);
				 st.setFoil(m.group(3).equalsIgnoreCase("true"));
				 st.setCondition(reverse(m.group(4)));
				 st.setLanguage(m.group(5));
				 ret.add(st);
				 notify(mc);
			 }
		});
		return ret;
	}
	

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		var d = new MagicDeck();
		d.setName(name);
		
		for(MagicCardStock st : importStock(f))
			d.getMain().put(st.getProduct(), st.getQte());
	
		return d;
	}
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		
		var temp = new StringBuilder();
		
		for(MagicCardStock mcs : stock)
		{
			temp.append(mcs.getQte()).append(getSeparator());
			temp.append("\"").append(mcs.getProduct().getName()).append("\"").append(getSeparator());
			temp.append(StringUtils.capitalize(String.valueOf(mcs.isFoil()))).append(getSeparator());
			temp.append(reverse(mcs.getCondition())).append(getSeparator());
			temp.append(mcs.getLanguage()).append(getSeparator());
			temp.append(getSeparator());
			temp.append(mcs.getProduct().getCurrentSet()).append(getSeparator());
			temp.append(mcs.getProduct().getCurrentSet().getId()).append(getSeparator());
			temp.append(mcs.getProduct().getCurrentSet().getMultiverseid());
			temp.append(System.lineSeparator());
		}
		
		FileTools.saveFile(f, temp.toString());
		
		
		
		
	}

	private String reverse(EnumCondition condition)
	{
		switch (condition)
		{
		 case LIGHTLY_PLAYED: return "MP";
		 case MINT : return "LP";
		 case NEAR_MINT : return "NM";
		 case POOR : return "D";
		 case PLAYED : return "HP";
		 default : return "NM";
		}
		
	}
	

	private EnumCondition reverse(String condition)
	{
		switch (condition)
		{
		 case "NM": return EnumCondition.NEAR_MINT;
		 case "LP":  return EnumCondition.MINT;
		 case "MP": return EnumCondition.LIGHTLY_PLAYED;
		 case "HP": return EnumCondition.PLAYED;
		 case "D": return EnumCondition.POOR;
		 default : EnumCondition.valueOf(condition.toUpperCase());
		}
		return null;
	}
	
	@Override
	public String getName() {
		return "Archidekt";
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
		return "(\\d+),((?=\\\")\\\".*?\\\"|.*?),(True|False),(NM|LP|MP|HP|D),(.*?),((?=\\\")\\\".*?\\\"|.*?),(.*?),(.*?),(\\d+),(.*?),(\\d+)";
	}

	@Override
	protected String getSeparator() {
		return ",";
	}
	

	@Override
	public String getFileExtension() {
		 return ".csv";
	}

}
