package org.magic.api.graders.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jsoup.select.Elements;
import org.magic.api.beans.MTGGrading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.tools.UITools;

public class PCAGrader extends AbstractGradersProvider {

	@Override
	public String getWebSite() {
		return "https://pcagrade.com";
	}

	@Override
	public MTGGrading loadGrading(String identifier) throws IOException {

		var url=getWebSite()+"/resumeBdd/"+identifier+"/1";
		var d = RequestBuilder.build().get()
				   .newClient()
				   .url(url)
				   .toHtml();


		Elements els = d.select("li.mb-1");

		if(els.isEmpty())
		{
			logger.debug("{} is not found for {}",identifier,getName());
			return null;
		}

		els.get(3).select("strong").remove();
		els.get(5).select("strong").remove();

		var g = new MTGGrading();
		g.setGraderName(getName());
		g.setNumberID(identifier);
		g.setGradeNote(UITools.parseDouble(els.get(3).text()));
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

}
