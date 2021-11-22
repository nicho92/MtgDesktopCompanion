package org.magic.api.graders.impl;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.URLTools;
import org.magic.tools.UITools;

public class CGCGrader extends AbstractGradersProvider {
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		Document d = URLTools.extractHtml(getWebSite()+"/certlookup/"+identifier+"/");
		var g = new Grading();
		g.setNumberID(identifier);
		g.setGraderName(getName());
		g.setGradeNote(UITools.parseDouble(d.select("div.related-info:contains(Grade) dd").text()));
		
		return g;
	}

	@Override
	public String getWebSite() {
		return "https://www.cgccomics.com";
	}

	@Override
	public String getName() {
		return "CGC";
	}

}
