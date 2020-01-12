package org.magic.api.graders.impl;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.URLTools;
import org.magic.tools.RequestBuilder.METHOD;

public class PCAGrader extends AbstractGradersProvider {
	
	@Override
	public String getWebSite() {
		return "https://pcagrade.com";
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		
		String url=getWebSite()+"/resumeBdd/"+identifier+"/1";
		Document d = RequestBuilder.build().method(METHOD.GET)
				   .setClient(URLTools.newClient())
				   .url(url)
				   .toHtml();
		
		
		Elements els = d.select("li.mb-1");
		
		if(els.isEmpty())
		{
			logger.debug(identifier +" is not found for " + getName());
			return null;
		}
		
		els.get(3).select("strong").remove();
		
		Grading g = new Grading();
		g.setGraderName(getName());
		g.setNumberID(identifier);
		g.setGradeNote(Double.parseDouble(els.get(3).text()));
		g.setUrlInfo(url);
		return g;
	}

	@Override
	public String getName() {
		return "PCA";
	}

}
