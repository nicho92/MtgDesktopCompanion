package org.magic.api.graders.impl;

import java.io.IOException;

import javax.script.ScriptException;

import org.magic.api.beans.Grading;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.CloudFlareAuthorizer;
import org.magic.services.network.URLTools;
import org.magic.tools.UITools;

public class PSAGrader extends AbstractGradersProvider {

	@Override
	public String getWebSite() {
		return "https://www.psacard.com";
	}

	@Override
	public Grading loadGrading(String identifier) throws IOException {
		var authorizer = new CloudFlareAuthorizer();
    	try {
			var ret = authorizer.getAuthorizationResult(getWebSite()+"/cert/"+identifier);
			var doc = URLTools.toHtml(ret);


			for(var it : doc.select("table tr"))
			{
				if(it.getElementsByTag("th").first().text().equals("Grade"))
				{
					var result = it.getElementsByTag("td").html();

				    var  condition=result.split(" ")[0].trim();
					var note = UITools.parseDouble (result.split(" ")[1].trim());
					logger.debug("result : " + result + " Note =" + note);
					var g = new Grading();
					g.setNumberID(identifier);
					g.setUrlInfo(getWebSite()+"/cert/"+identifier);
					g.setGraderName(getName());
					g.setGrade(parseCondition(condition));
					g.setGradeNote(note);
					return g;
				}
			}


		} catch (IOException | ScriptException e) {
			throw new IOException(e);
		}
		return null;
	}

	private EnumCondition parseCondition(String condition) {
		switch (condition)
		{
			case "NM-MT": return EnumCondition.NEAR_MINT;
			case "PR" : return EnumCondition.POOR;
			case "MINT": return EnumCondition.MINT;
			default : logger.warn(condition  + " is unknow"); return EnumCondition.NEAR_MINT;
		}
	}

	@Override
	public String getName() {
		return "PSA";
	}


	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
}
