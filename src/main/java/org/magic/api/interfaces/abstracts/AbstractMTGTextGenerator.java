package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGTextGenerator;

public abstract class AbstractMTGTextGenerator extends AbstractMTGPlugin implements MTGTextGenerator {


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
