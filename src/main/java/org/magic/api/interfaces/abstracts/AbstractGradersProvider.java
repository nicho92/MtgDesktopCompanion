package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGGraders;

public abstract class AbstractGradersProvider extends AbstractMTGPlugin implements MTGGraders {

	@Override
	public PLUGINS getType() {
		return PLUGINS.GRADING;
	}



}
