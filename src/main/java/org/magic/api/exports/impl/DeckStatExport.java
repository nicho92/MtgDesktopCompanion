package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class DeckStatExport extends AbstractFormattedFileCardExport {
	
	private static final String FORMAT = "FORMAT";
	private String columns="amount,card_name,is_foil,is_pinned,is_signed,set_id,set_code,collector_number,language,condition,comment,added\n";

	@Override
	public String getFileExtension() {
		return "."+getString(FORMAT).toLowerCase();
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File dest) throws IOException {
		var line = new StringBuilder(columns);
		for(MTGCardStock mc : stock)
		{
			if(getString(FORMAT).equalsIgnoreCase("TXT"))
				line.append(exportAsTxt(mc));
			else
				line.append(exportAsCSV(mc));
			
			line.append("\n");
			
			notify(mc.getProduct());
		}
		FileTools.saveFile(dest, line.toString());
	}


	private String exportAsCSV(MTGCardStock mc) {
		return new StringBuilder()
				.append(mc.getQte()).append(getSeparator())
				.append("\"").append(mc.getProduct().getName()).append("\"").append(getSeparator())
				.append(mc.isFoil()?"1":"").append(getSeparator())
				.append("").append(getSeparator())
				.append(mc.isSigned()?"1":"").append(getSeparator())
				.append("").append(getSeparator())
				.append("\"").append(mc.getProduct().getEdition().getId()).append("\"").append(getSeparator())
				.append("\"").append(mc.getLanguage().substring(0, 2)).append("\"").append(getSeparator())
				.append("\"").append(aliases.getConditionFor(this, mc.getCondition())).append("\"").append(getSeparator())
				.append("\"").append(mc.getComment()).append("\"").append(getSeparator())
				.append("").append(getSeparator())
				.toString();
	}

	private String exportAsTxt(MTGCardStock mc) {
		return new StringBuilder()
				.append(mc.getQte()).append(" ")
				.append("[").append(mc.getProduct().getEdition().getId()).append("#").append(mc.getProduct().getNumber()).append("] ")
				.append(mc.getProduct().getName()).append(" ")
				.append("#")
				.append(mc.getComment()).append(" ")
				.append("(COND=").append(aliases.getConditionFor(this, mc.getCondition())).append(")").append(" ")
				.append("(LANG=").append(mc.getLanguage().substring(0, 2)).append(")").append(" ")
				.append(mc.isFoil()?"!Foil":"").append(" ")
				.append(mc.isSigned()?"!Signed":"")
			
				
				.toString();
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> list = new ArrayList<>();

		matches(content,true).forEach(m->{
			
		});

		return list;
	}


	@Override
	public String getName() {
		return "DeckStats";
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
		return aliases.getRegexFor(this, getString(FORMAT));
	}
	
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = new HashMap<String, MTGProperty>();
			m.put(FORMAT, new MTGProperty("CSV", "select format for import/export items","CSV","TXT"));

		return m;
	}


	@Override
	public String getSeparator() {
		return ",";
	}

}
