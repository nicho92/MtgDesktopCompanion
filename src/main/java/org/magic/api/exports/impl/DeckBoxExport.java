package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class DeckBoxExport extends AbstractFormattedFileCardExport {


	private static final String REGEX = "REGEX";
	private String columns="Count,Tradelist Count,Name,Edition,Edition Code,Card Number,Condition,Language,Foil,Signed,Artist Proof,Altered Art,Misprint,Promo,Textless,My Price\n";



	@Override
	public String getFileExtension() {
		return ".csv";
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
			String name=mc.getProduct().getName();
			if(mc.getProduct().getName().contains(getSeparator()))
				name="\""+mc.getProduct().getName()+"\"";

			line.append(mc.getQte()).append(getSeparator());
			line.append(mc.getQte()).append(getSeparator());
			line.append(name).append(getSeparator());
			line.append(mc.getProduct().getEdition().getSet()).append(getSeparator());
			line.append(mc.getProduct().getEdition().getId()).append(getSeparator());
			line.append(mc.getProduct().getNumber()).append(getSeparator());
			line.append(aliases.getConditionFor(this,mc.getCondition())).append(getSeparator());
			line.append(mc.getLanguage()).append(getSeparator());
			line.append(mc.isFoil()?"foil":"").append(getSeparator());
			line.append(mc.isSigned()?"signed":"").append(getSeparator());
			line.append(getSeparator());
			line.append(mc.isAltered()?"altered":"").append(getSeparator());
			line.append(getSeparator());
			line.append(getSeparator());
			line.append(getSeparator());
			line.append(mc.getValue().doubleValue()).append(System.lineSeparator());
			notify(mc.getProduct());
		}
		FileTools.saveFile(dest, line.toString());
	}


	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> list = new ArrayList<>();

		matches(content,true).forEach(m->{

			MTGEdition ed = null;

			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(m.group(4));
			}
			catch(Exception _)
			{
				logger.error("Edition not found for {}",m.group(4));
			}

			String cname = cleanName(m.group(3));

			String number=null;
			try {
				number = m.group(6);
			}
			catch(IndexOutOfBoundsException _)
			{
				//do nothing
			}

			MTGCard mc=null;

			if(number!=null && ed !=null)
			{
				try {
					mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, ed);
				} catch (Exception _) {
					logger.error("no card found with number {}/{}",number,ed);
				}
			}

			if(mc==null)
			{
				try {
					mc = parseMatcherWithGroup(m, 3, 4, true, FORMAT_SEARCH.NAME,FORMAT_SEARCH.NAME);
				} catch (Exception _) {
					logger.error("no card found for {}/{}",cname,ed);
				}
			}

			if(mc!=null) {
				MTGCardStock mcs = MTGControler.getInstance().getDefaultStock();
					   mcs.setQte(Integer.parseInt(m.group(1)));
					   mcs.setProduct(mc);
					   mcs.setCondition(aliases.getReversedConditionFor(this,m.group(7),null));

					   if(!m.group(8).isEmpty())
						   mcs.setLanguage(m.group(8));

					   mcs.setFoil(m.group(9)!=null);
					   mcs.setSigned(m.group(10)!=null);
					   mcs.setAltered(m.group(12)!=null);

					   if(!m.group(16).isEmpty())
						   mcs.setPrice(UITools.parseDouble(m.group(18)));

			   list.add(mcs);
			}
			else
			{
				logger.error("No cards found for {}",cname);
			}


		});

		return list;
	}


	@Override
	public String getName() {
		return "DeckBox";
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
		if(getString(REGEX).isEmpty())
			setProperty(REGEX,"default");

			return aliases.getRegexFor(this, getString(REGEX));
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
			  m.put(REGEX, new MTGProperty("default", "select regex for export items. You can write your own"));

		return m;
	}


	@Override
	public String getSeparator() {
		return ",";
	}

}
