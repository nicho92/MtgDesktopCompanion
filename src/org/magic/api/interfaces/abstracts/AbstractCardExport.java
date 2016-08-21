package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.CardExporter;

public abstract class AbstractCardExport implements CardExporter {

	public abstract String getFileExtension();
	public abstract void export(MagicDeck deck, File dest) throws IOException ;
	public abstract String getName() ;
	public abstract Icon getIcon() ;
	
	private boolean enabled;
	
	
	@Override
	public MagicDeck importDeck(File f) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable(boolean boolean1) {
		this.enabled=boolean1;

	}

	public boolean isEnable() {
		return enabled;
	}


}
