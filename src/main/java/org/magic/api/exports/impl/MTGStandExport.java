package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class MTGStandExport extends AbstractFormattedFileCardExport {


	private String columns = " Name,Quantity,Edition,\"Edition Code\",\"Collector Number\",Language,Foil,Condition,Rarity,Note";


	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

		var build = new StringBuilder();
					  build.append(columns).append(System.lineSeparator());

		for(MagicCardStock st : stock)
		{

			build.append("\"").append(st.getProduct().getName()).append("\",");
			build.append(st.getQte()).append(",");
			build.append("\"").append(st.getProduct().getCurrentSet().getSet()).append("\",");
			build.append(st.getProduct().getCurrentSet().getId()).append(",");
			build.append(st.getProduct().getNumber()).append(",");
			build.append(st.getLanguage()).append(",");
			if(st.isFoil())
				build.append("1,");
			else
				build.append("0,");

			build.append(convert(st.getCondition())).append(",");
			build.append(st.getProduct().getRarity()).append(",");
			build.append(st.getComment()).append(System.lineSeparator());
		}

		FileTools.saveFile(f, build.toString());

	}

	private String convert(EnumCondition condition) {
		switch (condition)
		{
		case LIGHTLY_PLAYED: return "Slightly Played";
		case PLAYED:return "Moderately Played";
		case POOR: return "Heavily Played";
		default: return "Near Mint";

		}
	}

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		List<MagicCardStock> ret = new ArrayList<>();
		for(Matcher m : matches(content, true))
		{
			var mc = parseMatcherWithGroup(m, 1, 4, true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);

			if(mc !=null )
			{

				var st = new MagicCardStock(mc);
				st.setQte(Integer.parseInt(m.group(2)));
				st.setLanguage(m.group(6));
				st.setFoil(m.group(7).equals("1"));
				st.setComment(m.group(10));

				ret.add(st);
				notify(mc);
			}



		}

		return ret;
	}


	@Override
	public String getName() {
		return "MTGStand";
	}

	@Override
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[] {" "};
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

}
