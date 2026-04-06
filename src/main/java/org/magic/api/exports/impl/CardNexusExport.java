package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class CardNexusExport  extends AbstractFormattedFileCardExport {

	
	
	private static final String COLUMNS = "\"totalQtyOwned\",\"name\",\"printNumber\",\"finish\",\"variant\",\"expansion\",\"game\",\"condition\",\"language\",\"price\"";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		
		var builder = new StringBuilder(COLUMNS).append(System.lineSeparator());
		
		
		for(var mcs : stock)
		{
			
		}
		
		
		
		FileTools.saveFile(f, builder.toString());
		
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
