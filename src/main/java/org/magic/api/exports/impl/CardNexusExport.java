package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class CardNexusExport  extends AbstractFormattedFileCardExport {

	
	
	private static final String COLUMNS = "\"Qty\",\"name\",\"printNumber\",\"finish\",\"variant\",\"expansion\",\"game\",\"condition\",\"language\",\"price\"";
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		var ret = new ArrayList<MTGCardStock>();
		
		
		matches(content, true).forEach(m->{
			var mcs = MTGControler.getInstance().getDefaultStock();
			var mc = parseMatcherWithGroup(m, 3, 6, true , FORMAT_SEARCH.NAME, FORMAT_SEARCH.NUMBER);
			
			if(mc!=null) {
				 mcs.setQte(Integer.parseInt(m.group(1)));
				 mcs.setProduct(mc);
				 mcs.setCondition(aliases.getReversedConditionFor(this, m.group(8),EnumCondition.NEAR_MINT));
				 mcs.setFoil(m.group(4).equalsIgnoreCase("Foil"));
				 mcs.setEtched(m.group(4).equalsIgnoreCase("Etched"));
				 mcs.setLanguage(m.group(9));
				 mcs.setPrice(UITools.parseDouble(m.group(10)));
				
				 ret.add(mcs);
				 notify(mc);
			}
			else
			{
				logger.error("no card found for {}",m);
			}
			
		});
		
		
		
		return ret;
		
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		
		var builder = new StringBuilder(COLUMNS).append(System.lineSeparator());
		
		
		for(var mcs : stock)
		{
			builder.append("\"").append(mcs.getQte()).append("\"").append(getSeparator());
			builder.append("\"").append(mcs.getProduct().getName().replace("\"", "")).append("\"").append(getSeparator());
			builder.append("\"").append(mcs.getProduct().getNumber()).append("\"").append(getSeparator());
			builder.append("\"").append(parseFinish(mcs)).append("\"").append(getSeparator());
			builder.append("\"").append("").append("\"").append(getSeparator());
			builder.append("\"").append(mcs.getProduct().getEdition().getSet()).append("\"").append(getSeparator());
			builder.append("\"").append("Magic: The Gathering").append("\"").append(getSeparator());
			builder.append("\"").append(aliases.getConditionFor(this, mcs.getCondition())).append("\"").append(getSeparator());
			builder.append("\"").append(mcs.getLanguage().substring(0, 2)).append("\"").append(getSeparator());
			builder.append("\"").append(UITools.formatDouble(mcs.getPrice(),'.')).append("\"").append(System.lineSeparator());
			
			notify(mcs.getProduct());
		}
		FileTools.saveFile(f, builder.toString());
	}
	
	
	private String parseFinish(MTGCardStock mcs) {
		if(mcs.isFoil())
			return "Foil";

		if(mcs.isEtched())
			return "Etched";
		
		return "Standard";	
		
		
	}


	@Override
	public String getStockFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "CardNexus";
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
