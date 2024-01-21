package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class DragonShieldExport extends AbstractFormattedFileCardExport {

	private static final String COLUMNS ="Folder Name,Quantity,Trade Quantity,Card Name,Set Code,Set Name,Card Number,Condition,Printing,Language,Price Bought,Date Bought";
	

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		StringBuilder temp = new StringBuilder();
						   temp.append(COLUMNS).append(System.lineSeparator());
						   
				stock.forEach(mcs->{
					   temp.append(mcs.getMagicCollection()).append(getSeparator()).append(System.lineSeparator());
					   temp.append(mcs.getQte()).append(getSeparator()).append(System.lineSeparator());
					   temp.append(0).append(getSeparator()).append(System.lineSeparator());
					   temp.append(mcs.getQte()).append(getSeparator()).append(System.lineSeparator());
					   temp.append(commated(mcs.getProduct().getName())).append(getSeparator()).append(System.lineSeparator());
					   temp.append(mcs.getProduct().getCurrentSet().getId()).append(getSeparator()).append(System.lineSeparator());
					   temp.append(mcs.getProduct().getCurrentSet().getSet()).append(getSeparator()).append(System.lineSeparator());
					   temp.append(mcs.getProduct().getCurrentSet().getNumber()).append(getSeparator()).append(System.lineSeparator());
					   temp.append(aliases.getConditionFor(this, mcs.getCondition())).append(getSeparator()).append(System.lineSeparator());
					   temp.append("Normal").append(getSeparator()).append(System.lineSeparator());
					   temp.append(mcs.getLanguage()).append(getSeparator()).append(System.lineSeparator());
					   temp.append(mcs.getPrice()).append(getSeparator()).append(System.lineSeparator());
					   temp.append(UITools.formatDate(new Date(), "MM/dd/yyyy"));
					   notify(mcs.getProduct());
				});	   
					
						 
		FileTools.saveFile(f, temp.toString());
	}
	
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {

		return new ArrayList<>();
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
