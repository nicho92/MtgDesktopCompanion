package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGGraders;
import org.magic.services.MTGConstants;

public abstract class AbstractGradersProvider extends AbstractMTGPlugin implements MTGGraders {

	@Override
	public PLUGINS getType() {
		return PLUGINS.GRADING;
	}

	public AbstractGradersProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "graders");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();

		}
	}

}
