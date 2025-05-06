package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TopDeckedExport extends AbstractFormattedFileCardExport {

	
	private static final String DEFAULT_CONDITION = "DEFAULT_CONDITION";
	private static final String DEFAULT_COLLECTION = "DEFAULT_COLLECTION";
	private static final String COLUMNS = "QUANTITY,\"NAME\",SETCODE,\"SETNAME\",FOIL,PURCHASE PRICE,RARITY,ID";
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		
		var builder = new StringBuilder(COLUMNS);
			 builder.append(System.lineSeparator());
			 
		for(var mcs : stock)
		{
			builder.append(mcs.getQte()).append(getSeparator());
			builder.append(commated(mcs.getProduct().getName())).append(getSeparator());
			builder.append(mcs.getProduct().getEdition().getId()).append(getSeparator());
			builder.append("\"").append(mcs.getProduct().getEdition().getSet()).append("\"").append(getSeparator());
			builder.append(mcs.isFoil()?"foil":"").append(getSeparator());
			builder.append(mcs.getValue().doubleValue()).append(getSeparator());
			builder.append(mcs.getProduct().getRarity().toPrettyString()).append(getSeparator());
			builder.append(mcs.getProduct().getScryfallId()).append(getSeparator());
			builder.append(System.lineSeparator());
			notify(mcs.getProduct());
		}
			 
		FileTools.saveFile(f, builder.toString());
	}
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		
		var ret = new ArrayList<MTGCardStock>();
		
		
		matches(content, true).forEach(m->{

			var mcs = new MTGCardStock();
			
			try {
				var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(m.group(9));
				mcs.setProduct(mc);
				mcs.setQte(Integer.parseInt(m.group(1)));				
				mcs.setFoil("foil".equals(m.group(6)));
				mcs.setPrice(UITools.parseDouble(m.group(7)));
				
				if(!getString(DEFAULT_COLLECTION).isEmpty())
					mcs.setMagicCollection(new MTGCollection(getString(DEFAULT_COLLECTION)));
					
				if(!getString(DEFAULT_CONDITION).isEmpty())
					mcs.setCondition(EnumCondition.valueOf(getString(DEFAULT_CONDITION)));
				
				
				notify(mc);
				
			ret.add(mcs);
			
			} catch (IOException _) {
				logger.error("can't find card with scryfallID = {}",m.group(9));
			}
		});
		return ret;
	}
	
	@Override
	protected String getStringPattern() {
		return aliases.getRegexFor(this, "collection");
	}
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of(DEFAULT_COLLECTION, new MTGProperty(MTGConstants.DEFAULT_COLLECTIONS_NAMES[0], "Default Collection to bind imported stock item"),
							DEFAULT_CONDITION,new MTGProperty(EnumCondition.values()[0].name(), "Default condition to apply to imported stock item", Arrays.stream(EnumCondition.values()).map(Enum::name).toList().toArray(new String[0]))
				);
	}

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "Top Decked";
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
