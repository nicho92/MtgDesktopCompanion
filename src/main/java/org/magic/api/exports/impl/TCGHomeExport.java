package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.UITools;

public class TCGHomeExport extends AbstractFormattedFileCardExport {

	
	private static final  String COLUMNS="amount,name,finish,set,collector_number,language,condition,scryfall_id,purchase_price";
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
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
			builder.append(mcs.isFoil()?"foil":"nonfoil").append(getSeparator());
			builder.append(mcs.getProduct().getEdition().getSet()).append(getSeparator());
			builder.append(mcs.getProduct().getNumber()).append(getSeparator());
			builder.append(mcs.getLanguage()).append(getSeparator());
			builder.append(aliases.getConditionFor(this, mcs.getCondition())).append(getSeparator());
			builder.append(mcs.getProduct().getScryfallId()).append(getSeparator());
			builder.append(UITools.formatDouble(mcs.getPrice())).append(getSeparator());
			builder.append(System.lineSeparator());
			notify(mcs.getProduct());
		}
			
			
		
		super.exportStock(stock, f);
	}
	
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		// TODO Auto-generated method stub
		return super.importStock(content);
	}
	
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		 return "TCGHome";
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
