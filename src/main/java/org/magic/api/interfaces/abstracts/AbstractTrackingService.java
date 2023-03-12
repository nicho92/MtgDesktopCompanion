package org.magic.api.interfaces.abstracts;

import java.io.IOException;

import org.magic.api.beans.shop.Tracking;
import org.magic.api.interfaces.MTGTrackingService;

public abstract class AbstractTrackingService extends AbstractMTGPlugin implements MTGTrackingService {

	@Override
	public PLUGINS getType() {
		return PLUGINS.TRACKING;
	}

	
	@Override
	public Tracking track(String number) throws IOException {
		return track(number,null);
	}


}
