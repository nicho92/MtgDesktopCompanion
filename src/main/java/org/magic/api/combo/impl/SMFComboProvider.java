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

public class SMFComboProvider extends AbstractComboProvider {

	private static final String BASE_URL="https://www.smfcorp.net";
		
	
	public static void main(String[] args) {
		var card = new MTGCard();
			card.setName("Wrath of God");
			
			
			new SMFComboProvider().loadComboWith(card);
	}
	
	
	@Override
	public List<MTGCombo> loadComboWith(MTGCard mc) {

		List<MTGCombo> cbos = new ArrayList<>();
		MTGHttpClient c = URLTools.newClient();
		String cardId="";
		try {
			var cardString = RequestBuilder.build().url(BASE_URL+"/index.php?objet=carte&action=searchCardTypeAhead").post().setClient(c)
						  .addContent("query", mc.getName())
						  .addContent("limit","10")
						  .addHeader(URLTools.ORIGIN, BASE_URL)
						  .addHeader(URLTools.REFERER, BASE_URL)
						  .addHeader(URLTools.X_REQUESTED_WITH,"XMLHttpRequest")
						  .addHeader("sec-fetch-mode","core")
						  .addHeader("sec-fetch-site","same-origin")
						  .toContentString();
			
			cardString=cardString.substring(cardString.indexOf("["));
			var cardObj = URLTools.toJson(cardString);
			cardId = cardObj.getAsJsonArray().get(0).getAsJsonObject().get("car_id").getAsString();
			
		} catch (Exception e) {
			logger.error(e);
				return cbos;
		}


			Document d;
			try {
			d = RequestBuilder.build().url(BASE_URL+"/mtg-combos-index.html").get().setClient(c)
						.addContent("objet", "combos")
						.addContent("action", "refreshPage")
						.addContent("nb", "12")
						.addContent("dataIsValidated", "1")
						.addContent("dataAttributes[]", cardId)
						.addContent("needPagination", "1")
						.addContent("dataDataId", "0")
						.addContent("view", "newarticleView.php")
						.addContent("dataIsValidated", "1")
						.addContent("dataIsTerminated", "1")
						.addContent("print", "1")
						.addContent("page", "1")
						.addContent("dataText", "")
						.addContent("tri", "dataPublicationDate")
						.addContent("login","")
						.toHtml();
			} catch (IOException e) {
				return cbos;
			}
						
			d.select("div.col div.card").forEach(el->{
				var cbo = new MTGCombo();
						 cbo.setName(el.getElementsByTag("h4").text());
						 cbo.setPlugin(this);

						 Document details;
						try {
							details = RequestBuilder.build().url(BASE_URL+"/"+el.getElementsByTag("a").attr("href")).get().setClient(c).toHtml();
							var article = details.getElementById("fullDeckDescription");
							cbo.setComment(article.html());
						} catch (Exception e) {
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
