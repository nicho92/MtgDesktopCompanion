package org.magic.api.interfaces;

import java.io.IOException;

import org.magic.api.beans.MTGGrading;

public interface MTGGraders extends MTGPlugin {

	public MTGGrading loadGrading(String identifier) throws IOException;
	public String getWebSite();



}
