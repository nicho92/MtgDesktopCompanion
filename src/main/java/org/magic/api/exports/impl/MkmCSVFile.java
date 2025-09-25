package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class MkmCSVFile extends AbstractFormattedFileCardExport {


	private static String header="\"Name,\"\"Expansion\"\",\"\"Language\"\",\"\"Price\"\",\"\"Count\"\",\"\"Condition\"\",\"\"inShoppingCart\"\",\"\"lastEdited\"\",\"\"Foil\"\",\"\"Signed\"\",\"\"Altered\"\",\"\"Playset\"\",\"\"LocName\"\",\"\"Rarity\"\",\"\"Comments\"\",\"\"abbreviation\"\",\"\"releaseDate\"\"\"";


	@Override
	public String getStockFileExtension() {
		return ".csv";
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		var build = new StringBuilder();
		build.append(header);
		FileTools.saveFile(dest, build.toString());
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> ret = new ArrayList<>();


		content =content.replace("\"MYS\"", "\"MB1\"")
				.replace("\"DTL\"", "\"FBB\"");

		matches(content, true).forEach(m->{

			MTGCard mc = parseMatcherWithGroup(m, 1, 16, true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);

			if(mc==null)
			{
				try {
					mc = parseMatcherWithGroup(m, 1, 2, true, FORMAT_SEARCH.NAME, FORMAT_SEARCH.NAME);
				}catch(Exception _)
				{
					logger.error("Card is not found with this provider : {}",m.group());
				}
			}

			if(mc!=null) {
				var stock = new MTGCardStock(mc);
				stock.setLanguage(m.group(3));
				stock.setFoil(Boolean.parseBoolean(m.group(9)));
				stock.setSigned(Boolean.parseBoolean(m.group(10)));
				stock.setAltered(Boolean.parseBoolean(m.group(11)));
				var count = Integer.parseInt(m.group(5));
				if(Boolean.parseBoolean(m.group(12)))
					count = count*4;


				try {
					stock.setPrice(Double.parseDouble(m.group(4)));
				}
				catch(Exception e)
				{
					logger.error(e);
				}
				stock.setQte(count);
				stock.setComment(m.group(15));
				ret.add(stock);
			}
			else
			{
				logger.debug("No card found for {}",m.group());
			}
		});

		return ret;
	}

	@Override
	public String getName() {
		return "MKM CSV File";
	}

	@Override
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return  new String[0];
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MKMFileWantListExport.class.getResource("/icons/plugins/magiccardmarket.png"));
	}

}
