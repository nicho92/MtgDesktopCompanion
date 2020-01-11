package org.magic.api.graders.impl;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class PSAGrader extends AbstractGradersProvider {

	@Override
	public Grading loadGrading(String identifier) {
	
		String url="https://www.psacard.com/cert/"+identifier;
		
		return null;
	}

	@Override
	public String getName() {
		return "PSA";
	}

}
