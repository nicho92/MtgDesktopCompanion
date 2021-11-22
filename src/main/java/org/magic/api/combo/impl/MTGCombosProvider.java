package org.magic.api.combo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.network.RequestBuilder.METHOD;

public class MTGCombosProvider extends AbstractComboProvider {

	
	private static final String BASE ="https://mtgcombos.com/";
	
	
	@Override
	public List<MTGCombo> loadComboWith(MagicCard mc) {
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
			for(var i=0;i<elsTitles.size();i++)
			{
						try {
							var cbo = new MTGCombo();
							 cbo.setName(elsTitles.get(i).html().replace("Combo Name:", "").trim());
							 cbo.setPlugin(this);
							 cbo.setComment(elsContent.get(i).text().trim());
							 notify(cbo);
							 ret.add(cbo);
						}
						catch(IndexOutOfBoundsException ioobe)
						{
							logger.error("No content at " + i);
						}
			}
		
		return ret;
	}

	@Override
	public String getName() {
		return "MTGCombos";
	}

}
