package org.magic.api.combo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCombo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;

public class NerdLeaguesComboProvider extends AbstractComboProvider {

    @Override
    public String getName() {
	return "NerdLeagues";
    }

    @Override
    public List<MTGCombo> loadComboWith(MTGCard mc) {
	final var result = new ArrayList<MTGCombo>();

	final var payload = new JsonObject();
	final var cardsP = new JsonArray();

	cardsP.add(mc.getName());

	payload.add("cards", cardsP);
	payload.addProperty("page", 1);

	  try {

	      final var c = URLTools.newClient();

	      final var element = c.doPost("https://www.nerdleagues.com/combos/search-by-card", URLTools.toJson(payload.toString()), Map.of(URLTools.REFERER,"https://www.nerdleagues.com/combos")).getAsJsonObject();


	    for(final var e : element.get("results").getAsJsonObject().get("included").getAsJsonArray())
	    {
		final var obj = e.getAsJsonObject();

		final var combo = new MTGCombo();

		combo.setName(obj.get("identity").getAsString());
		combo.setComment(obj.get("description").getAsString());
		combo.setPlugin(this);

		for(final var el : obj.get("uses").getAsJsonArray())
		{
		    final var id = el.getAsJsonObject().get("card").getAsJsonObject().get("name").getAsString();

		    final var card = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(id,null,true).getFirst();

		    combo.getCards().add(card);
		}
		notify(combo);
		result.add(combo);
	    }
	} catch (final IOException e) {
	   logger.error(e);
	}




	return result;
    }

}
