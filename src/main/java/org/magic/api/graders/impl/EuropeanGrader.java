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
		
			
				var item = trs.select("p.header_etiqueta_notas");
				
				if(item!=null)
				{
					grad.setCentering(UITools.parseDouble(item.get(0).text().replace("CENTERING ", "")));
					grad.setCorners(UITools.parseDouble(item.get(1).text().replace("CORNERS ", "")));
					grad.setEdges(UITools.parseDouble(item.get(2).text().replace("EDGES ", "")));
					grad.setSurface(UITools.parseDouble(item.get(3).text().replace("SURFACE ", "")));
					grad.setGradeNote(UITools.parseDouble(trs.select("p.header_etiqueta_grade").first().text()));
				}
				
		return grad;
	}

	@Override
	public String getName() {
		return "European Grading";
	}

}
