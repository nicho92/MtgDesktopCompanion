package org.magic.api.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;

public interface MTGCardsExport extends MTGPlugin{

	public enum MODS {
		EXPORT, IMPORT, BOTH
	}

	public String getFileExtension();

	public void exportDeck(MTGDeck deck, File dest) throws IOException;
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException;

	public MTGDeck importDeckFromFile(File f) throws IOException;
	public MTGDeck importDeck(String f, String name) throws IOException;

	public List<MTGCardStock> importStockFromFile(File f) throws IOException;
	public List<MTGCardStock> importStock(String content) throws IOException;

	public boolean needFile();

	public boolean needDialogForDeck(MODS mod);
	public boolean needDialogForStock(MODS mod);

	public EnumExportCategory getCategory();

	public MODS getMods();

}