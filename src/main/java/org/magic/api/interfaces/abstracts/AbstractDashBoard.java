package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.MTGConstants;

public abstract class AbstractDashBoard extends AbstractMTGPlugin implements MTGDashBoard {

	

	@Override
	public PLUGINS getType() {
		return PLUGINS.DASHBOARD;
	}

	public AbstractDashBoard() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "dashboards");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
}
