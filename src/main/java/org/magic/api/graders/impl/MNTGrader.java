package org.magic.api.graders.impl;

import java.io.IOException;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.tools.UITools;

public class MNTGrader extends AbstractGradersProvider {

	@Override
	public Grading loadGrading(String identifier) throws IOException {

		MTGHttpClient c = URLTools.newClient();

		var el = RequestBuilder.build()
						.setClient(c)
						.url(getWebSite()+"/wp-admin/admin-ajax.php")
						.method(METHOD.POST)
						.addContent("verification_number", identifier)
						.addContent("action", "mnt_verification_lookup")
						.addHeader(URLTools.REFERER, getWebSite()+"/verification-lookup/")
						.addHeader("x-requested-with", "XMLHttpRequest")
						.addHeader(":authority", "mntgrading.com")
						.addHeader(URLTools.ACCEPT, "*/*")
						.addHeader(URLTools.ORIGIN, getWebSite())
						.addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
						.addHeader("sec-fetch-dest", "empty")
						.addHeader("sec-fetch-mode","cors")
						.toJson();

		var grad = new Grading();
		grad.setNumberID(identifier);
		grad.setCentering(el.getAsJsonObject().get("grade_center").getAsJsonObject().get("value").getAsDouble());
		grad.setCorners(el.getAsJsonObject().get("grade_corners").getAsJsonObject().get("value").getAsDouble());
		grad.setEdges(el.getAsJsonObject().get("grade_edges").getAsJsonObject().get("value").getAsDouble());
		grad.setSurface(el.getAsJsonObject().get("grade_surface").getAsJsonObject().get("value").getAsDouble());
		grad.setGradeNote(el.getAsJsonObject().get("final_grade").getAsJsonObject().get("value").getAsDouble());
		grad.setGradeDate(UITools.parseDate(el.getAsJsonObject().get("year").getAsJsonObject().get("value").getAsString(), "YYYY-MM"));
		grad.setGraderName(getName());


		return grad;

	}

	@Override
	public String getWebSite() {
		return "https://mntgrading.com/";
	}

	@Override
	public String getName() {
		return "MNT";
	}

}
