package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MTGSTrackingService;

public abstract class AbstractTrackingService extends AbstractMTGPlugin implements MTGSTrackingService {

	@Override
	public PLUGINS getType() {
		return PLUGINS.TRACKING;
	}



}
