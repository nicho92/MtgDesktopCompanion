package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.Date;

import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Tracking;
import org.magic.api.beans.shop.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;

public class UPSTrackingService extends AbstractTrackingService {

	@Override
	public Tracking track(String number, Contact c) throws IOException {
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
