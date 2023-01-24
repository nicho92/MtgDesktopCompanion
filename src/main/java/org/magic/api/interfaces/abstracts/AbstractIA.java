package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGIA;

public abstract class AbstractIA extends AbstractMTGPlugin implements MTGIA {

	@Override
	public PLUGINS getType() {
		return PLUGINS.IA;
	}



}
