package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.Date;

import org.magic.api.beans.Tracking;
import org.magic.api.beans.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;

public class UPSTrackingService extends AbstractTrackingService {

	@Override
	public Tracking track(String number) throws IOException {
		var t = new Tracking(number);
		
		t.setTrackingUri("https://www.ups.com/track?loc=fr_FR&tracknum="+number);
		t.setProductName("UPS");
		t.addStep(new TrackingStep(new Date(),"no detail",""));
		return t;
	}

	@Override
	public String getName() {
		return "UPS";
	}

}
