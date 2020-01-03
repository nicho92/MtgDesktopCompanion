package org.magic.api.combo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;
import org.magic.tools.RequestBuilder.METHOD;

public class MagicVilleComboProvider extends AbstractComboProvider {

	
	private static final String BASE_URL="https://www.magic-ville.com/fr/combos/";
	
	public static void main(String[] args) {
		MagicCard mc = new MagicCard();
		mc.setName("Black Lotus");
		
		
		new MagicVilleComboProvider().getComboWith(mc);
	}
	
	@Override
	public List<MTGCombo> getComboWith(MagicCard mc) {
		List<MTGCombo> ret = new ArrayList<>();
		
		URLToolsClient c = URLTools.newClient();
		
		String id;
		try {
			Document req = RequestBuilder.build().setClient(c).url(BASE_URL+"submit_search").method(METHOD.POST).addContent("n", mc.getName()).toHtml();
			id = req.select("td>a").first().attr("id").replace("c_t_", "");
		} catch (IOException e) {
			logger.error("error looking for card " + mc +" : " + e);
			return ret;
		}
		
		try {
			Document req = RequestBuilder.build().setClient(c).url(BASE_URL+"resultats").method(METHOD.POST).addContent("card_to_search["+id+"]", mc.getName()).toHtml();
			
			req.select("tr[id]").forEach(tr->{
				MTGCombo cbo = new MTGCombo();
						 cbo.setName(tr.child(1).text());
						 
			
			try {
				Document cboDetail = RequestBuilder.build().setClient(c).url(BASE_URL+tr.child(0).select("a").attr("href")).method(METHOD.GET).toHtml();
				cbo.setComment(cboDetail.select("div[align=justify]").text());

				
				
			} catch (IOException e) {
				logger.error("error getting detail for " + cbo, e);
			}
	 
						 
						 
						 
				
			});
			
		} catch (IOException e) {
			logger.error("error looking for card " + mc +" with id = "+id,e);
			return ret;
		}
		
		
		
		
		return ret;
	}

	@Override
	public String getName() {
		return "Magic-Ville";
	}

}
