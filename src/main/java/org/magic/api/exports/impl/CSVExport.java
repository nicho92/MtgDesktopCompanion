package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class CSVExport extends AbstractFormattedFileCardExport {

	private static final String EXTRA_PROPERTIES = "extraProperties";
	private String columns="Card Name;Edition;Language;Qte;Condition;Foil;Altered;Signed;Collection;Price;Comment;number";


	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
			List<MTGCardStock> stock = new ArrayList<>();
			for(Matcher part : matches(content, true))
			{
				var mcs = MTGControler.getInstance().getDefaultStock();

				MTGEdition ed = null;

				try {
					ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(part.group(2));
				}
				catch(Exception _)
				{
					logger.error("edition {} is not found",part.group(2));
				}


				MTGCard mc = null;
				try {
					mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber( part.group(12), ed);
				}
				catch(Exception _)
				{
					logger.debug(part.group());
					logger.error("card with number {} is not found",part.group(12));
				}

				if(mc==null)
				{
					try {
						mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( part.group(1), ed, true).get(0);
					}
					catch(Exception _)
					{
						logger.error("card with name {} is not found",part.group(1));
					}
				}

				if(mc!=null) {
					mcs.setProduct(mc);
					mcs.setLanguage(part.group(3));
					mcs.setQte(Integer.parseInt(part.group(4)));
					mcs.setCondition(EnumCondition.valueOf(part.group(5)));
					mcs.setFoil(Boolean.valueOf(part.group(6)));
					mcs.setAltered(Boolean.valueOf(part.group(7)));
					mcs.setSigned(Boolean.valueOf(part.group(8)));
					mcs.setMagicCollection(new MTGCollection(part.group(9)));
					mcs.setPrice(Double.valueOf(part.group(10)));
					mcs.setComment(part.group(11));
					mcs.setId(-1);
					mcs.setUpdated(true);
					stock.add(mcs);
					notify(mcs.getProduct());
				}
			}
			return stock;


	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

		var bw = new StringBuilder();

			bw.append(columns).append(getSeparator());
			bw.append(StringUtils.join(getArray(EXTRA_PROPERTIES),getSeparator())).append(System.lineSeparator());

			for (MTGCardStock mcs : stock)
			{
				bw.append("\"").append(mcs.getProduct().getName()).append("\"").append(getSeparator());
				bw.append(mcs.getProduct().getEdition()).append(getSeparator());
				bw.append(mcs.getLanguage()).append(getSeparator());
				bw.append(mcs.getQte()).append(getSeparator());
				bw.append(mcs.getCondition().name()).append(getSeparator());

				bw.append(mcs.isFoil()).append(getSeparator());
				bw.append(mcs.isAltered()).append(getSeparator());
				bw.append(mcs.isSigned()).append(getSeparator());

				bw.append(mcs.getMagicCollection()).append(getSeparator());
				bw.append(mcs.getValue().doubleValue()).append(getSeparator());
				bw.append(mcs.getComment()).append(getSeparator());
				bw.append(mcs.getProduct().getNumber()).append(getSeparator());

				writeExtraMap(mcs.getProduct(),bw);
				bw.append(System.lineSeparator());

				notify(mcs.getProduct());
			}
			FileTools.saveFile(f, bw.toString());
	}

	@Override
	public void exportDeck(MTGDeck deck, File f) throws IOException {


		var bw = new StringBuilder();
		String[] extraProperties = getArray(EXTRA_PROPERTIES);

		bw.append("Name").append(getSeparator()).append("Edition").append(getSeparator()).append("Qty");

		if(extraProperties.length>0)
			bw.append(getSeparator());

		for (String k : extraProperties)
			bw.append(k).append(getSeparator());

		bw.append(System.lineSeparator());


		for (Entry<MTGCard, Integer> entry : deck.getMain().entrySet())
		{
			bw.append("\"").append(entry.getKey()).append("\"").append(getSeparator());
			bw.append(entry.getKey().getEdition()).append(getSeparator());
			bw.append(entry.getValue()).append(getSeparator());
			writeExtraMap(entry.getKey(),bw);
			bw.append(System.lineSeparator());
		}

		bw.append(System.lineSeparator());

		for (Entry<MTGCard, Integer> entry : deck.getSideBoard().entrySet())
		{
			bw.append("\"").append(entry.getKey()).append("\"").append(getSeparator());
			bw.append(entry.getKey().getEdition()).append(getSeparator());
			bw.append(entry.getValue()).append(getSeparator());
			writeExtraMap(entry.getKey(),bw);
			bw.append(System.lineSeparator());
		}

		FileTools.saveFile(f, bw.toString());

	}


	private void writeExtraMap(MTGCard mc, StringBuilder bw)
	{
			for (String k : getArray(EXTRA_PROPERTIES))
			{
				String val = null;
				try {
					val = BeanUtils.getNestedProperty(mc, k);
				} catch (Exception e) {
					logger.error("Error reading bean", e);
				}

				if (val == null)
					val = "";

				bw.append(val.replaceAll(System.lineSeparator(), "")).append(getSeparator());

			}
			notify(mc);
	}


	@Override
	public MTGDeck importDeck(String content,String n) throws IOException {
		var deck = new MTGDeck();
		deck.setName(n);
		var isSide=false;

		for(String line : UITools.stringLineSplit(content, false)) {

				if(line.isBlank())
				{
					isSide=true;
				}
				else
				{
					String[] part = line.split(getSeparator());
					String name = cleanName(part[0]);
					String qte = part[2];
					String set = part[1];
					MTGEdition ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(set);
					MTGCard mc = null;
					try {
						mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, ed, true).get(0);
					}
					catch(Exception _)
					{
						logger.error("no cards found for {} {} ",name,set);
					}

					if(mc!=null) {
						if(isSide)
							deck.getSideBoard().put(mc, Integer.parseInt(qte));
						else
							deck.getMain().put(mc, Integer.parseInt(qte));

						notify(mc);
					}
				}
		}
		return deck;
	}

	@Override
	public String getStockFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "CSV";
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.get("SEPARATOR").setDefaultValue(";");
		try {
			m.put(EXTRA_PROPERTIES, new MTGProperty("id,cost,supertypes,types,subtypes,layout,showCase,fullArt,extendedArt","choose cards extra attributs you want to export. Separated by comma", BeanUtils.describe(new MTGCard()).keySet().stream().toArray(value -> new String[value])));
		} catch (Exception _) {
			
			m.put(EXTRA_PROPERTIES, new MTGProperty("id,cost,supertypes,types,subtypes,layout,showCase,fullArt,extendedArt","choose cards extra attributs you want to export. Separated by comma"));
		}
		return m;
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
		return getString("SEPARATOR");
	}


}
