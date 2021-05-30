package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGTrackingService;

public abstract class AbstractTrackingService extends AbstractMTGPlugin implements MTGTrackingService {

	@Override
	public PLUGINS getType() {
		return PLUGINS.TRACKING;
	}



}
