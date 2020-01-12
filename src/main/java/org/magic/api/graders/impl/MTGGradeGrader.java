package org.magic.api.graders.impl;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

public class MTGGradeGrader extends AbstractGradersProvider {

	
	public static void main(String[] args) throws IOException {
		new MTGGradeGrader().loadGrading("1064241");
	}
	
	@Override
	public String getWebSite() {
		return "https://www.mtggrade.com";
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		
		
		Document d = RequestBuilder.build().method(METHOD.GET)
				   .setClient(URLTools.newClient())
				   .url(getWebSite()+"/produit/"+identifier)
				   .toHtml();
		
		Elements trs = d.select("table.table-product tr");
		
		if(trs.isEmpty())
			return null;
		
		
		
		Grading grad = new Grading();
				grad.setGraderName(getName());
				grad.setNumberID(identifier);
		
		trs.remove(0);
		
		logger.debug("found " + trs.text());
		
		grad.setGradeNote(Double.parseDouble(trs.select("td").get(3).text()));
		
		
		return grad;
	}

	@Override
	public String getName() {
		return "MTGGrade";
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public void initDefault() {
		setProperty("EMAIL", "");
		setProperty("PASS", "");
	}
}
