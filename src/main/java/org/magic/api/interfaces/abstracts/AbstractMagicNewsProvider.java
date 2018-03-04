package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMagicNewsProvider extends AbstractMTGPlugin implements MTGNewsProvider {

	
	@Override
	public PLUGINS getType() {
		return PLUGINS.NEWS;
	}
	
	
	
	public AbstractMagicNewsProvider() {
		confdir = new File(MTGConstants.CONF_DIR, "news");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}

}
