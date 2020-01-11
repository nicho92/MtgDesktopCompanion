package org.magic.api.graders.impl;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class BeckettGrader extends AbstractGradersProvider{

	@Override
	public Grading loadGrading(String identifier) {
		
		String url = " https://www.beckett.com/grading/card-lookup?item_type=BGS&submit=Submit&&item_id="+identifier;
		
		return null;
	}

	@Override
	public String getName() {
		return "BGS";
	}
	
	
	@Override
	public void initDefault() {
		setProperty("EMAIL", "");
		setProperty("PASS", "");
	}
}
