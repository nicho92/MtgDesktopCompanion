package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGScript;
import org.magic.services.MTGConstants;

public abstract class AbstractMTGScript extends AbstractMTGPlugin implements MTGScript{

	
	public AbstractMTGScript() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "scripts");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	

	@Override
	public PLUGINS getType() {
		return PLUGINS.SCRIPT;
	}

}
