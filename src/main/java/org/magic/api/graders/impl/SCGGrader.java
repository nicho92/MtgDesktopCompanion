package org.magic.api.graders.impl;

import java.io.IOException;
import java.util.Map.Entry;

import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

import com.google.gson.JsonElement;

public class SCGGrader extends AbstractGradersProvider {

	@Override
	public Grading loadGrading(String identifier) throws IOException {

		var ret = RequestBuilder.build().url("https://api.gosgc.com/v1/pop-report/GetCertAuthCode/"+identifier+"/empty/empty").get().setClient(URLTools.newClient()).toJson().getAsJsonObject();

		Entry<String,JsonElement> entry=ret.entrySet().stream().filter(e->e.getValue().getAsString().equals("1")).findFirst().orElse(null);


		if(entry==null)
			return new Grading();
			

		var grad = new Grading();
		grad.setNumberID(identifier);
		grad.setUrlInfo("https://www.gosgc.com/auth-code");
		grad.setGraderName(getName());
		grad.setGradeDate(UITools.parseGMTDate(ret.get("gradeDate").getAsString()));
		grad.setGradeNote(parse(entry.getKey()));


		return grad;

	}

	private Double parse(String key) {
		switch(key) {
			case "grade10P": return 10.0;
			case "grade10": return 10.0;
			case "grade9pt5":return 9.5;
			case "grade9": return 9.0;
			case "grade8pt5": return 8.5;
			case "grade8": return 8.0;
			case "grade7pt5": return 7.5;
			case "grade7": return 7.0;
			case "grade6pt5": return 6.5;
			case "grade6": return 6.0;
			case "grade5pt5": return 5.5;
			case "grade5": return 5.0;
			case "grade4pt5": return 4.5;
			case "grade4": return 4.0;
			case "grade3pt5": return 3.5;
			case "grade3": return 3.0;
			case "grade2pt5": return 2.5;
			case "grade2": return 2.0;
			case "grade1pt5": return 1.5;
			case "grade1": return 1.0;
			default : return 0.0;
		}

	}


	@Override
	public String getWebSite() {
		return "https://www.gosgc.com/";
	}

	@Override
	public String getName() {
		return "SCG";
	}

}
