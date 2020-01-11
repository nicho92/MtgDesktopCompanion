package org.magic.api.graders.impl;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

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
		Document d = RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url(url).toHtml();
		logger.debug(d);
		
		//need to use incapsula parsing
		
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
