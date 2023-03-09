package org.magic.api.tracking.impl;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.shop.Tracking;
import org.magic.api.interfaces.abstracts.AbstractTrackingService;

public class MondialRelayTrackingService extends AbstractTrackingService {

	@Override
	public Tracking track(String number) throws IOException {
		return new Tracking();
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
