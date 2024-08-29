package org.magic.api.graders.impl;

import java.io.IOException;

import org.magic.api.beans.MTGGrading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class ACGGrader extends AbstractGradersProvider {

	
	
	public static void main(String[] args) throws IOException {
		
		var grad = new ACGGrader();
		
		grad.loadGrading("1040123");
		
		
		
	}
	
	
	@Override
	public MTGGrading loadGrading(String identifier) throws IOException {

		var client = URLTools.newClient();
		
		var href = RequestBuilder.build().url(getWebSite()+"/search").addContent("query",identifier).get().setClient(client).toHtml().select("div.search-result-item").first().select("a").attr("href");
		var page = RequestBuilder.build().url(getWebSite()+"/"+href).get().setClient(client).toHtml();
		
		var grad = new MTGGrading();
		grad.setNumberID(identifier);
		grad.setGraderName(getName());
		grad.setGradeNote(UITools.parseDouble(page.select("h1.heading-19").text()));
		grad.setGradeDate(UITools.parseDate(page.select("div.grade-texte").get(1).select("h1").get(1).text(), "dd/MM/yyyy"));
		
		return grad;

	}

	@Override
	public String getWebSite() {
		return "https://www.acggrade.com/";
	}

	@Override
	public String getName() {
		return "ACG";
	}

}
