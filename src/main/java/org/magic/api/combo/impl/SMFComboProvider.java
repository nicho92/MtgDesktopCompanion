package org.magic.api.combo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class SMFComboProvider extends AbstractComboProvider {

	private static final String BASE_URL="https://www.smfcorp.net";

	@Override
	public List<MTGCombo> loadComboWith(MTGCard mc) {

		List<MTGCombo> cbos = new ArrayList<>();
		MTGHttpClient c = URLTools.newClient();
		String cardUri;
		try {
			Document d = RequestBuilder.build().url(BASE_URL+"/include/php/views/moteurCartes.php").get().setClient(c)
						  .addContent("ihm", "M1")
						  .addContent("order", "car_nomfr")
						  .addContent("page", "1")
						  .addContent("tri", "asc")
						  .addContent("language", "FR")
						  .addContent("nom", mc.getName())
						  .addHeader(URLTools.ORIGIN, BASE_URL)
						  .addHeader(URLTools.REFERER, BASE_URL)
						  .addHeader(URLTools.X_REQUESTED_WITH,"XMLHttpRequest")
						  .addHeader("sec-fetch-mode","core")
						  .addHeader("sec-fetch-site","same-origin")
						  .toHtml();

			cardUri=d.select("tr.contentTrPair>td>a").attr("href");
			cardUri = BASE_URL+cardUri.substring(cardUri.lastIndexOf('/'));

		} catch (Exception e) {
			logger.error(e);
			return cbos;
		}


			Document d;
			try {
				d = RequestBuilder.build().url(cardUri).get().setClient(c).toHtml();

			String idAttribute= d.getElementById("dataAttribute").attr("value");

			d = RequestBuilder.build().url(BASE_URL+"/index.php").get().setClient(c)
						.addContent("objet", "combos")
						.addContent("action", "refreshPage")
						.addContent("nb", "0")
						.addContent("dataIsValidated", "1")
						.addContent("dataAttribute", idAttribute)
						.toHtml();
			} catch (IOException e) {
				return cbos;
			}

			d.select("div.media-body").forEach(el->{

				var cbo = new MTGCombo();
						 cbo.setName(el.getElementsByTag("h4").text());
						 cbo.setPlugin(this);

						 Document details;
						try {
							details = RequestBuilder.build().url(BASE_URL+"/"+el.getElementsByTag("a").attr("href")).get().setClient(c).toHtml();
							var article = details.getElementsByTag("article");
							article.select("div.panel").remove();
							cbo.setComment(article.text());
						} catch (IOException e) {
							logger.error(e);
						}

						cbos.add(cbo);

			});








		return cbos;
	}

	@Override
	public String getName() {
		return "SMF";
	}

}
