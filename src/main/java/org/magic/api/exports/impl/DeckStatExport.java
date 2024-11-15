package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;

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
	public STATUT getStatut() {
		return STATUT.DEV;
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
				.append("\"").append(mc.getProduct().getNumber()).append("\"").append(getSeparator())
				.append("\"").append(mc.getLanguage().toLowerCase().substring(0, 2)).append("\"").append(getSeparator())
				.append("\"").append(aliases.getConditionFor(this, mc.getCondition())).append("\"").append(getSeparator())
				.append("\"").append(mc.getComment()).append("\"").append(getSeparator())
				.toString();
	}

	private String exportAsTxt(MTGCardStock mc) {
		return new StringBuilder()
				.append(mc.getQte()).append(" ")
				.append("[").append(mc.getProduct().getEdition().getId()).append("#").append(mc.getProduct().getNumber()).append("] ")
				.append(mc.getProduct().getName()).append(" ")
				.append("#")
				.append(mc.getComment()).append(" ")
				.append(mc.isFoil()?"!Foil":"").append(" ")
				.append(mc.isSigned()?"!Signed":"").append(" ")
				.append("(COND=").append(aliases.getConditionFor(this, mc.getCondition())).append(")").append(" ")
				.append("(LANG=").append(mc.getLanguage().substring(0, 2)).append(")").append(" ")
				
				.toString();
	}
	
	
	private MTGCardStock parseAsCsv(Matcher m)
	{
		var mc = parseMatcherWithGroup(m, 8, 7,true,FORMAT_SEARCH.ID,FORMAT_SEARCH.NUMBER);
		
		var mcs = MTGControler.getInstance().getDefaultStock();
			mcs.setProduct(mc);
			mcs.setQte(Integer.parseInt(m.group(1)));
			mcs.setFoil(!StringUtils.isEmpty(m.group(3)));
			mcs.setSigned(!StringUtils.isEmpty(m.group(5)));
			mcs.setCondition(aliases.getReversedConditionFor(this, m.group(10), EnumCondition.NEAR_MINT));
			
			if(!StringUtils.isEmpty(m.group(9)))
				mcs.setLanguage(m.group(9));
			
			
			
		return mcs;
	}
	
	private MTGCardStock parseAsTxt(Matcher m)
	{
		
		var mcs = MTGControler.getInstance().getDefaultStock();
		
		var setId = m.group(2).split("#")[0];
		var setNumber = m.group(2).split("#")[1];
		
		
		try {
				mcs.setProduct(MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(setNumber, setId));
				mcs.setQte(Integer.parseInt(m.group(1)));
				mcs.setFoil(m.group(4).contains("!Foil"));
				mcs.setSigned(m.group(4).contains("!Signed"));
				mcs.setLanguage(parseAttributs("LNG",m.group(4)));
				mcs.setCondition(aliases.getReversedConditionFor(this, parseAttributs("COND",m.group(4)), EnumCondition.NEAR_MINT));
		} catch (IOException e) {
			logger.error("can't find card with {} for the set {}",setNumber,setId);
		}
		
		return mcs;
	}
	
	

	private String parseAttributs(String key, String data) {
		
		
		var p  = Pattern.compile("\\("+key+"=(.*?)\\)");
		var m = p.matcher(data);
		
		if(m.find())
			return m.group(1);
		
		return "";
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> list = new ArrayList<>();

		matches(content,true).forEach(m->{
			
			if(getString(FORMAT).equalsIgnoreCase("TXT"))
				list.add(parseAsTxt(m));
			else
				list.add(parseAsCsv(m));

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
		return aliases.getRegexFor(this, getString(FORMAT).toLowerCase());
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
