package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.shop.Tracking;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;

public class MondialRelayTrackingService extends AbstractTrackingService {

	public static void main(String[] args) throws IOException {
		new MondialRelayTrackingService().track("69390348");
	}
	
	
	@Override
	public Tracking track(String number) throws IOException {
		
		var client = URLTools.newClient();
			  client.doGet("https://www.mondialrelay.fr/suivi-de-colis/");
		
		var ret=	  RequestBuilder.build().url("https://www.mondialrelay.fr/_mvc/fr-FR/SuiviExpedition/RechercherJsonResponsive").setClient(client).method(METHOD.POST)
					.addContent("CodeMarque","")
					.addContent("NumeroExpedition",number)
					.addContent("CodePostal","62620")
					.toJson().getAsJsonObject();
		
		
		if(!ret.get("Success").getAsBoolean())
		{
			try {
				throw new IOException(URLTools.toHtml((ret.get("ModelState").getAsJsonArray().get(0).getAsJsonObject().get("Item2").getAsString())).text());	
			}catch(Exception e)
			{
				throw new IOException("Error " + e.getMessage());
			}
			
		}
		
		var doc = URLTools.toHtml(ret.get("Message").getAsString()).select("div.infos-account");
		
		
		
		
		System.out.println(doc.select("div.col-xs-4"));
		
		
		
		var track = new Tracking();
		
		return track;
		
		
	}

	@Override
	public String getName() {
		return "MondialRelay";
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("CODE_ENSEIGNE","PRIVATE_KEY");
	}
	
}
