package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Tracking;
import org.magic.api.beans.shop.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class LaposteTrackingService extends AbstractTrackingService{

	private static final String SHIPMENT = "shipment";
	private final String baseUri="https://api.laposte.fr/suivi/"+getVersion()+"/idships";
	private static final String  OKAPI_KEY = "OKAPI-KEY";


	@Override
	public String getVersion() {
		return "v2";
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(OKAPI_KEY);
	}
	
	
	@Override
	public Tracking track(String number, Contact c) throws IOException {


		if(getAuthenticator().get(OKAPI_KEY).isEmpty())
		{
			throw new IOException("please fill "+OKAPI_KEY+" for " + getName() + " account in config panel");
		}


		var e = RequestBuilder.build().setClient(URLTools.newClient()).url(baseUri+"/"+number +"?"+getString("LANG")).method(METHOD.GET)
				.addHeader("X-Okapi-Key", getAuthenticator().get(OKAPI_KEY))
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON).toJson().getAsJsonObject();
		
		
		logger.trace("result from {} request {}",getName(),e);
		
		
		var t = new Tracking(number);
				 t.setFinished(e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("isFinal").getAsBoolean());
				 t.setProductName(e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("product").getAsString());

				 e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("event").getAsJsonArray().forEach(je->{
					 t.addStep(new TrackingStep(UITools.parseGMTDate(je.getAsJsonObject().get("date").getAsString()), je.getAsJsonObject().get("label").getAsString(), je.getAsJsonObject().get("code").getAsString()));

					 if(je.getAsJsonObject().get("code").getAsString().equals("PC1"))
					 {
						 t.setDeliveryDate(UITools.parseGMTDate(je.getAsJsonObject().get("date").getAsString()));
						 t.setFinished(true);
					 }


				 });

		if(t.getProductName().equalsIgnoreCase("chronopost"))
			t.setTrackingUri("https://www.chronopost.fr/tracking-no-cms/suivi-page?langue=fr&listeNumerosLT="+number);
		else
			t.setTrackingUri("https://www.laposte.fr/outils/suivre-vos-envois?code="+number);

		return t;
	}

	@Override
	public String getName() {
		return "La Poste";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("LANG", "en_EN");
	}


}
