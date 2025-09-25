package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;

public class ZCartProductsExport extends AbstractFormattedFileCardExport {

	
	
	private static final String COLUMNS = "\"NAME\",\"SLUG\",\"ACTIVE\",\"CATEGORIES\",\"GTIN\",\"GTIN_TYPE\",\"MODEL_NUMBER\",\"BRAND\",\"MPN\",\"DESCRIPTION\",\"MANUFACTURER\",\"ORIGIN_COUNTRY\",\"REQUIRES_SHIPPING\",\"MINIMUM_PRICE\",\"MAXIMUM_PRICE\",\"IMAGE_LINK\",\"TAGS\"";
	
	
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.APPLICATION;
	}
	
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		var list = new ArrayList<MTGCardStock>();
		
		
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
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
			var temp = new StringBuilder(COLUMNS);
			temp.append(System.lineSeparator());
			
			stock.forEach(st->{
				temp.append("\"").append(st.getProduct().getName()).append("\"").append(getSeparator());
				temp.append("\"").append(slug(st.getProduct())).append("\"").append(getSeparator());
				temp.append(getString("ACTIVE").toUpperCase()).append(getSeparator());
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
				temp.append("\"").append(st.getValue().doubleValue()).append("\"").append(getSeparator());
				temp.append("\"").append("").append("\"").append(getSeparator());
				temp.append("\"").append(st.getProduct().getUrl()).append("\"").append(getSeparator());
				temp.append(System.lineSeparator());
				notify(st.getProduct());
			});
			FileTools.saveFile(f, temp.toString());
	}
	
	
	private String describe(MTGCardStock st) {
		
		return st.getComment();
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m  = super.getDefaultAttributes();
		m.put("ACTIVE", MTGProperty.newBooleanProperty("true", "set product active or not"));
		m.put("CATEGORY_CARD_ID", new MTGProperty("1", "set default category"));
		return m;
	}
	
	

	private String slug(MTGCard product) {
		
		return product.getName().replace(" ", "-").replace("'", "-")+"-"+product.getNumber();
		
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
	public String getStockFileExtension() {
		return ".csv";
	}
}
