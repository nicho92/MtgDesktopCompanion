package org.magic.api.graders.impl;

import java.io.IOException;

import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.Grading;
import org.magic.api.interfaces.abstracts.AbstractGradersProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class CardMarketGrading extends AbstractGradersProvider {

	
	private static final String BASE_SITE ="https://guardandgradingsolution.de/wizardsproject_get_grading.php";
	
	
	public static void main(String[] args) throws IOException {
		new CardMarketGrading().loadGrading("5198");
	}
	
	@Override
	public Grading loadGrading(String identifier) throws IOException {
		var g = new Grading();
			 g.setNumberID(identifier);
		
		var d = RequestBuilder.build()
				.setClient(URLTools.newClient())
				.url(BASE_SITE)
				.addContent("post_id","11150")
				.addContent("form_id","f00dd21")
				.addContent("queried_id", "11150")
				.addContent("referer_title","Guard+and+Grading+Population+Report+Upload+-+Gutachten+von+Sammelkarten")
				.addContent("grading_serial", identifier)
				.get()
				.toHtml()
				.select("div.form-container").html().split("<br>");
		
		
		for(var s : d)
		{
			if(s.contains("Surface:"))
				g.setSurface(UITools.parseDouble(s.substring(s.indexOf(" "))));
			
			if(s.contains("Centering:"))
				g.setCentering(UITools.parseDouble(s.substring(s.indexOf(" "))));
			
			if(s.contains("Edges:"))
				g.setEdges(UITools.parseDouble(s.substring(s.indexOf(" "))));
			
			if(s.contains("Corners:"))
				g.setCorners(UITools.parseDouble(s.substring(s.indexOf(" "))));
			
			if(s.contains("Total Grade:"))
				g.setGradeNote(UITools.parseDouble(s.substring(s.indexOf(" "))));
			
		}
		return g;
	}

	@Override
	public String getWebSite() {
		return "https://www.cardmarket.com/fr/Magic/Grading";
	}

	@Override
	public String getName() {
		return MkmConstants.MKM_NAME;
	}

}
