package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.shop.Tracking;
import org.magic.api.beans.shop.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.tools.UITools;

public class DHLTrackingService extends AbstractTrackingService {

	private static final String BASEURL="https://api-eu.dhl.com/track/shipments?trackingNumber=";

	@Override
	public Tracking track(String number) throws IOException {

		var t = new Tracking(number);

		var res = RequestBuilder.build().setClient(URLTools.newClient()).url(BASEURL+number).method(METHOD.GET)
				.addHeader("DHL-API-Key", getString("API_KEY"))
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON).toJson().getAsJsonObject().get("shipments").getAsJsonArray();

		var e=res.get(0).getAsJsonObject();


		t.setTrackingUri("https://www.dhl.com/fr-fr/home/tracking/tracking-parcel.html?submit=1&tracking-id="+number+"&language=fr");
		t.setProductName(e.get("service").getAsString());
		t.setFinished(e.get("details").getAsJsonObject().get("proofOfDelivery")!=null);
		e.get("events").getAsJsonArray().forEach(je->t.addStep(new TrackingStep(UITools.parseGMTDate(je.getAsJsonObject().get("timestamp").getAsString()),je.getAsJsonObject().get("description").getAsString(),je.getAsJsonObject().get("statusCode").getAsString())));

		return t;

	}

	@Override
	public String getName() {
		return "DHL";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("API_KEY", "demo-key");
	}
}
