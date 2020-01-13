package org.magic.api.graders.impl;

import java.io.IOException;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;

public class PSAGrader extends AbstractGradersProvider {

	
	public static void main(String[] args) throws IOException {
		new PSAGrader().loadGrading("15687369");
	}
	
	@Override
	public String getWebSite() {
		return "https://www.psacard.com";
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
	
		String url=getWebSite()+"/cert/"+identifier;
		//need to parsing cloudFlare protection
		throw new IOException("Blocked by CloudFlare Protection");
		
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
