package org.magic.api.graders.impl;

import java.io.IOException;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.tools.UITools;

public class PSAGrader extends AbstractGradersProvider {

	@Override
	public String getWebSite() {
		return "https://www.psacard.com";
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		logger.error("Blocked by captcha protection");
		UITools.browse(getWebSite()+"/cert/"+identifier);
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
