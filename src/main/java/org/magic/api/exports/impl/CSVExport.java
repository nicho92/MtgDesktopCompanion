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
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class CSVExport extends AbstractFormattedFileCardExport {

	private static final String EnumExtra_PROPERTIES = "extraProperties";
	private String columns="Card Name;Edition;Language;Qte;Condition;Foil;Altered;Signed;Collection;Price;Comment;currentSet.number";


	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
			List<MagicCardStock> stock = new ArrayList<>();
			for(Matcher part : matches(content, true))
			{
				var mcs = MTGControler.getInstance().getDefaultStock();

				MagicEdition ed = null;

				try {
					ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(part.group(2));
				}
				catch(Exception e)
				{
					logger.error("edition {} is not found",part.group(2));
				}


				MagicCard mc = null;
				try {
					mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber( part.group(12), ed);
				}
				catch(Exception e)
				{
					logger.debug(part.group());
					logger.error("card with number {} is not found",part.group(12));
				}

				if(mc==null)
				{
					try {
						mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( part.group(1), ed, true).get(0);
					}
					catch(Exception e)
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
					mcs.setMagicCollection(new MagicCollection(part.group(9)));
					mcs.setPrice(Double.valueOf(part.group(10)));
					mcs.setComment(part.group(11));
					mcs.setId(-1);
					mcs.setUpdated(true);
					stock.add(mcs);
				}
			}
			return stock;


	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

		var bw = new StringBuilder();

			bw.append(columns).append(getSeparator());
			bw.append(StringUtils.join(getArray(EnumExtra_PROPERTIES),getSeparator())).append(System.lineSeparator());

			for (MagicCardStock mcs : stock)
			{
				bw.append("\""+mcs.getProduct().getName()+"\"").append(getSeparator());
				bw.append(mcs.getProduct().getCurrentSet()).append(getSeparator());
				bw.append(mcs.getLanguage()).append(getSeparator());
				bw.append(mcs.getQte()).append(getSeparator());
				bw.append(mcs.getCondition()).append(getSeparator());

				bw.append(mcs.isFoil()).append(getSeparator());
				bw.append(mcs.isAltered()).append(getSeparator());
				bw.append(mcs.isSigned()).append(getSeparator());

				bw.append(mcs.getMagicCollection()).append(getSeparator());
				bw.append(mcs.getPrice()).append(getSeparator());
				bw.append(mcs.getComment()).append(getSeparator());
				bw.append(mcs.getProduct().getCurrentSet().getNumber()).append(getSeparator());

				writeExtraMap(mcs.getProduct(),bw);
				bw.append(System.lineSeparator());

				notify(mcs.getProduct());
			}
			FileTools.saveFile(f, bw.toString());
	}

	@Override
	public void exportDeck(MagicDeck deck, File f) throws IOException {


		var bw = new StringBuilder();
		String[] extraProperties = getArray(EnumExtra_PROPERTIES);

		bw.append("Name").append(getSeparator()).append("Edition").append(getSeparator()).append("Qty");

		if(extraProperties.length>0)
			bw.append(getSeparator());

		for (String k : extraProperties)
			bw.append(k).append(getSeparator());

		bw.append(System.lineSeparator());


		for (Entry<MagicCard, Integer> entry : deck.getMain().entrySet())
		{
			bw.append("\""+entry.getKey()+"\"").append(getSeparator());
			bw.append(entry.getKey().getCurrentSet()).append(getSeparator());
			bw.append(entry.getValue()).append(getSeparator());
			writeExtraMap(entry.getKey(),bw);
			bw.append(System.lineSeparator());
		}

		bw.append(System.lineSeparator());

		for (Entry<MagicCard, Integer> entry : deck.getSideBoard().entrySet())
		{
			bw.append("\""+entry.getKey()+"\"").append(getSeparator());
			bw.append(entry.getKey().getCurrentSet()).append(getSeparator());
			bw.append(entry.getValue()).append(getSeparator());
			writeExtraMap(entry.getKey(),bw);
			bw.append(System.lineSeparator());
		}

		FileTools.saveFile(f, bw.toString());

	}


	private void writeExtraMap(MagicCard mc, StringBuilder bw)
	{
			for (String k : getArray(EnumExtra_PROPERTIES))
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
	public MagicDeck importDeck(String content,String n) throws IOException {
		var deck = new MagicDeck();
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
					MagicEdition ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(set);
					MagicCard mc = null;
					try {
						mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, ed, true).get(0);
					}
					catch(Exception e)
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
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "CSV";
	}

	@Override
	protected String getStringPattern() {
		return "\"(.*?)\";(.*?);(.*?);(\\d+);("+StringUtils.join(EnumCondition.values(), "|")+")?;(true|false);(true|false);(true|false);(.*?);(\\d+.\\d+);(.*?)?;(.*?)?;(.*?)?;";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put(EnumExtra_PROPERTIES, "id,cost,supertypes,types,subtypes,layout,showCase,fullArt,extendedArt");
		m.put("SEPARATOR", ";");

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
