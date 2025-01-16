package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
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
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	


	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

		var temp = new StringBuilder();
					  temp.append(columns).append("\n");

		stock.forEach(st->{
			temp.append("\"").append(st.getProduct().getName()).append("\"").append(getSeparator());
			temp.append("\"").append(st.getProduct().getEdition().getSet()).append("\"").append(getSeparator());
			temp.append(st.getQte()).append(getSeparator());
			temp.append(st.getValue().doubleValue()).append(getSeparator());
			temp.append( aliases.getConditionFor(this,st.getCondition())).append(getSeparator());
			temp.append(st.getLanguage()).append(getSeparator());
			temp.append(st.isFoil()?"Yes":"No").append(getSeparator());
			temp.append(st.isSigned()?"Yes":"No").append("\n");
			notify(st.getProduct());
		});
		FileTools.saveFile(f, temp.toString());
	}


	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> ret = new ArrayList<>();

		matches(content,true).forEach(m->{
			String cname = cleanName(m.group(1));
			MTGEdition ed = null;
			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(m.group(2));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for {}",m.group(2));
			}

			MTGCard card=null;
			try {
				card = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cname, ed, true).get(0);
			} catch (IOException e) {
				logger.error("no card found for {}/{}",cname,ed);
			}


			if(card!=null)
			{
				Integer qty = Integer.parseInt(m.group(3));
				MTGCardStock st = MTGControler.getInstance().getDefaultStock();
				st.setProduct(card);
				st.setQte(qty);
				st.setPrice(Double.parseDouble(m.group(4)));
				st.setLanguage(m.group(6));
				st.setFoil(m.group(7).equalsIgnoreCase("yes"));
				st.setSigned(m.group(8).equalsIgnoreCase("yes"));
				st.setCondition(aliases.getReversedConditionFor(this,m.group(5),EnumCondition.NEAR_MINT));
				ret.add(st);
				notify(card);
			}
		});

		return ret;
	}


	@Override
	public MTGDeck importDeck(String f,String dname) throws IOException
	{

		var deck = new MTGDeck();
		deck.setName(dname);

		matches(f,true).forEach(m->{
			String cname = cleanName(m.group(1));
			MTGEdition ed = null;
			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetByName(m.group(2));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for {}",m.group(2));
			}

			MTGCard card=null;
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
	protected String getSeparator() {
		return ",";
	}




}
