package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.List;

import org.dajlab.mondialrelayapi.IMondialRelayService;
import org.dajlab.mondialrelayapi.MondialRelayServiceImpl;
import org.dajlab.mondialrelayapi.vo.MRException;
import org.magic.api.beans.shop.Tracking;
import org.magic.api.beans.shop.TrackingStep;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;

public class MondialRelayTrackingService extends AbstractTrackingService {

	@Override
	public Tracking track(String number) throws IOException {
		var track = new Tracking();
		
		try {
			IMondialRelayService service = new MondialRelayServiceImpl(getAuthenticator().get("CODE_ENSEIGNE"), getAuthenticator().get("PRIVATE_KEY"));
			var mrSuivi = service.getSuivi(number);
				track.setNumber(number);
			
			mrSuivi.getEvenements().forEach(ev->{
				var ts = new TrackingStep();
					ts.setDateStep(ev.getDate().getTime());
					ts.setDescriptionStep(ev.getLibelle());
				
				track.getSteps().add(ts);
			});
			return track;
		} catch (MRException e) {
			throw new IOException(e);
		}
		
		
		
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
