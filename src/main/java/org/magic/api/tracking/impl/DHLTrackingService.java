package org.magic.api.tracking.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.magic.api.beans.Tracking;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.tools.RequestBuilder;
import org.magic.tools.URLTools;
import org.magic.tools.RequestBuilder.METHOD;

public class DHLTrackingService extends AbstractTrackingService {

	private final String baseUrl="https://api-eu.dhl.com/track/shipments?trackingNumber=";
	
	
	public static void main(String[] args) throws IOException {
		new DHLTrackingService().track("00340434292135100131");
	}
	
	@Override
	public Tracking track(String number) throws IOException {
		
		var t = new Tracking();
		
		var e = RequestBuilder.build().setClient(URLTools.newClient()).url(baseUrl+number).method(METHOD.GET)
				.addHeader("DHL-API-Key", getString("API_KEY"))
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON).toJson().getAsJsonObject();
		
		logger.debug(e);
		
		return t;
		
	}

	@Override
	public URL trackUriFor(String number) {
		try {
			return new URL("https://www.dhl.com/fr-fr/home/tracking/tracking-parcel.html?submit=1&tracking-id="+number);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return "DHL";
	}

	
	@Override
	public void initDefault() {
		setProperty("API_KEY", "demo-key");
	}
}
