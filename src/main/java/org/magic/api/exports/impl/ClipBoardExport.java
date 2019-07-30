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
		clipboard = new TCache<>("clipboard");
	}
	
	@Override
	public String getFileExtension() {
		return "";
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
	public void export(MagicDeck deck, File dest) throws IOException {
		for(MagicCard mc : deck.getAsList())
			clipboard.put(IDGenerator.generate(mc), mc);

	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName("ClipBoard");
		
		for(MagicCard mc : clipboard.values())
			d.add(mc);
		
		
		clipboard.clean();
		
		return d;
		
	}

	@Override
	public String getName() {
		return "ClipBoard";
	}

}
