package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.services.MTGConstants;

public abstract class AbstractMTGTextGenerator extends AbstractMTGPlugin implements MTGTextGenerator {

	public AbstractMTGTextGenerator() {
		super();
		
		confdir = new File(MTGConstants.CONF_DIR, "generators");
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
		return PLUGINS.GENERATOR;
	}

	@Override
	public String[] suggestWords(String start)
	{
		return suggestWords(new String[] {start});
	}
	
	
}
