package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractDashBoard extends AbstractMTGPlugin implements MTGDashBoard {

	public enum FORMAT { STANDARD,LEGACY,VINTAGE,MODERN}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DASHBOARD;
	}
	
		
	public AbstractDashBoard() {
		confdir = new File(MTGConstants.CONF_DIR, "dashboards");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
}
