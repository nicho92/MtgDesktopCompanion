package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Tracking;
import org.magic.api.beans.shop.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class DHLTrackingService extends AbstractTrackingService {

	private static final String API_KEY = "API_KEY";
	private static final String BASEURL="https://api-eu.dhl.com/track/shipments?trackingNumber=";

	@Override
	public Tracking track(String number, Contact c) throws IOException {
		
		if(getAuthenticator().get(API_KEY).isEmpty())
			throw new IOException("Authenticator need  to set API_KEY filled");
		
		
		var t = new Tracking(number);

		var res = RequestBuilder.build().newClient().url(BASEURL+number).get()
				.addHeader("DHL-API-Key",getAuthenticator().get(API_KEY))
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON).toJson().getAsJsonObject().get("shipments").getAsJsonArray();

		var e=res.get(0).getAsJsonObject();

		
		logger.trace("result = {}",e);

		t.setTrackingUri("https://www.dhl.com/fr-fr/home/tracking/tracking-parcel.html?submit=1&tracking-id="+number+"&language=en&service=parcel-de");
		t.setProductName(e.get("service").getAsString());
		t.setFinished("delivered".equals(e.get("status").getAsJsonObject().get("statusCode").getAsString()));
		e.get("events").getAsJsonArray().forEach(je->t.addStep(new TrackingStep(UITools.parseGMTDate(je.getAsJsonObject().get("timestamp").getAsString()),je.getAsJsonObject().get("description").getAsString(),je.getAsJsonObject().get("statusCode").getAsString())));

		return t;

	}

	@Override
	public String getName() {
		return "DHL";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(API_KEY);
	}
	
}
