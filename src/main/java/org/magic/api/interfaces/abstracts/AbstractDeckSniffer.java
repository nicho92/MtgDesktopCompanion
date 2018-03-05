package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.services.MTGConstants;

public abstract class AbstractDeckSniffer extends AbstractMTGPlugin implements MTGDeckSniffer {

	public AbstractDeckSniffer() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "decksniffers");
		if(!confdir.exists())
			confdir.mkdir();
		load();
		
		if(!new File(confdir, getName()+".conf").exists()){
			initDefault();
			save();
		} 
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DECKS;
	}
	

}
