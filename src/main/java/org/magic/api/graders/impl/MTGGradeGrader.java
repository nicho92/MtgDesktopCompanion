package org.magic.api.graders.impl;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class MTGGradeGrader extends AbstractGradersProvider {

	@Override
	public String getWebSite() {
		return "https://www.mtggrade.com";
	}
	
	@Override
	public Grading loadGrading(String identifier) {
		String url=getWebSite()+"/en/#verifier2";
		
		return null;
	}

	@Override
	public String getName() {
		return "MTGGrade";
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public void initDefault() {
		setProperty("EMAIL", "");
		setProperty("PASS", "");
	}
}
