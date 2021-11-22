package org.magic.api.graders.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.network.RequestBuilder.METHOD;

public class EuropeanGrader extends AbstractGradersProvider {

	
	@Override
	public String getWebSite() {
		return "https://www.europeangrading.com";
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		
		String url=getWebSite()+"/en/card-verifier.html";
		
		
		Document d = RequestBuilder.build().method(METHOD.GET)
										   .setClient(URLTools.newClient())
										   .url(url)
										   .addContent("certificate",identifier).toHtml();
		
		Elements trs = d.select("table.center tr");
		
		if(trs.isEmpty())
			return null;
		
		
			var grad = new Grading();
				grad.setGraderName(getName());
				grad.setNumberID(identifier);
				grad.setUrlInfo(url+"?certificate="+identifier);
				
		logger.debug("Found " + trs.text());
		
		trs.forEach(tr->{
			
			if(tr.text().startsWith("Centring"))
				grad.setCentering(Double.parseDouble(tr.text().replace("Centring grade : ","").replace(',', '.').trim()));
			
			if(tr.text().startsWith("Corner"))
				grad.setCorners(Double.parseDouble(tr.text().replace("Corner grade : ","").replace(',', '.').trim()));
			
			if(tr.text().startsWith("Edges"))
				grad.setEdges(Double.parseDouble(tr.text().replace("Edges grade : ","").replace(',', '.').trim()));
			
			if(tr.text().startsWith("Surface"))
				grad.setSurface(Double.parseDouble(tr.text().replace("Surface grade : ","").replace(',', '.').trim()));
			
			if(tr.text().startsWith("Final"))
				grad.setGradeNote(Double.parseDouble(tr.text().replace("Final grade : ","").replace(',', '.').trim()));
			
			if(tr.text().startsWith("Grading date"))
			{
				try {
					grad.setGradeDate(new SimpleDateFormat("dd/MM/yyyy").parse(tr.text().replace("Grading date : ","").replace(',', '.').trim()));
				} catch (ParseException e) {
					logger.error(e);
				}
			}
				
			
		});
		return grad;
	}

	@Override
	public String getName() {
		return "European Grading";
	}

}
