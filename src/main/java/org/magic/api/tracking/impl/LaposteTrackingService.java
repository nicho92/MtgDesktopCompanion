package org.magic.api.tracking.impl;

import java.io.IOException;

import org.magic.api.beans.Tracking;
import org.magic.api.beans.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

public class LaposteTrackingService extends AbstractTrackingService{

	private static final String SHIPMENT = "shipment";
	private final String baseUri="https://api.laposte.fr/suivi/"+getVersion()+"/idships";
	
	@Override
	public String getVersion() {
		return "v2";
	}
	
	@Override
	public Tracking track(String number) throws IOException {
		
		var e = RequestBuilder.build().setClient(URLTools.newClient()).url(baseUri+"/"+number +"?"+getString("LANG")).method(METHOD.GET)
				.addHeader("X-Okapi-Key", getString("OKAPI-KEY"))
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON).toJson().getAsJsonObject();
		
		var t = new Tracking(number);
				 t.setFinished(e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("isFinal").getAsBoolean());
				 t.setProductName(e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("product").getAsString());
		
				 e.getAsJsonObject().get(SHIPMENT).getAsJsonObject().get("event").getAsJsonArray().forEach(je->{
					 t.addStep(new TrackingStep(UITools.parseGMTDate(je.getAsJsonObject().get("date").getAsString()), je.getAsJsonObject().get("label").getAsString(), je.getAsJsonObject().get("code").getAsString()));
					 
					 if(je.getAsJsonObject().get("code").getAsString().equals("PC1"))
					 {
						 t.setDeliveryDate(UITools.parseGMTDate(je.getAsJsonObject().get("date").getAsString()));
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
	public void initDefault() {
		setProperty("OKAPI-KEY", "");
		setProperty("LANG", "en_EN");
	}
	
	
}
