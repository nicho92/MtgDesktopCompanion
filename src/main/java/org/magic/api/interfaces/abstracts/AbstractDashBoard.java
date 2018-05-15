package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.magic.api.beans.CardShake;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.MTGConstants;

public abstract class AbstractDashBoard extends AbstractMTGPlugin implements MTGDashBoard {

	protected Map<String, List<CardShake>> cacheEditions;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DASHBOARD;
	}

	public AbstractDashBoard() {
		super();
		cacheEditions=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		
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
