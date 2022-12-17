package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;

public class MTGStockExport extends AbstractFormattedFileCardExport {


	private String columns="\"Card\",\"Set\",\"Quantity\",\"Price\",\"Condition\",\"Language\",\"Foil\",\"Signed\"";

	@Override
	public String getFileExtension() {
		return ".mtgstock";
	}


	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}



	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
	}

	private EnumCondition reverse(String condition)
	{
		switch (condition)
		{
		 case "M": return EnumCondition.MINT;
		 case "NM": return EnumCondition.NEAR_MINT;
		 case "EXC": return EnumCondition.NEAR_MINT;
		 case "GD": return EnumCondition.LIGHTLY_PLAYED;
		 case "FIN": return EnumCondition.PLAYED;
		 case "PR": return EnumCondition.POOR;
		 case "": return null;
		 default : EnumCondition.valueOf(condition.toUpperCase());
		}
		return EnumCondition.valueOf(condition.toUpperCase());
	}

	private String reverse(EnumCondition condition)
	{
		switch (condition)
		{
		 case LIGHTLY_PLAYED: return "GD";
		 case MINT : return "M";
		 case NEAR_MINT : return "NM";
		 case POOR : return "PR";
		 case PLAYED : return "FIN";
		 default : return "NM";
		}

	}


	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

		var temp = new StringBuilder();
					  temp.append(columns).append("\n");

		stock.forEach(st->{
			temp.append("\"").append(st.getProduct().getName()).append("\"").append(getSeparator());
			temp.append("\"").append(st.getProduct().getCurrentSet().getSet()).append("\"").append(getSeparator());
			temp.append(st.getQte()).append(getSeparator());
			temp.append(st.getPrice()).append(getSeparator());
			temp.append(reverse(st.getCondition())).append(getSeparator());
			temp.append(st.getLanguage()).append(getSeparator());
			temp.append(st.isFoil()?"Yes":"No").append(getSeparator());
			temp.append(st.isSigned()?"Yes":"No").append("\n");
			notify(st.getProduct());
		});
		FileTools.saveFile(f, temp.toString());
	}


	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {

		List<MagicCardStock> ret = new ArrayList<>();

		matches(content,true).forEach(m->{
			String cname = cleanName(m.group(1));
			MagicEdition ed = null;
			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(m.group(2));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for {}",m.group(2));
			}

			MagicCard card=null;
			try {
				card = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cname, ed, true).get(0);
			} catch (IOException e) {
				logger.error("no card found for {}/{}",cname,ed);
			}


			if(card!=null)
			{
				Integer qty = Integer.parseInt(m.group(3));
				MagicCardStock st = MTGControler.getInstance().getDefaultStock();
				st.setProduct(card);
				st.setQte(qty);
				st.setPrice(Double.parseDouble(m.group(4)));
				st.setLanguage(m.group(6));
				st.setFoil(m.group(7).equalsIgnoreCase("yes"));
				st.setSigned(m.group(8).equalsIgnoreCase("yes"));
				st.setCondition(reverse(m.group(5)));
				ret.add(st);
				notify(card);
			}
		});

		return ret;
	}


	@Override
	public MagicDeck importDeck(String f,String dname) throws IOException
	{

		var deck = new MagicDeck();
		deck.setName(dname);

		matches(f,true).forEach(m->{
			String cname = cleanName(m.group(1));
			MagicEdition ed = null;
			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(m.group(2));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for {}",m.group(2));
			}

			MagicCard card=null;
			try {
				card = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cname, ed, true).get(0);
			} catch (IOException e) {
				logger.error("no card found for {}/{}",cname,ed);
			}


			if(card!=null)
			{
				Integer qty = Integer.parseInt(m.group(3));
				deck.getMain().put(card, qty);
				notify(card);
			}

		});
		return deck;

	}

	@Override
	public String getName() {
		return "MTGStocks";
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
		return "\"(.*?)\",\\\"(.*?)\\\",(\\d+),(\\d+.\\d+?),(M|NM|EXC|GD|FIN|PR),(.*?),(Yes|No),(Yes|No)";
	}


	@Override
	protected String getSeparator() {
		return ",";
	}




}
