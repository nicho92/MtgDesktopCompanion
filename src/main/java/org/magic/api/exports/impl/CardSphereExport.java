package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;

public class CardSphereExport extends AbstractFormattedFileCardExport {

	private static final String COLUMNS="Count,Tradelist Count,Name,Edition,Condition,Language,Foil,Tags";

	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		List<MTGCardStock> list = new ArrayList<>();

		matches(content,true).forEach(m->{

			MTGCard mc=null;
				try {
					mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(m.group(3),null,true).get(0);
				} catch (Exception e) {
					logger.error(e);
				}

			if(mc!=null)
			{
				var st = MTGControler.getInstance().getDefaultStock();
				st.setProduct(mc);
				st.setLanguage(m.group(6));
				st.setQte(Integer.parseInt(m.group(1)));
				st.setFoil(m.group(7).equalsIgnoreCase("foil")	);
				st.setCondition(aliases.getReversedConditionFor(this, m.group(5), EnumCondition.NEAR_MINT)  );
				st.setPrice(0.0);
				notify(mc);
				list.add(st);
			}
		});

		return list;
	}
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

		var buff = new StringBuilder(COLUMNS).append(System.lineSeparator());
		
		stock.forEach(mcs->{
			buff
			.append("\"").append(mcs.getQte()).append("\"").append(getSeparator())
			.append("\"").append(mcs.getQte()).append("\"").append(getSeparator())
			.append("\"").append(mcs.getProduct().getName()).append("\"").append(getSeparator())
			.append("\"").append(mcs.getProduct().getEdition().getSet()).append("\"").append(getSeparator())
			.append("\"").append(aliases.getConditionFor(this, mcs.getCondition())).append("\"").append(getSeparator())
			.append("\"").append(mcs.getLanguage()).append("\"").append(getSeparator())
			.append("\"").append(mcs.isFoil()?"Foil":"").append("\"").append(getSeparator())
			.append("\"").append("").append("\"").append(System.lineSeparator());
			notify(mcs.getProduct());
		});
		
		FileTools.saveFile(f, buff.toString());
		
	}
	
	
	
	@Override
	public String getStockFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "CardSphere";
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
