package org.magic.api.graders.impl;

import java.io.IOException;

import org.magic.api.beans.MTGGrading;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class CCCGrader extends AbstractGradersProvider {

	@Override
	public MTGGrading loadGrading(String identifier) throws IOException {
		
		var onlinepage="https://cccgrading.com/en/ccc-card-verification";
		var d = RequestBuilder.build().url(getWebSite()+"/api/v2/certs/"+identifier)
											.get()
											.setClient(URLTools.newClient())
											.addHeader("Referer", onlinepage)
											.addHeader("Sd-Locale", "en")
											.addHeader("Host", "cccgrading.com")
											.addHeader("Accept", "application/json, text/plain, */*")
											.toJson().getAsJsonObject();
		
		var g = new MTGGrading();
		g.setNumberID(identifier);
		g.setGraderName(getName());
		
		
		final String notationKey = "notation";
		g.setGradeNote(d.get(notationKey).getAsJsonObject().get("cardNote").getAsDouble());
		g.setCentering(d.get(notationKey).getAsJsonObject().get("centerNote").getAsDouble());
		g.setSurface(d.get(notationKey).getAsJsonObject().get("surfaceNote").getAsDouble());
		g.setCorners(d.get(notationKey).getAsJsonObject().get("cornerNote").getAsDouble());
		
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
