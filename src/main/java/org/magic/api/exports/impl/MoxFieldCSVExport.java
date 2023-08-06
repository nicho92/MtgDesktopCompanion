package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.UITools;

public class MoxFieldCSVExport extends AbstractFormattedFileCardExport {

	
	String columns = "\"Count\",\"Tradelist Count\",\"Name\",\"Edition\",\"Condition\",\"Language\",\"Foil\",\"Tags\",\"Last Modified\",\"Collector Number\",\"Alter\",\"Proxy\",\"Purchase Price\""; 
			
			
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
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
		return "MoxField";
	}

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		List<MagicCardStock> list = new ArrayList<>();
		matches(content,true).forEach(m->{

			MagicEdition ed = null;

			try {
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(m.group(4));
			}
			catch(Exception e)
			{
				logger.error("Edition not found for {}",m.group(4));
			}

			String cname = cleanName(m.group(3));

			String number=null;
			try {
				number = m.group(10);
			}
			catch(IndexOutOfBoundsException e)
			{
				//do nothing
			}

			MagicCard mc=null;

			if(number!=null && ed !=null)
			{
				try {
					mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, ed);
				} catch (Exception e) {
					logger.error("no card found with number {}/{}",number,ed);
				}
			}

			if(mc==null)
			{
				try {
					mc = parseMatcherWithGroup(m, 3, 4, true, FORMAT_SEARCH.NAME,FORMAT_SEARCH.NAME);
				} catch (Exception e) {
					logger.error("no card found for {}/{}",cname,ed);
				}
			}

			if(mc!=null) {
				MagicCardStock mcs = MTGControler.getInstance().getDefaultStock();
					   mcs.setQte(Integer.parseInt(m.group(1)));
					   mcs.setProduct(mc);
					   mcs.setCondition(aliases.getReversedConditionFor(this,m.group(5),null));
					   
					   if(!m.group(6).isEmpty())
						   mcs.setLanguage(m.group(6));

					   mcs.setFoil(m.group(7).equals("foil"));
					   mcs.setSigned(m.group(9).equalsIgnoreCase("true"));
					   mcs.setAltered(m.group(11).equalsIgnoreCase("true"));

					   if(!m.group(13).isEmpty())
						   mcs.setPrice(UITools.parseDouble(m.group(13)));

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
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		// TODO Auto-generated method stub
		super.exportStock(stock, f);
	}
	
	
	
	
	public static void main(String[] args) throws IOException, SQLException {
		var f = new File("D:\\Téléchargements\\moxfield_haves_2023-08-06-0909Z.csv");
		
		MTGControler.getInstance().init();
		
		var exp = new MoxFieldCSVExport();
		exp.importStockFromFile(f).forEach(e->{
			System.out.println(e.getQte() + " " + e.getProduct());
		});
		System.exit(0);

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
