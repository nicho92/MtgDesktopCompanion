package org.magic.api.graders.impl;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;

public class MTGGradeGrader extends AbstractGradersProvider {

	
	@Override
	public String getWebSite() {
		return "https://www.mtggrade.com";
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		
		String url=getWebSite()+"/produit/"+identifier;
		
		
		Document d = RequestBuilder.build().method(METHOD.GET)
				   .setClient(URLTools.newClient())
				   .url(url)
				   .toHtml();
		
		Elements trs = d.select("table.table-product tr");
		
		if(trs.isEmpty())
			return null;
		
		
		
		var grad = new Grading();
				grad.setGraderName(getName());
				grad.setNumberID(identifier);
				grad.setUrlInfo(url);
				
		trs.remove(0);
		
		logger.debug("found " + trs.text());
		
		grad.setGradeNote(Double.parseDouble(trs.select("td").get(3).text()));
		
		
		return grad;
	}

	@Override
	public String getName() {
		return "MTGGrade";
	}
	
}
