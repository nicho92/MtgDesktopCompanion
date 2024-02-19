package org.magic.api.combo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCombo;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class MagicVilleComboProvider extends AbstractComboProvider {


	private static final String BASE_URL="https://www.magic-ville.com/fr/combos/";


	@Override
	public List<MTGCombo> loadComboWith(MTGCard mc) {
		List<MTGCombo> ret = new ArrayList<>();

		MTGHttpClient c = URLTools.newClient();

		String id;
		try {
			Document req = RequestBuilder.build().setClient(c).url(BASE_URL+"submit_search").post().addContent("n", mc.getName()).toHtml();
			id = req.select("td>a").first().attr("id").replace("c_t_", "");
		} catch (Exception e) {
			logger.error("error looking for card {}",mc, e);
			return ret;
		}


			Document req;
			try {
				req = RequestBuilder.build().setClient(c).url(BASE_URL+"resultats").addHeader(URLTools.ACCEPT_LANGUAGE, "en-US,en;q=0.5").post().addContent("card_to_search["+id+"]", mc.getName()).toHtml();
			} catch (IOException e) {
				logger.error(e);
				return ret;
			}

			req.select("tr[id]").forEach(tr->{
				var cbo = new MTGCombo();
						 cbo.setName(tr.child(1).text());
						 cbo.setPlugin(this);


					Document cboDetail;
					try {
						cboDetail = RequestBuilder.build().setClient(c).url(BASE_URL+tr.child(0).select("a").attr("href")).get().toHtml();
						cbo.setComment(cboDetail.select("div[align=justify]").text());
					} catch (IOException e) {
						logger.error(e);
					}

					notify(cbo);
					ret.add(cbo);

			});


		return ret;
	}

	@Override
	public String getName() {
		return "Magic-Ville";
	}

}
