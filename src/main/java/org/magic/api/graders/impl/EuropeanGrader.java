package org.magic.api.graders.impl;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGGrading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class EuropeanGrader extends AbstractGradersProvider {


	private static final String HTML_KEY_TAGS = "p.header_etiqueta_notas";

	@Override
	public String getWebSite() {
		return "https://www.europeangrading.com";
	}

	@Override
	public MTGGrading loadGrading(String identifier) throws IOException {

		String url=getWebSite()+"/en/card-verifier";


		Document d = RequestBuilder.build().get()
										   .setClient(URLTools.newClient())
										   .url(url)
										   .addContent("certificate",identifier).toHtml();

		Elements trs = d.select("table").first().select("thead div.justify-content-center");
		
		if(trs.isEmpty())
			return null;


			var grad = new MTGGrading();
				grad.setGraderName(getName());
				grad.setNumberID(identifier);
				grad.setUrlInfo(url+"?certificate="+identifier);
		
			grad.setCentering(UITools.parseDouble(trs.select(HTML_KEY_TAGS).first().text().replace("CENTERING ", "")));
			
			grad.setCorners(UITools.parseDouble(trs.select(HTML_KEY_TAGS).get(1).text().replace("CORNERS ", "")));
			
			grad.setEdges(UITools.parseDouble(trs.select(HTML_KEY_TAGS).get(2).text().replace("EDGES ", "")));
			
			grad.setSurface(UITools.parseDouble(trs.select(HTML_KEY_TAGS).get(3).text().replace("SURFACE ", "")));
			
			grad.setGradeNote(UITools.parseDouble(trs.select("p.header_etiqueta_grade").first().text()));
		
		return grad;
	}

	@Override
	public String getName() {
		return "European Grading";
	}

}
