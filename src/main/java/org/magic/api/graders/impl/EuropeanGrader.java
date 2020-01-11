package org.magic.api.graders.impl;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class EuropeanGrader extends AbstractGradersProvider {

	@Override
	public Grading loadGrading(String identifier) {
		
		String url="https://www.europeangrading.com/es/card-verifier.html?certificate="+identifier;
		
		
		return null;
	}

	@Override
	public String getName() {
		return "European Grading";
	}

}
