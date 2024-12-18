package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;

public class AetherhubExport extends AbstractFormattedFileCardExport{

	private String columns = "Count,Tradelist Count,Wishlist Count,Name,AetherHub Card Id,Set Code,Card Number,Multiverse Id,Mtgo Id,Scryfall Id,TCGplayer Id,Cardmarket Id,Condition,Language,Foil,Signed,Artist Proof,Altered Art,Misprint";
	private Charset charset = Charset.forName("UTF-16");
	
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	
	
	@Override
	public List<MTGCardStock> importStockFromFile(File f) throws IOException {
		return importStock(FileTools.readFile(f,charset));
	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		var builder = new StringBuilder();
			 builder.append(columns).append(System.lineSeparator());
			 
			 for(var mcs : stock)
			 {
				 builder.append(mcs.getQte()).append(getSeparator());
				 builder.append(0).append(getSeparator());
				 builder.append(0).append(getSeparator());
				 builder.append("\""+mcs.getProduct().getName()+"\"").append(getSeparator());
				 builder.append("").append(getSeparator());
				 builder.append(mcs.getProduct().getEdition().getId()).append(getSeparator());
				 builder.append(mcs.getProduct().getNumber()).append(getSeparator());
				 builder.append(mcs.getProduct().getMultiverseid()).append(getSeparator());
				 builder.append("").append(getSeparator());
				 builder.append(mcs.getProduct().getScryfallId()).append(getSeparator());
				 builder.append(mcs.getProduct().getTcgPlayerId()).append(getSeparator());
				 builder.append(mcs.getProduct().getMkmId()).append(getSeparator());
				 builder.append("Mint").append(getSeparator());
				 builder.append(mcs.getLanguage()).append(getSeparator());
				 builder.append(mcs.isFoil()?"1":"0").append(getSeparator());
				 builder.append(mcs.isSigned()?"1":"0").append(getSeparator());
				 builder.append("0").append(getSeparator());
				 builder.append(mcs.isAltered()?"1":"0").append(getSeparator());
				 builder.append("0").append(System.lineSeparator());
				 
				 
			 }
			 
			 FileTools.saveLargeFile(f, builder.toString(), charset);
			 
			 
			 
	}
	
	
	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		var l = new ArrayList<MTGCardStock>();
		
				matches(content, true).forEach(m->{
					
					try {
						var product = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(m.group(10));
						
						var mcs = MTGControler.getInstance().getDefaultStock();
						
						mcs.setProduct(product);
						mcs.setQte(Integer.parseInt(m.group(1)));
						mcs.setLanguage(m.group(14));
						mcs.setFoil(m.group(15).equals("1"));
						l.add(mcs);
					} catch (IOException e) {
						logger.error("no card found by scryfallId {}",m.group(10));
					}
				});
		return l;
	}
	
	
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "Aetherhub";
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
