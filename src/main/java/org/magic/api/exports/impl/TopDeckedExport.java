package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class TopDeckedExport extends AbstractFormattedFileCardExport {

	
	private static final String COLUMNS = "QUANTITY,\"NAME\",SETCODE,\"SETNAME\",FOIL,PURCHASE PRICE,RARITY,ID";
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		
		var builder = new StringBuilder(COLUMNS);
			 builder.append(System.lineSeparator());
			 
		for(var mcs : stock)
		{
			builder.append(mcs.getQte()).append(getSeparator());
			builder.append(commated(mcs.getProduct().getName())).append(getSeparator());
			builder.append(mcs.getProduct().getEdition().getId()).append(getSeparator());
			builder.append("\"").append(mcs.getProduct().getEdition().getSet()).append("\"").append(getSeparator());
			builder.append(mcs.isFoil()?"foil":"").append(getSeparator());
			builder.append(mcs.getValue().doubleValue()).append(getSeparator());
			builder.append(mcs.getProduct().getRarity().toPrettyString()).append(getSeparator());
			builder.append(mcs.getProduct().getScryfallId()).append(getSeparator());
			builder.append(System.lineSeparator());
			notify(mcs.getProduct());
		}
			 
		FileTools.saveFile(f, builder.toString());
		
		
		
	}
	

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "Top Decked";
	}

	@Override
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

}
