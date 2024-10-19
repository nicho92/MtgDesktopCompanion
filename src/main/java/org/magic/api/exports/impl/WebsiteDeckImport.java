package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.components.deck.DeckSnifferDialog;
import org.magic.services.MTGConstants;

public class WebsiteDeckImport extends AbstractCardExport {



	@Override
	public MODS getMods() {
		return MODS.IMPORT;
	}

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		throw new NotImplementedException("not implemented");
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.ONLINE;
	}

	@Override
	public boolean needFile() {
		return false;
	}

	@Override
	public boolean needDialogForDeck(MODS mod) {
		return true;
	}

	 @Override
	public boolean needDialogForStock(MODS mod) {
		return true;
	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		return importDeckFromFile(null);
	}

	@Override
	public List<MTGCardStock> importStockFromFile(File f) throws IOException {
		var diag = new DeckSnifferDialog();
		diag.setVisible(true);
		var d = diag.getSelectedItem();

		return importFromDeck(d);
	}


	@Override
	public MTGDeck importDeckFromFile(File f) throws IOException {
		var diag = new DeckSnifferDialog();
		diag.setVisible(true);
		return diag.getSelectedItem();

	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		throw new NotImplementedException("not implemented");

	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_WEBSITE_24;
	}

	@Override
	public String getName() {
		return "Website";
	}

}
