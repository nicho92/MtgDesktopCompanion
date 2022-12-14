package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.providers.PluginsAliasesProvider;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class DelverLensExport extends AbstractFormattedFileCardExport{


	private static final String REGEX_VAR = "(.*?); (.*?); (.*?).; (.*?); (.*?); (.*?); (.*?); (.*?); (.*?); (.*?); (.*?); (.*?)$";
	private static final String REGEX = "REGEX";
	private String columns= "Name; Edition; Price; Language; Collector's number; Condition; Currency; Edition code; Foil; List name; Quantity; Scryfall ID";

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

		var temp = new StringBuilder(columns);
		temp.append(System.lineSeparator());
		stock.forEach(st->{
			temp.append(st.getProduct().getName()).append(getSeparator());
			temp.append(PluginsAliasesProvider.inst().getSetNameFor(this,st.getProduct().getCurrentSet())).append(getSeparator());
			temp.append(UITools.formatDouble(st.getPrice())).append(getSeparator());
			temp.append(st.getLanguage()).append(getSeparator());
			temp.append(st.getProduct().getCurrentSet().getNumber()).append(getSeparator());
			temp.append(PluginsAliasesProvider.inst().getConditionFor(this,st.getCondition())).append(getSeparator());
			temp.append(MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode()).append(getSeparator());
			temp.append(PluginsAliasesProvider.inst().getSetIdFor(this,st.getProduct().getCurrentSet())).append(getSeparator());
			temp.append(st.isFoil()?"Foil":"").append(getSeparator());
			temp.append(st.getMagicCollection()).append(getSeparator());
			temp.append(st.getQte()).append(getSeparator());
			temp.append(st.getProduct().getScryfallId()).append(getSeparator());
			temp.append(System.lineSeparator());
		});
		FileTools.saveFile(f, temp.toString());
	}


	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		List<MagicCardStock> list = new ArrayList<>();

		matches(content,true).forEach(m->{

			MagicCard mc=null;
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
				st.setFoil((m.group(9)!=null && m.group(9).equalsIgnoreCase("foil")));
				st.setCondition(PluginsAliasesProvider.inst().getReversedConditionFor(this, m.group(6), EnumCondition.NEAR_MINT)  );
				st.setPrice(UITools.parseDouble(m.group(3).trim()));
				list.add(st);
			}
		});

		return list;
	}


	@Override
	public MagicDeck importDeck(String content, String name) throws IOException {
		var d = new MagicDeck();
		d.setName(name);

		for(MagicCardStock st : importStock(content))
			d.getMain().put(st.getProduct(), st.getQte());

		return d;
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

		if(getString(REGEX).isBlank())
			setProperty(REGEX,defaultRegex());

		return getString(REGEX);
	}

	private String defaultRegex() {
		return REGEX_VAR;
	}

	@Override
	protected String getSeparator() {
		return getString("SEPARATOR");
	}

	@Override
	public Map<String, String> getDefaultAttributes() {

		var m = super.getDefaultAttributes();
			 m.put("SEPARATOR", ";");
			m.put(REGEX,defaultRegex());

			return m;
	}


}
