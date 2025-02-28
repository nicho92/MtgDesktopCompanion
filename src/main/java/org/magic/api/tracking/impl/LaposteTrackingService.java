package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Tracking;
import org.magic.api.beans.shop.TrackingStep;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.services.network.RequestBuilder;
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


		var e = RequestBuilder.build().setClient(URLTools.newClient()).url(baseUri+"/"+number +"?lang="+getString("LANG")).get()
				.addHeader("X-Okapi-Key", getAuthenticator().get(OKAPI_KEY))
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON).toJson().getAsJsonObject();
		
		
		logger.trace("result from {} request {}",getName(),e);
		
		
		var t = new Tracking(number);
				 t.setFinished(e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("isFinal").getAsBoolean());
				 t.setProductName(e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("product").getAsString());

				 e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("event").getAsJsonArray().forEach(je->{
					 t.addStep(new TrackingStep(UITools.parseGMTDate(je.getAsJsonObject().get("date").getAsString()), je.getAsJsonObject().get("label").getAsString(), je.getAsJsonObject().get("code").getAsString()));

					 if(ArrayUtils.contains(new String[] {"DI1","DI2","DI0"}, je.getAsJsonObject().get("code").getAsString()))
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
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LANG", new MTGProperty("fr_FR","ISO code of the result"));
	}


}
