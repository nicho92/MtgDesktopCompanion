package org.magic.api.graders.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

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
		els.get(5).select("strong").remove();
	
		Grading g = new Grading();
		g.setGraderName(getName());
		g.setNumberID(identifier);
		g.setGradeNote(Double.parseDouble(els.get(3).text()));
		try {
			g.setGradeDate(new SimpleDateFormat("yyyy").parse(els.get(5).text()));
		} catch (ParseException e) {
			logger.error(e);
		}
		g.setUrlInfo(url);
		return g;
	}

	@Override
	public String getName() {
		return "PCA";
	}

	public static void main(String[] args) throws IOException {
		new PCAGrader().loadGrading("63101543");
	}
}
