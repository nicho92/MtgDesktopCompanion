package org.magic.api.graders.impl;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class MTGGradeGrader extends AbstractGradersProvider {

	@Override
	public Grading loadGrading(String identifier) {
		String url="https://www.mtggrade.com/en/#verifier2";
		
		return null;
	}

	@Override
	public String getName() {
		return "MTGGrade";
	}

	
	@Override
	public void initDefault() {
		setProperty("EMAIL", "");
		setProperty("PASS", "");
	}
}
