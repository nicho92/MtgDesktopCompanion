package org.beta;

import java.io.IOException;
import java.net.URL;

import org.magic.api.beans.Tracking;
import org.magic.api.beans.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

public class LaposteTrackingService extends AbstractTrackingService{

	private final String baseUri="https://api.laposte.fr/suivi/"+getVersion()+"/idships";
	
	
	public static void main(String[] args) throws IOException {
		 System.out.println(new LaposteTrackingService().track("6T11111111110"));
		
	}

	
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
				 t.setFinished(e.getAsJsonObject().get("shipment").getAsJsonObject().get("isFinal").getAsBoolean());
				 t.setProductName(e.getAsJsonObject().get("shipment").getAsJsonObject().get("product").getAsString());
		
				 e.getAsJsonObject().get("shipment").getAsJsonObject().get("event").getAsJsonArray().forEach(je->{
					 t.addStep(new TrackingStep(UITools.parseGMTDate(je.getAsJsonObject().get("date").getAsString()), je.getAsJsonObject().get("label").getAsString(), je.getAsJsonObject().get("code").getAsString()));
				 });
		
		logger.debug(e);
		return t;
	}

	@Override
	public String getName() {
		return "La Poste";
	}

	@Override
	public URL trackUriFor(String number) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void initDefault() {
		setProperty("OKAPI-KEY", "");
		setProperty("LANG", "en_EN");
	}
	
	
}
