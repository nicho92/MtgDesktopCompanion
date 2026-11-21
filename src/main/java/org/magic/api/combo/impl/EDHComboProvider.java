package org.magic.api.combo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCombo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

public class EDHComboProvider extends AbstractComboProvider {

    @Override
    public String getName() {
	return "EDHCombo";
    }



    @Override
    public List<MTGCombo> loadComboWith(MTGCard mc) {
	final var result = new ArrayList<MTGCombo>();

	try {
	    final var arr = RequestBuilder.build().newClient().get().url("https://edh-combos.com/api/search").addContent("s", mc.getName()).toJson().getAsJsonObject().get("combos").getAsJsonArray();

	    if(arr.isEmpty()) {
		return result;
	    }

	    for(final var e : arr)
	    {
		final var obj = e.getAsJsonObject();

		final var combo = new MTGCombo();
		combo.setPlugin(this);
		combo.setName(obj.get("i").toString());
		combo.setComment(obj.get("r").getAsString());

		for(final var c : obj.get("c").getAsJsonArray())
		{

		    final var card = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(c.getAsString(), null, true).getFirst();
		    combo.getCards().add(card);
		}

		final var doc= URLTools.extractAsHtml("https://edh-combos.com/combo/"+obj.get("d").getAsString());

		combo.setComment(doc.select("main").html());

		notify(combo);


		result.add(combo);

	    }

	} catch (final IOException e) {
	    logger.error(e);
	}
	return result;
    }

}
