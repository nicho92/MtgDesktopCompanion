package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGShopper;
import org.magic.services.MTGConstants;

public abstract class AbstractMagicShopper extends AbstractMTGPlugin implements MTGShopper {
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.SHOPPER;
	}
	
	public AbstractMagicShopper() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "shoppers");
		if(!confdir.exists())
			confdir.mkdir();
		load();
		
		if(!new File(confdir, getName()+".conf").exists()){
			initDefault();
			save();
		} 
	}
}
