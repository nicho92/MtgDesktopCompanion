package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.services.MTGConstants;

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

	@Override
	public void export(List<MagicCard> cards, File f) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName("export " + getName() + " cards");
		d.setDescription(getName() + " export to " + f.getName());
		for (MagicCard mc : cards) {
			d.add(mc);
		}
		export(d, f);
	}
	
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());

		for (MagicCardStock mcs : stock) {
			d.getMap().put(mcs.getMagicCard(), mcs.getQte());
		}
		export(d, f);
	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		String content="";
		try {
			content=FileUtils.readFileToString(f, MTGConstants.DEFAULT_ENCODING);
		}catch(Exception e)
		{
			logger.error("error reading " + f.getAbsolutePath());
		}
		
		String name;
		try {
			name=f.getName().substring(0, f.getName().indexOf('.'));
		}catch(Exception e)
		{
			name=f.getName();
		}
		
		return importDeck(content,name);
	}

	
	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		return importFromDeck(importDeck(f));
	}
	
	protected List<MagicCardStock> importFromDeck(MagicDeck deck) {
		List<MagicCardStock> mcs = new ArrayList<>();

		for (MagicCard mc : deck.getMap().keySet()) {
			MagicCardStock stock = new MagicCardStock();
			stock.setMagicCard(mc);
			stock.setQte(deck.getMap().get(mc));
			stock.setComment("import from " + deck.getName());
			stock.setIdstock(-1);
			stock.setUpdate(true);
			mcs.add(stock);
			notify(stock);
		}
		return mcs;
	}
	

}
