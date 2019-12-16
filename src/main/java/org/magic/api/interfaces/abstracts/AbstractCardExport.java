package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public abstract class AbstractCardExport extends AbstractMTGPlugin implements MTGCardsExport {

	public enum MODS {
		EXPORT, IMPORT, BOTH
	}

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
	
	protected String cleanName(String cname) {
		cname = cname.replace("\"","").trim();
		if(cname.indexOf('/') > 1)
			cname=cname.substring(0,cname.indexOf('/')).trim();
		
		return cname;
	}


	public AbstractCardExport() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "exports");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	
	public boolean needDialogGUI() {
		return false;
	}
	
	
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());

		for (MagicCardStock mcs : stock) {
			d.getMap().put(mcs.getMagicCard(), mcs.getQte());
		}
		exportDeck(d, f); 
	}

	protected List<MagicCardStock> importFromDeck(MagicDeck deck) {
		List<MagicCardStock> mcs = new ArrayList<>();

		for (MagicCard mc : deck.getMap().keySet()) {
			MagicCardStock stock = MTGControler.getInstance().getDefaultStock();
			stock.setMagicCard(mc);
			stock.setQte(deck.getMap().get(mc));
			stock.setComment("import from " + deck.getName());
			stock.setIdstock(-1);
			stock.setUpdate(true);
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
		return importDeck(FileUtils.readFileToString(f, MTGConstants.DEFAULT_ENCODING),FilenameUtils.getBaseName(f.getName()));
	}
	
	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		return importStock(FileUtils.readFileToString(f,MTGConstants.DEFAULT_ENCODING));
	}
	
	

}
