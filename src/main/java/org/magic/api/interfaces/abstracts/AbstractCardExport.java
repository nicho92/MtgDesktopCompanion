package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
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
		int i = 0;
		for (MagicCard mc : cards) {
			d.add(mc);
			setChanged();
			notifyObservers(i++);
		}
		export(d, f);
	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGDesktopCompanionExport.class.getResource("/icons/plugins/"+getName().toLowerCase()+".png"));
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
		}
		return mcs;
	}

}
