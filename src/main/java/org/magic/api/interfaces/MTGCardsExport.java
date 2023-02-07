package org.magic.api.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumExportCategory;

public interface MTGCardsExport extends MTGPlugin{

	public enum MODS {
		EXPORT, IMPORT, BOTH
	}

	public String getFileExtension();

	public void exportDeck(MagicDeck deck, File dest) throws IOException;
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException;

	public MagicDeck importDeckFromFile(File f) throws IOException;
	public MagicDeck importDeck(String f, String name) throws IOException;

	public List<MagicCardStock> importStockFromFile(File f) throws IOException;
	public List<MagicCardStock> importStock(String content) throws IOException;

	public boolean needFile();

	public boolean needDialogForDeck(MODS mod);
	public boolean needDialogForStock(MODS mod);

	public EnumExportCategory getCategory();

	public MODS getMods();

}