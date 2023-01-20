package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.tools.IDGenerator;
import org.magic.services.tools.TCache;

public class ClipBoardExport extends AbstractCardExport {

	private TCache<MagicCard> clipboard;


	public ClipBoardExport() {
		clipboard = new TCache<>(getName());
	}

	@Override
	public String getFileExtension() {
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
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		for(MagicCard mc : deck.getMainAsList())
			clipboard.put(IDGenerator.generate(mc), mc);

	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		var d = new MagicDeck();
		d.setName("ClipBoard");

		for(MagicCard mc : clipboard.values())
		{
			d.add(mc);
			notify(mc);
		}

		if(getBoolean("CLEAN_AFTER_IMPORT"))
			clipboard.clean();

		return d;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("CLEAN_AFTER_IMPORT","true");
	}


	@Override
	public String getName() {
		return "ClipBoard";
	}



}
