package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class DelverLensExport extends AbstractFormattedFileCardExport{


	
	private String columns= "Name; Edition; Price; Language; Collector's number; Condition; Currency; Edition code; Foil; List name; Quantity; Scryfall ID";

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.WEBSITE;
	}
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

		var temp = new StringBuilder(columns);
		temp.append(System.lineSeparator());
		stock.forEach(st->{
			temp.append(st.getProduct().getName()).append(getSeparator());
			temp.append(aliases.getSetNameFor(this,st.getProduct().getCurrentSet())).append(getSeparator());
			temp.append(UITools.formatDouble(st.getPrice())).append(getSeparator());
			temp.append(st.getLanguage()).append(getSeparator());
			temp.append(st.getProduct().getNumber()).append(getSeparator());
			temp.append(aliases.getConditionFor(this,st.getCondition())).append(getSeparator());
			temp.append(MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode()).append(getSeparator());
			temp.append(aliases.getSetIdFor(this,st.getProduct().getCurrentSet())).append(getSeparator());
			temp.append(st.isFoil()?"Foil":"").append(getSeparator());
			temp.append(st.getMagicCollection()).append(getSeparator());
			temp.append(st.getQte()).append(getSeparator());
			temp.append(st.getProduct().getScryfallId()).append(getSeparator());
			temp.append(System.lineSeparator());
		});
		FileTools.saveFile(f, temp.toString());
	}


	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		List<MTGCardStock> list = new ArrayList<>();

		matches(content,true).forEach(m->{

			MTGCard mc=null;
				try {
					mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(m.group(12));
				} catch (Exception e) {
					logger.error(e);
				}

			if(mc!=null)
			{
				var st = MTGControler.getInstance().getDefaultStock();
				st.setProduct(mc);
				st.setLanguage(m.group(4));
				st.setQte(Integer.parseInt(m.group(11)));
				st.setFoil(m.group(9).equalsIgnoreCase("foil")	);
				st.setCondition(aliases.getReversedConditionFor(this, m.group(6), EnumCondition.NEAR_MINT)  );
				st.setPrice(UITools.parseDouble(m.group(3).trim()));
				list.add(st);
			}
		});

		return list;
	}

	@Override
	public String getName() {
		return "DelverLens";
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
		return aliases.getRegexFor(this, "mtgcompanion");
	}

	@Override
	protected String getSeparator() {
		return getString("SEPARATOR");
	}

	@Override
	public Map<String, String> getDefaultAttributes() {

		var m = super.getDefaultAttributes();
			 m.put("SEPARATOR", ";");
			return m;
	}


}
