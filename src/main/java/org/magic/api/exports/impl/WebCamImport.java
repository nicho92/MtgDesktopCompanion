package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.components.dialog.WebcamCardImportDialog;
import org.magic.services.MTGControler;

public class WebCamImport extends AbstractCardExport {


	@Override
	public MODS getMods() {
		return MODS.IMPORT;
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
	public EnumExportCategory getCategory() {
		return EnumExportCategory.MANUAL;
	}

	@Override
	public String getFileExtension() {
		return ".dat";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		throw new NotImplementedException("Not Implemented");

	}

	@Override
	public List<MTGCardStock> importStockFromFile(File f) throws IOException {

		var c = new WebcamCardImportDialog();
		c.setVisible(true);

		return c.getFindedCards().stream().map(card->{
			MTGCardStock st = MTGControler.getInstance().getDefaultStock();
			st.setProduct(card);
			return st;
		}).toList();
	}


	@Override
	public String getName() {
		return "Webcam";
	}

	@Override
	public MTGDeck importDeckFromFile(File f) throws IOException {
		var c = new WebcamCardImportDialog();
		c.setVisible(true);
		return c.getSelectedDeck();
	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		return importDeckFromFile(null);
	}

}
