package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;

public class ArchidektExport extends AbstractFormattedFileCardExport {

	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.WEBSITE;
	}


	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> ret = new ArrayList<>();
		matches(content, true).forEach(m->{

			MTGCardStock st = MTGControler.getInstance().getDefaultStock();
						   st.setQte(Integer.parseInt(m.group(1)));

			 MTGCard mc = parseMatcherWithGroup(m, 2, 8, true, FORMAT_SEARCH.ID,FORMAT_SEARCH.NAME);

			 if(mc!=null)
			 {

				 st.setProduct(mc);
				 st.setFoil(m.group(3).equalsIgnoreCase("true"));
				 st.setCondition(aliases.getReversedConditionFor(this, m.group(4),EnumCondition.GOOD));
				 st.setLanguage(m.group(5));
				 ret.add(st);
				 notify(mc);
			 }
		});
		return ret;
	}


	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

		var temp = new StringBuilder();

		for(MTGCardStock mcs : stock)
		{
			temp.append(mcs.getQte()).append(getSeparator());
			temp.append("\"").append(mcs.getProduct().getName()).append("\"").append(getSeparator());
			temp.append(StringUtils.capitalize(String.valueOf(mcs.isFoil()))).append(getSeparator());
			temp.append(aliases.getConditionFor(this,mcs.getCondition())).append(getSeparator());
			temp.append(mcs.getLanguage()).append(getSeparator());
			temp.append(getSeparator());
			temp.append(mcs.getProduct().getCurrentSet()).append(getSeparator());
			temp.append(mcs.getProduct().getEdition().getId()).append(getSeparator());
			temp.append(mcs.getProduct().getMultiverseid());
			temp.append(System.lineSeparator());
		}

		FileTools.saveFile(f, temp.toString());
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
	protected String getSeparator() {
		return ",";
	}


	@Override
	public String getFileExtension() {
		 return ".csv";
	}

}
