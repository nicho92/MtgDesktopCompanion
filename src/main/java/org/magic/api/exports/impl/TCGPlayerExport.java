package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.providers.PluginsAliasesProvider;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TCGPlayerExport extends AbstractFormattedFileCardExport {


	private static final String COLUMNS="Quantity,Name,Simple Name,Set,Card Number,Set Code,External ID,Printing,Condition,Language,Rarity,Product ID,SKU,Price,Price Each";


	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		var d = new MagicDeck();
		d.setName(name);
		d.setDescription("import from " + getName());

		importStock(f).forEach(mcs->d.add(mcs.getProduct()));

		return d;
	}


	@Override
	public void exportStock(List<MagicCardStock> stocks, File f) throws IOException {

		var temp = new StringBuilder();

		temp.append(COLUMNS).append(System.lineSeparator());

		for(MagicCardStock mcs : stocks)
		{
			try {
			temp.append(mcs.getQte()).append(getSeparator());
			temp.append(toFullName(mcs.getProduct())).append(getSeparator());

			if(mcs.getProduct().getName().contains(","))
				temp.append("\"").append(mcs.getProduct()).append("\"");
			else
				temp.append(mcs.getProduct());

			temp.append(getSeparator());

			temp.append(mcs.getProduct().getCurrentSet().getSet()).append(getSeparator());
			temp.append(mcs.getProduct().getCurrentSet().getNumber()).append(getSeparator());
			temp.append(mcs.getProduct().getCurrentSet().getId()).append(getSeparator());
			temp.append(0).append(getSeparator());
			temp.append(mcs.isFoil()?"Foil":"Normal").append(getSeparator());
			temp.append(translate(mcs.getCondition())).append(getSeparator());
			temp.append(mcs.getLanguage()).append(getSeparator());
			temp.append(mcs.getProduct().isLand()?"Land":mcs.getProduct().getRarity().toPrettyString()).append(getSeparator());
			temp.append(mcs.getProduct().getTcgPlayerId()).append(getSeparator());
			temp.append("").append(getSeparator());
			temp.append("$").append(mcs.getQte() * UITools.roundDouble(MTGControler.getInstance().getCurrencyService().convert(MTGControler.getInstance().getCurrencyService().getCurrentCurrency(), Currency.getInstance("USD"), mcs.getPrice()))).append(getSeparator());
			temp.append("$").append(UITools.roundDouble(MTGControler.getInstance().getCurrencyService().convert(MTGControler.getInstance().getCurrencyService().getCurrentCurrency(), Currency.getInstance("USD"), mcs.getPrice())));
			temp.append(System.lineSeparator());
			}
			catch(Exception e)
			{
				logger.error("Error export {}",mcs,e);
			}
		}
		FileTools.saveFile(f, temp.toString());
	}




	private String toFullName(MagicCard magicCard) {
		var temp = new StringBuilder();

			if(magicCard.getName().contains(","))
				temp.append("\"").append(magicCard.getName()).append("\"");
			else
				temp.append(magicCard.getName());

			if(magicCard.isShowCase())
				temp.append(" (Showcase)");
			if(magicCard.isBorderLess())
				temp.append(" (Borderless)");
			if(magicCard.isExtendedArt())
				temp.append(" (Extended Art)");

		return temp.toString();
	}

	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {

		List<MagicCardStock> ret = new ArrayList<>();
		matches(content, true).forEach(m->{
			var st = new MagicCardStock();
			st.setQte(Integer.parseInt(m.group(1)));
			st.setLanguage(m.group(10));
			st.getTiersAppIds().put(getName(), m.group(12));
			st.setPrice(UITools.parseDouble(m.group(14)));
			st.setFoil(m.group(8).equalsIgnoreCase("foil"));
			st.setCondition(translate(m.group(9)));
			var found = false;
				
			try {
				var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(m.group(5), MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(PluginsAliasesProvider.inst().getSetIdFor(this, m.group(6))));
				st.setProduct(mc);
				found = true;
			} catch (Exception e) {
				logger.error("not card found by number for {} for set {} : {}",m.group(5),m.group(6),e.getMessage());
			}


			if(!found)
			{
				try {
					var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(m.group(3).replace("\"", ""), MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(PluginsAliasesProvider.inst().getSetIdFor(this, m.group(6))),true).get(0);
					st.setProduct(mc);
					found = true;
				} catch (Exception e) {
					logger.error("no card found by name for {} for set {} : {}", m.group(3),m.group(6),e.getMessage());
				}
			}


			if(!found)
				logger.error("no card found for {}",m.group());
			else
				ret.add(st);

		});
		return ret;


	}

	private String translate(EnumCondition condition) {
		switch (condition)
		{
		case LIGHTLY_PLAYED:return "Lightly Played";
		case MINT:			return "Mint";
		case NEAR_MINT: 	return "Near Mint";
		case PLAYED:		return "Heavily Played";
		case POOR:			return "Damaged";
		default:			return "Mint";

		}
	}

	private EnumCondition translate(String group) {
		if(group.equalsIgnoreCase("Near Mint"))
			return EnumCondition.NEAR_MINT;
		if(group.equalsIgnoreCase("Lightly Played"))
			return EnumCondition.LIGHTLY_PLAYED;
		if(group.equalsIgnoreCase("Heavily Played"))
			return EnumCondition.PLAYED;
		if(group.equalsIgnoreCase("Damaged"))
			return EnumCondition.POOR;

		return EnumCondition.GOOD;
	}

	@Override
	public String getName() {
		return "TCGPlayer";
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
		return "(\\d+),((?=\\\")\\\".*?\\\"|.*?),((?=\\\")\\\".*?\\\"|.*?),(.*?),(.*?),(.*?),(\\d+),(.*?),(.*?),(.*?),(.*?),(\\d+),(\\d+),\\$?(\\d+\\.(\\d{2}))?,\\$?(\\d+\\.(\\d{2}))";
	}

	@Override
	protected String getSeparator() {
		return ",";
	}

}
