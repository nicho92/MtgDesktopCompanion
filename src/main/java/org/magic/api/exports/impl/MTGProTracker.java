package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class MTGProTracker extends AbstractFormattedFileCardExport {
	
	@Override
	public MODS getMods() {
			return MODS.IMPORT;
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}
	
	
	public static void main(String[] args) throws IOException {
		new MTGProTracker().importStockFromFile(new File("D:\\Téléchargements\\collection.csv"));

	}
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		
		var ret = new ArrayList<MTGCardStock>();
		
		
		matches(content, true).forEach(m->{
			
			var mc = parseMatcherWithGroup(m, 1, 2, true, FORMAT_SEARCH.ID, FORMAT_SEARCH.NAME);
			if(mc != null)
			{
				var mcs = MTGControler.getInstance().getDefaultStock();
				mcs.setProduct(mc);
				mcs.setCondition(EnumCondition.ONLINE);
				mcs.setQte(Integer.parseInt(m.group(4)));
				mcs.setLanguage(MTGControler.getInstance().getLocale().getLanguage());
				ret.add(mcs);
				notify(mc);
			}
			
		});
		return ret;
	}
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "MTGAProTracker";
	}
	
	@Override
	public String getVersion() {
		return "2.2.35";
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
