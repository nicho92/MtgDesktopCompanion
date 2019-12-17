package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

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
import org.magic.services.MTGDeckManager;
import org.magic.tools.UITools;

public class CSVExport extends AbstractFormattedFileCardExport {

	private String columns="Card Name;Edition;Language;Qte;Condition;Foil;Altered;Signed;Collection;Price;Comment";


	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
			List<MagicCardStock> stock = new ArrayList<>();
			for(Matcher part : matches(content, true)) 
			{
				MagicCardStock mcs = MTGControler.getInstance().getDefaultStock();
				
				MagicEdition ed = null;
				
				try {
					ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(part.group(2));
				}
				catch(Exception e)
				{
					logger.error("edition " + part.group(2) + " is not found");
				}
				
				MagicCard mc = null;
				
				try {
					mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( part.group(1), ed, true).get(0);
				}
				catch(Exception e)
				{
					logger.error("card " + part.group(1)+ " is not found");
				}
				
				if(mc!=null) {
				
					mcs.setMagicCard(mc);
					mcs.setLanguage(part.group(3));
					mcs.setQte(Integer.parseInt(part.group(4)));
					mcs.setCondition(EnumCondition.valueOf(part.group(5)));
					mcs.setFoil(Boolean.valueOf(part.group(6)));
					mcs.setAltered(Boolean.valueOf(part.group(7)));
					mcs.setSigned(Boolean.valueOf(part.group(8)));
					mcs.setMagicCollection(new MagicCollection(part.group(9)));
					mcs.setPrice(Double.valueOf(part.group(10)));
					mcs.setComment(part.group(11));
					mcs.setIdstock(-1);
					mcs.setUpdate(true);
					stock.add(mcs);
				}
			}
			return stock;
	

	}


	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

			StringBuilder bw = new StringBuilder();
		
			bw.append(columns);
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

		
		StringBuilder bw = new StringBuilder();
		String[] extraProperties = getArray("extraProperties");
		
		bw.append("Name").append(getSeparator()).append("Edition").append(getSeparator()).append("Qte");
		
		if(extraProperties.length>0)
			bw.append(getSeparator());
		
		
		for (String k : extraProperties)
			bw.append(k).append(getSeparator());

		bw.append(System.lineSeparator());

		
		
		writeMap(deck.getMap(),bw,extraProperties);
		bw.append(System.lineSeparator());
		writeMap(deck.getMapSideBoard(),bw,extraProperties);
		FileUtils.write(f, bw.toString(),MTGConstants.DEFAULT_ENCODING);
		
	}


	private void writeMap(Map<MagicCard, Integer> deck, StringBuilder bw, String[] exportedDeckProperties) {
		
		for (Entry<MagicCard, Integer> entry : deck.entrySet()) {
			bw.append(entry.getKey().getName()).append(getSeparator());
			bw.append(entry.getKey().getCurrentSet()).append(getSeparator());
			bw.append(entry.getValue()).append(getSeparator());
			for (String k : exportedDeckProperties) 
			{
				String val = null;
				try {
					val = BeanUtils.getProperty(entry.getKey(), k);
				} catch (Exception e) {
					logger.error("Error reading bean", e);
				}
				
				if (val == null)
					val = "";
				
				bw.append(val.replaceAll(System.lineSeparator(), "")).append(getSeparator());
			}
			bw.append(System.lineSeparator());
			notify(entry.getKey());
		}
		
	}


	@Override
	public MagicDeck importDeck(String content,String n) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(n);
		boolean isSide=false;
		
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
					MagicEdition ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(set);
					MagicCard mc = null;
					
					try {
					
						mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(name, ed, true).get(0);
					}
					catch(Exception e)
					{
						logger.error("no cards found for " + name +" " + set);
					}
					
					if(mc!=null) {
						
						if(isSide)
							deck.getMapSideBoard().put(mc, Integer.parseInt(qte));
						else
							deck.getMap().put(mc, Integer.parseInt(qte));
						
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
		return "(.*?);(.*?);(.*?);(\\d+);("+StringUtils.join(EnumCondition.values(), "|")+");(true|false);(true|false);(true|false);(.*?);(\\d+(\\.\\d{1,2})?)";
	}

	@Override
	public void initDefault() {
		setProperty("extraProperties", "editions[0].number,cost,supertypes,types,subtypes");
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
