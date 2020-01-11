package org.magic.api.graders.impl;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class MTGGradeGrader extends AbstractGradersProvider {

	@Override
	public Grading loadGrading(String identifier) {
		return null;
	}

	@Override
	public String getName() {
		return "MTGGrade";
	}

}
