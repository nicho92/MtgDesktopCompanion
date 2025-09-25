package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.TCache;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

public class ClipBoardExport extends AbstractCardExport {

	private TCache<MTGCard> clipboard;


	public ClipBoardExport() {
		clipboard = new TCache<>(getName());
	}

	@Override
	public String getStockFileExtension() {
		return "";
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.MANUAL;
	}

	@Override
	public boolean needDialogForDeck(MODS mod) {
		return false;
	}

	@Override
	public boolean needDialogForStock(MODS mod) {
		return false;
	}


	@Override
	public boolean needFile() {
		return false;
	}

	@Override
	public MODS getMods() {
		return MODS.BOTH;
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		for(MTGCard mc : deck.getMainAsList())
			clipboard.put(mc.getScryfallId(), mc);

	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		var d = new MTGDeck();
		d.setName("ClipBoard");

		for(MTGCard mc : clipboard.values())
		{
			d.add(mc);
			notify(mc);
		}

		if(getBoolean("CLEAN_AFTER_IMPORT"))
			clipboard.clean();

		return d;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		
		var m = super.getDefaultAttributes();
		m.put("CLEAN_AFTER_IMPORT", MTGProperty.newBooleanProperty("true"," set to true if you want to empty the clipboard after import"));
		return m;
	}


	@Override
	public String getName() {
		return "ClipBoard";
	}



}
