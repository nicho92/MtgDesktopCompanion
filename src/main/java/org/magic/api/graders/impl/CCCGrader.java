package org.magic.api.graders.impl;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.tools.UITools;

public class CCCGrader extends AbstractGradersProvider {

	
	public static void main(String[] args) throws IOException {
		new CCCGrader().loadGrading("747359539");
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		
		var d = RequestBuilder.build().url(getWebSite()+"/api/v2/certs/"+identifier)
											.method(METHOD.GET)
											.setClient(URLTools.newClient())
											.addHeader("Referer", "https://cccgrading.com/en/ccc-card-verification")
											.addHeader("Sd-Locale", "en")
											.addHeader("Host", "cccgrading.com")
											.addHeader("Accept", "application/json, text/plain, */*")
											.toJson().getAsJsonObject();
		
		var g = new Grading();
		g.setNumberID(identifier);
		g.setGraderName(getName());
		
		
		g.setGradeNote(d.get("cardNote").getAsDouble());
		g.setCentering(d.get("centerNote").getAsDouble());
		g.setSurface(d.get("surfaceNote").getAsDouble());
		g.setGradeDate(UITools.parseGMTDate(d.get("date").getAsString()));
		g.setUrlInfo(onlinepage+"?card-input="+identifier);
		
		
		if(g.getGradeNote()>=9)
			g.setGrade(EnumCondition.MINT);
		else if(g.getGradeNote()>=8.5)
			g.setGrade(EnumCondition.NEAR_MINT);
		else if(g.getGradeNote()>=7)
			g.setGrade(EnumCondition.EXCELLENT);
		else if(g.getGradeNote()>=5)
			g.setGrade(EnumCondition.GOOD);
		else if(g.getGradeNote()>=3)
			g.setGrade(EnumCondition.LIGHTLY_PLAYED);
		else if(g.getGradeNote()>=2)
			g.setGrade(EnumCondition.PLAYED);
		else if(g.getGradeNote()>=0)
			g.setGrade(EnumCondition.POOR);
		
		return g;
	}

	@Override
	public String getWebSite() {
		return "https://cccgrading.com";
	}

	@Override
	public String getName() {
		return "CCC Grading";
	}

}
