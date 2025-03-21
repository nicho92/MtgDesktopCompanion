package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class ArchidektExport extends AbstractFormattedFileCardExport {

	private static final String COLUMNS ="Quantity,Name,Finish,Condition,Date Added,Language,Purchase Price,Tags,Edition Name,Edition Code,Multiverse Id,Scryfall ID,Collector Number";
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}


	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		var ret = new ArrayList<MTGCardStock>();
		matches(content, true).forEach(m->{

			var st = MTGControler.getInstance().getDefaultStock();
				  st.setQte(Integer.parseInt(m.group(1)));

			MTGCard mc=null;
			try {
				mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(m.group(10));
			} catch (IOException e) {
				logger.error(e);
			}

			 if(mc!=null)
			 {

				 st.setProduct(mc);
				 st.setFoil(m.group(3).equalsIgnoreCase("Foil"));
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

		var temp = new StringBuilder(COLUMNS);
		temp.append(System.lineSeparator());

		for(var mcs : stock)
		{
			temp.append(mcs.getQte()).append(getSeparator());
			temp.append(commated(mcs.getProduct().getName())).append(getSeparator());
			temp.append(mcs.isFoil()?"Foil":"Normal").append(getSeparator());
			temp.append(aliases.getConditionFor(this,mcs.getCondition())).append(getSeparator());
			temp.append(UITools.formatDate(new Date(), "yyyy-MM-dd")).append(getSeparator());
			temp.append(mcs.getLanguage()).append(getSeparator());
			temp.append(UITools.roundDouble(mcs.getValue().doubleValue())).append(getSeparator());
			temp.append(getSeparator());
			temp.append(mcs.getProduct().getEdition().getSet()).append(getSeparator());
			temp.append(mcs.getProduct().getEdition().getId()).append(getSeparator());
			temp.append(mcs.getProduct().getMultiverseid()).append(getSeparator());
			temp.append(mcs.getProduct().getScryfallId()).append(getSeparator());
			temp.append(mcs.getProduct().getNumber());
			temp.append(System.lineSeparator());
			notify(mcs.getProduct());
		}

		FileTools.saveFile(f, temp.toString());
	}
	
	@Override
	public String getVersion() {
		return "Build Id - 205f7eb";
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
