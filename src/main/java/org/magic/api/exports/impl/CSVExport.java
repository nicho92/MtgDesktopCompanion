package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class CSVExport extends AbstractFormattedFileCardExport {

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
			List<MagicCardStock> stock = new ArrayList<>();
			for(String line : UITools.stringLineSplit(content, true)) 
			{
				String[] part = line.split(getSeparator());
				MagicCardStock mcs = MTGControler.getInstance().getDefaultStock();
				
				MagicEdition ed = null;
				
				try {
					ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(part[2]);
				}
				catch(Exception e)
				{
					logger.error("edition " + part[2] + " is not found");
				}
				
				MagicCard mc = null;
				
				try {
					mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( part[1], ed, true).get(0);
				}
				catch(Exception e)
				{
					logger.error("card " + part[1]+ " is not found");
				}
				
				if(mc!=null) {
				
					mcs.setMagicCard(mc);
					mcs.setLanguage(part[2]);
					mcs.setQte(Integer.parseInt(part[3]));
					mcs.setCondition(EnumCondition.valueOf(part[4]));
					mcs.setFoil(Boolean.valueOf(part[5]));
					mcs.setAltered(Boolean.valueOf(part[6]));
					mcs.setSigned(Boolean.valueOf(part[7]));
					mcs.setMagicCollection(new MagicCollection(part[8]));
					mcs.setPrice(Double.valueOf(part[9]));
					mcs.setComment(part[10]);
					mcs.setIdstock(-1);
					mcs.setUpdate(true);
					stock.add(mcs);
				}
			}
			return stock;
	

	}

	@Override
	public MagicDeck importDeck(String content,String n) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(n);
		
		for(String line : UITools.stringLineSplit(content, true)) {
				String[] part = line.split(getSeparator());
				String name = cleanName(part[0]);
				String qte = part[1];
				String set = part[2];
				MagicEdition ed = null;
				try {
					ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(set);
				} catch (Exception e) {
					ed = null;
					
				}
				MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(name, ed, true).get(0);
				notify(mc);
				deck.getMap().put(mc, Integer.parseInt(qte));
		}
		return deck;
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

			StringBuilder bw = new StringBuilder();
		
			bw.append("Card Name;Edition;Language;Qte;Condition;Foil;Altered;Signed;Collection;Price;Comment");
			bw.append(System.lineSeparator());
			for (MagicCardStock mcs : stock) 
			{
				bw.append(mcs.getMagicCard().getName()).append(getSeparator());
				bw.append(mcs.getMagicCard().getCurrentSet()).append(getSeparator());
				bw.append(mcs.getLanguage()).append(getSeparator());
				bw.append(mcs.getQte()).append(getSeparator());
				bw.append(mcs.getCondition()).append(getSeparator());

				bw.append(mcs.isFoil()).append(getSeparator());
				bw.append(mcs.isAltered()).append(getSeparator());
				bw.append(mcs.isSigned()).append(getSeparator());

				bw.append(mcs.getMagicCollection()).append(getSeparator());
				bw.append(mcs.getPrice()).append(getSeparator());
				bw.append(mcs.getComment()==null ? ""  :mcs.getComment());
				bw.append(System.lineSeparator());
				notify(mcs.getMagicCard());
			}
			FileUtils.write(f, bw.toString(),MTGConstants.DEFAULT_ENCODING);
	}

	@Override
	public void exportDeck(MagicDeck deck, File f) throws IOException {

		String[] exportedDeckProperties= getArray("exportedDeckProperties");

		StringBuilder bw = new StringBuilder();
		bw.append("Main list\n");

			bw.append("Qte;");
			for (String k : exportedDeckProperties)
				bw.append(k).append(getSeparator());

			bw.append("\n");

			for (MagicCard mc : deck.getMap().keySet()) {
				bw.append(deck.getMap().get(mc) + getSeparator());
				for (String k : exportedDeckProperties) {
					String val = null;
					try {
						val = BeanUtils.getProperty(mc, k);
					} catch (Exception e) {
						logger.error("Error reading bean", e);
					}
					if (val == null)
						val = "";
					bw.append(val.replaceAll("\n", "") + getSeparator());
				}
				bw.append("\n");
				notify(mc);
			}

			bw.append("SideBoard\n");
			bw.append("Qte;");
			for (String k : exportedDeckProperties)
				bw.append(k + ";");

			bw.append("\n");
			for (MagicCard mc : deck.getMapSideBoard().keySet()) {
				bw.append(deck.getMapSideBoard().get(mc) +getSeparator());
				for (String k : exportedDeckProperties) {
					String val = null;
					try {
						val = BeanUtils.getProperty(mc, k);
					} catch (Exception e) {
						logger.error("Error reading bean ", e);
					}
					if (val == null)
						val = "";
					bw.append(val.replaceAll("\n", "") + ";");
				}
				bw.append("\n");
				notify(mc);
			}
			
			FileUtils.write(f, bw.toString(),MTGConstants.DEFAULT_ENCODING);
		
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
		return "(.*?);(.*?);(.*?);(\\d+);("+StringUtils.join(EnumCondition.values(), "|")+");(true|false);(true|false);(true|false);(.*?);(\\d+(\\.\\d{1,2})?)";
	}


	@Override
	public void initDefault() {
		setProperty("exportedDeckProperties", "name,editions[0].id,editions[0].number,cost,supertypes,types,subtypes");
		setProperty("SEPARATOR", ";");

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
