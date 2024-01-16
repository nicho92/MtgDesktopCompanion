package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;

public class ZCartProductsExport extends AbstractFormattedFileCardExport {

	
	
	private static final String COLUMNS = "\"NAME\",\"SLUG\",\"ACTIVE\",\"CATEGORIES\",\"GTIN\",\"GTIN_TYPE\",\"MODEL_NUMBER\",\"BRAND\",\"MPN\",\"DESCRIPTION\",\"MANUFACTURER\",\"ORIGIN_COUNTRY\",\"REQUIRES_SHIPPING\",\"MINIMUM_PRICE\",\"MAXIMUM_PRICE\",\"IMAGE_LINK\",\"TAGS\"";
	
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		var list = new ArrayList<MagicCardStock>();
		
		
		matches(content, true, aliases.getRegexFor(this,"default")).forEach(m->{
			var stock = MTGControler.getInstance().getDefaultStock();
			var mc = parseMatcherWithGroup(m, 3, 2,false, FORMAT_SEARCH.ID, FORMAT_SEARCH.NUMBER);
			if(mc!=null){
				stock.setProduct(mc);
				stock.setFoil(m.group(5).equalsIgnoreCase("True"));
				stock.setQte(Integer.parseInt(m.group(6)));
				stock.setLanguage(m.group(4));
				list.add(stock);
				notify(mc);
			}
		});
		
		return list;
	}
	
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
			var temp = new StringBuilder(COLUMNS);
			temp.append(System.lineSeparator());
			
			stock.forEach(st->{
				temp.append("\"").append(st.getProduct().getName()).append("\"").append(getSeparator());
				temp.append("\"").append(slug(st.getProduct())).append("\"").append(getSeparator());
				temp.append(getString("ACTIVE")).append(getSeparator());
				temp.append("\"").append(getString("CATEGORY_CARD_ID")).append("\"").append(getSeparator());
				temp.append("\"").append("").append("\"").append(getSeparator());
				temp.append("\"").append("").append("\"").append(getSeparator());
				temp.append("\"").append(st.getProduct().getMkmId()).append("\"").append(getSeparator());
				temp.append("\"").append("").append("\"").append(getSeparator());
				temp.append("\"").append("").append("\"").append(getSeparator());
				temp.append("\"").append(describe(st)).append("\"").append(getSeparator());
				temp.append("\"").append("Cardmarket").append("\"").append(getSeparator());
				temp.append("\"").append("").append("\"").append(getSeparator());
				temp.append("\"").append(st.getProduct().isOnlineOnly()?"FALSE":"TRUE").append("\"").append(getSeparator());
				temp.append("\"").append(st.getPrice()).append("\"").append(getSeparator());
				temp.append("\"").append("").append("\"").append(getSeparator());
				temp.append("\"").append(st.getProduct().getUrl()).append("\"").append(getSeparator());
				temp.append(System.lineSeparator());
				notify(st.getProduct());
			});
			FileTools.saveFile(f, temp.toString());
	}
	
	
	private String describe(MagicCardStock st) {
		
		return st.getComment();
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		var m  = super.getDefaultAttributes();
		m.put("ACTIVE", "TRUE");
		m.put("CATEGORY_CARD_ID", "1");
		return m;
	}
	
	

	private String slug(MagicCard product) {
		
		return product.getName().replace(" ", "-").replace("'", "-")+"-"+product.getCurrentSet().getNumber();
		
	}


	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		var d = new MagicDeck();
		d.setName(name);

		for(MagicCardStock st : importStock(f))
			d.getMain().put(st.getProduct(), st.getQte());

		return d;
	}

	@Override
	public String getName() {
		return "ZCart";
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

	@Override
	public String getFileExtension() {
		return ".csv";
	}
}
