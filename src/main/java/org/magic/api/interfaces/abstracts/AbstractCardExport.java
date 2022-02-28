package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.MTGExportCategory;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.services.MTGControler;
import org.magic.tools.FileTools;

public abstract class AbstractCardExport extends AbstractMTGPlugin implements MTGCardsExport {
		
	public MODS getMods() {
		return MODS.BOTH;
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.EXPORT;
	}
	
	@Override
	public boolean needFile() {
		return true;
	}
	
	@Override
	public MTGExportCategory getCategory() {
		if(needFile())
			return MTGExportCategory.FILE;
		
		return null;
	}
	
	
	protected String cleanName(String cname) {
		cname = cname.replace("\"","").trim();
		if(cname.indexOf('/') > 1)
			cname=cname.substring(0,cname.indexOf('/')).trim();
		
		if(cname.indexOf('(')>1)
			cname=cname.substring(0,cname.indexOf('(')).trim();
		
		
		
		return cname;
	}
	
	@Override
	public boolean needDialogForDeck(MODS mod) {
		return false;
	}
	
	 @Override
	public boolean needDialogForStock(MODS mod) {
		return false;
	}
	
	
	
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		var d = new MagicDeck();
		d.setName(FilenameUtils.getBaseName(f.getName()));

		for (MagicCardStock mcs : stock) {
			d.getMain().put(mcs.getProduct(), mcs.getQte());
		}
		exportDeck(d, f); 
	}

	protected List<MagicCardStock> importFromDeck(MagicDeck deck) {
		List<MagicCardStock> mcs = new ArrayList<>();

		for (MagicCard mc : deck.getMain().keySet()) {
			MagicCardStock stock = MTGControler.getInstance().getDefaultStock();
			stock.setProduct(mc);
			stock.setQte(deck.getMain().get(mc));
			stock.setComment("import from " + deck.getName());
			stock.setId(-1);
			stock.setUpdated(true);
			mcs.add(stock);
		}
		return mcs;
	}


	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		return importFromDeck(importDeck(content, "defaultImport from " + getName()));
	}
	
	
	@Override
	public MagicDeck importDeckFromFile(File f) throws IOException {
		return importDeck(FileTools.readFile(f),FilenameUtils.getBaseName(f.getName()));
	}
	
	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		return importStock(FileTools.readFile(f));
	}
	
	

}
