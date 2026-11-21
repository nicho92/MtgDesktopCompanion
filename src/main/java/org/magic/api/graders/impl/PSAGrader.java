package org.magic.api.graders.impl;

import java.io.IOException;

import org.magic.api.beans.MTGGrading;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class PSAGrader extends AbstractGradersProvider {

	@Override
	public String getWebSite() {
		return "https://www.psacard.com";
	}

	@Override
	public MTGGrading loadGrading(String identifier) throws IOException {
		final var doc = URLTools.extractAsHtml(getWebSite() + "/cert/" + identifier);

		for (final var it : doc.select("table tr")) {
			if (it.getElementsByTag("th").first().text().equals("Grade")) {
				final var result = it.getElementsByTag("td").html();

				final var condition = result.split(" ")[0].trim();
				final var note = UITools.parseDouble(result.split(" ")[1].trim());
				logger.debug("result : {} note={}", result, note);
				final var g = new MTGGrading();
				g.setNumberID(identifier);
				g.setUrlInfo(getWebSite() + "/cert/" + identifier);
				g.setGraderName(getName());
				g.setGrade(parseCondition(condition));
				g.setGradeNote(note);
				return g;
			}
		}

		return null;
	}

	private EnumCondition parseCondition(String condition) {
		switch (condition) {
			case "NM-MT" :
				return EnumCondition.NEAR_MINT;
			case "PR" :
				return EnumCondition.POOR;
			case "MINT" :
				return EnumCondition.MINT;
			default :
				logger.warn("{} is unknow", condition);
				return EnumCondition.NEAR_MINT;
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
