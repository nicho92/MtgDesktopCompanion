package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.components.dialog.ManualImportDialog;
import org.magic.services.MTGConstants;

public class ManualImportExport extends AbstractCardExport {

	@Override
	public MODS getMods() {
		return MODS.IMPORT;
	}

	@Override
	public String getFileExtension() {
		return ".man";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		throw new NotImplementedException("not implemented");

	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.MANUAL;
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
	public MagicDeck importDeck(String name,String f) throws IOException {
		return importDeckFromFile(null);

	}

	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		var diag = new ManualImportDialog();
		diag.setVisible(true);
		var d =  diag.getSelectedDeck();
		return importFromDeck(d);
	}


	@Override
	public MagicDeck importDeckFromFile(File f) throws IOException {
		var diag = new ManualImportDialog();
		diag.setVisible(true);
		return diag.getSelectedDeck();

	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		throw new NotImplementedException("not implemented");

	}



	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_MANUAL;
	}

	@Override
	public String getName() {
		return "Manual";
	}



}
