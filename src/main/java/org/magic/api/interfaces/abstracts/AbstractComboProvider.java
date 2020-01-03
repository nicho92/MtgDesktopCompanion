package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGComboProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractComboProvider extends AbstractMTGPlugin implements MTGComboProvider {

	@Override
	public PLUGINS getType() {
		return PLUGINS.COMBO;
	}

	
	public AbstractComboProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "combos");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();

		}
		
	}
}
