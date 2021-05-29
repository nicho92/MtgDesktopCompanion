package org.beta;

import java.io.IOException;
import java.net.URL;

import org.magic.api.beans.Tracking;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

public class LaposteTrackingService extends AbstractTrackingService{

	private String API_URL="https://api.laposte.fr/suivi/"+getVersion()+"/idships";
	
	
	public static void main(String[] args) throws IOException {
		 new LaposteTrackingService().track("ZZ111111110NZ");
		
	}

	
	@Override
	public String getVersion() {
		return "v2";
	}
	
	@Override
	public Tracking track(String number) throws IOException {
		
		var e = RequestBuilder.build().setClient(URLTools.newClient()).url(API_URL+"/"+number +"?"+getString("LANG")).method(METHOD.GET)
				.addHeader("X-Okapi-Key", getString("OKAPI-KEY"))
				.addHeader(URLTools.ACCEPT, URLTools.HEADER_JSON).toJson().getAsJsonObject();
		
		var t = new Tracking();
				 t.setFinished(e.getAsJsonObject().get("shipment").getAsJsonObject().get("isFinal").getAsBoolean());
				 t.setProductName(e.getAsJsonObject().get("shipment").getAsJsonObject().get("product").getAsString());
		
				 e.getAsJsonObject().get("shipment").getAsJsonObject().get("timeline").getAsJsonArray().forEach(je->{
					 
				 });
		
		logger.debug(e);
		return new Tracking();
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
