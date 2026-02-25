package org.magic.api.graders.impl;

import java.io.IOException;

import org.magic.api.beans.MTGGrading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;

public class MTGGradeGrader extends AbstractGradersProvider {


	@Override
	public String getWebSite() {
		return "https://www.mtggrade.com";
	}

	@Override
	public MTGGrading loadGrading(String identifier) throws IOException {

		var url=getWebSite()+"/produit/"+identifier;


		var d = RequestBuilder.build().get()
				   .newClient()
				   .url(url)
				   .toHtml();

		var trs = d.select("table.table-product tr");

		if(trs.isEmpty())
			return null;



		var grad = new MTGGrading();
				grad.setGraderName(getName());
				grad.setNumberID(identifier);
				grad.setUrlInfo(url);

		trs.remove(0);

		grad.setGradeNote(Double.parseDouble(trs.select("td").get(3).text()));


		return grad;
	}

	@Override
	public String getName() {
		return "MTGGrade";
	}

}
