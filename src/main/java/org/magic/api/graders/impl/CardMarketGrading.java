package org.magic.api.graders.impl;

import java.io.IOException;

import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class CardMarketGrading extends AbstractGradersProvider {

	@Override
	public Grading loadGrading(String identifier) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWebSite() {
		return "https://www.cardmarket.com/fr/Magic/Grading";
	}

	@Override
	public String getName() {
		return MkmConstants.MKM_NAME;
	}

}
