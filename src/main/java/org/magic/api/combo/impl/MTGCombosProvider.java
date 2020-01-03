package org.magic.api.combo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

public class MTGCombosProvider extends AbstractComboProvider {

	
	private static final String BASE ="https://mtgcombos.com/";
	
	@Override
	public List<MTGCombo> getComboWith(MagicCard mc) {
		List<MTGCombo> ret = new ArrayList<>();
		
		
			Document d;
			try {
				d = RequestBuilder.build().url(BASE+"index.php").setClient(URLTools.newClient()).method(METHOD.POST).addContent("search", mc.getName()).addContent("submit", "Search >").toHtml();
			} catch (IOException e) {
				logger.error("Error loading " + BASE,e);
				return ret;
			}
			
			Elements elsTitles = d.select("td span.text15");
			Elements elsContent = d.select("td[bgcolor=#CFDEDA]");
			
			for(int i=0;i<elsTitles.size();i++)
			{
				MTGCombo cbo = new MTGCombo();
						 cbo.setName(elsTitles.get(i).html().replace("Combo Name:", "").trim());
						try {
						 cbo.setComment(elsContent.get(i).text().trim());
						}
						catch(IndexOutOfBoundsException ioobe)
						{
							logger.error("No content at " + i);
						}
				ret.add(cbo);
			}
		
		return ret;
	}

	@Override
	public String getName() {
		return "MTGCombos";
	}

}
