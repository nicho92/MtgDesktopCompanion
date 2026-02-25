package org.magic.api.tracking.impl;

import java.io.IOException;

import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Tracking;
import org.magic.api.beans.shop.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MondialRelayTrackingService extends AbstractTrackingService {

	@Override
	public Tracking track(String number, Contact c) throws IOException {
		
		if(c==null)
			throw new IOException("Contact is needed");
		
		var track = new Tracking();
		track.setProductName(getName());
		track.setTrackingUri("https://www.mondialrelay.fr/suivi-de-colis/?numeroExpedition="+number+"&codePostal="+c.getZipCode());
		track.setNumber(number);

		var ret=	  RequestBuilder.build().url("https://www.mondialrelay.fr/_mvc/fr-FR/SuiviExpedition/RechercherJsonResponsive").newClient().post()
					.addContent("CodeMarque","")
					.addContent("NumeroExpedition",number)
					.addContent("CodePostal",c.getZipCode())
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
		
		for(var div : doc)
		{
			var date= div.select("div.col-xs-4").get(0).text();
			
			for(var divStep : div.select("div.step-suivi"))
			{
				
				var hour = divStep.select("div.col-xs-4").first();
				if(hour!=null)
				{
					var step = new TrackingStep();
						 step.setDateStep(UITools.parseDate(date + " " + hour.text(),"dd/MM/yyyy hh:mm"));
						 step.setDescriptionStep(divStep.select("div.col-xs-8").text());
						 track.addStep(step);
						 
						 if(step.getDescriptionStep().contains("Colis livré"))
						 {
							 track.setFinished(true);
							 track.setDeliveryDate(step.getDateStep());
						 }
						 
				}
				
				
				
					
			}
			
		}
		
		return track;
		
		
	}

	@Override
	public String getName() {
		return "MondialRelay";
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
}
