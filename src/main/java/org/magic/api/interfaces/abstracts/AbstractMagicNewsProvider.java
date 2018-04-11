package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractMagicNewsProvider extends AbstractMTGPlugin implements MTGNewsProvider {

	public enum NEWS_TYPE {RSS,TWITTER,FORUM}
	
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.NEWS;
	}
	
	
	
	public AbstractMagicNewsProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "news");
		if(!confdir.exists())
			confdir.mkdir();
		load();
		
		if(!new File(confdir, getName()+".conf").exists()){
			initDefault();
			save();
		} 
	}

}
