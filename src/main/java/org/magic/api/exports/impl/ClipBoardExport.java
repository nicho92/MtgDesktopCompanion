package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.tools.IDGenerator;
import org.magic.tools.TCache;

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
	public CATEGORIES getCategory() {
		return CATEGORIES.MANUAL;
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
	public void initDefault() {
		setProperty("CLEAN_AFTER_IMPORT","true");
	}
	

	@Override
	public String getName() {
		return "ClipBoard";
	}


	
}
