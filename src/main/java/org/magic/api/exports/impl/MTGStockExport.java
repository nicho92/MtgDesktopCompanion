package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;

public class MTGStockExport extends AbstractCardExport {

	@Override
	public String getFileExtension() {
		return ".mtgstock";
	}
	
	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		
		int val=0;
		
		for(MagicCard mc : deck.getMap().keySet())
		{
			String name=mc.getName();
			if(mc.getName().contains("'"))
				name="\""+mc.getName()+"\"";
			
			String line= name+","+mc.getCurrentSet().getId()+"\n";
			FileUtils.write(dest, line, MTGConstants.DEFAULT_ENCODING,true);
			setChanged();
			notifyObservers(val++);
		}
		
	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "MTGStocks";
	}

	
	
	
}
