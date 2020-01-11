package org.magic.api.graders.impl;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class PCAGrader extends AbstractGradersProvider {

	@Override
	public Grading loadGrading(String identifier) {
		String url="https://www.pcagrade.com/verif/"+identifier;
		
		return null;
	}

	@Override
	public String getName() {
		return "PCA";
	}

}
