package org.magic.api.graders.impl;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class PSAGrader extends AbstractGradersProvider {

	
	@Override
	public String getWebSite() {
		return "https://www.psacard.com";
	}
	
	@Override
	public Grading loadGrading(String identifier) {
	
		String url=getWebSite()+"/cert/"+identifier;
		
		return null;
	}

	@Override
	public String getName() {
		return "PSA";
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
}
