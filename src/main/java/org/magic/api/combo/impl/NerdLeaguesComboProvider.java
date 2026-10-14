package org.magic.api.combo.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.services.network.MTGHttpClient;
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
	var result = new ArrayList<MTGCombo>();
	
	var payload = new JsonObject();
	var cardsP = new JsonArray();
	
	cardsP.add(mc.getName());
	
	payload.add("cards", cardsP);
	payload.addProperty("page", 1);
	
	  try {
	      
	      MTGHttpClient c = URLTools.newClient();
	      
	      var element = c.doPost("https://www.nerdleagues.com/combos/search-by-card", URLTools.toJson(payload.toString()), Map.of(URLTools.REFERER,"https://www.nerdleagues.com/combos")).getAsJsonObject();
	      	      
	    
	    for(var e : element.get("results").getAsJsonObject().get("included").getAsJsonArray())
	    {
		var obj = e.getAsJsonObject();
		
		var combo = new MTGCombo();;
		
		combo.setName(obj.get("identity").getAsString());	
		combo.setComment(obj.get("description").getAsString());
		combo.setPlugin(this);
		
		for(var el : obj.get("uses").getAsJsonArray())
		{
		    var id = el.getAsJsonObject().get("card").getAsJsonObject().get("name").getAsString();
		    
		    var card = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(id,null,true).getFirst();
		    
		    combo.getCards().add(card);
		}
		notify(combo);
		result.add(combo);
	    }
	} catch (IOException e) {
	   logger.error(e);
	}
	
	
	
	
	return result;
    }

}
