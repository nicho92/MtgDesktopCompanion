package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.components.dialog.DeckSnifferDialog;
import org.magic.services.MTGConstants;

public class WebsiteDeckImport extends AbstractCardExport {

	
	
	@Override
	public MODS getMods() {
		return MODS.IMPORT;
	}

	@Override
	public String getFileExtension() {
		return ".com";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		throw new NotImplementedException("not implemented");

	}
	
	
	@Override
	public boolean needDialogGUI() {
		return true;
	}
	
	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		return importDeck(null);
	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		DeckSnifferDialog diag = new DeckSnifferDialog();
		diag.setVisible(true);
		return diag.getSelectedDeck();

	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		throw new NotImplementedException("not implemented");

	}

	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		MagicDeck d = importDeck(f);
		List<MagicCardStock> ret = new ArrayList<>();

		for (MagicCard mc : d.getMap().keySet()) {
			MagicCardStock stock = new MagicCardStock();
			stock.setMagicCard(mc);
			stock.setQte(d.getMap().get(mc));
			stock.setUpdate(true);
			stock.setIdstock(-1);
			ret.add(stock);
			notify(stock);
		}
		return ret;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_WEBSITE;
	}

	@Override
	public String getName() {
		return "Website";
	}


}
