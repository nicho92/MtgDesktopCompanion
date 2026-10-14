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
import org.magic.services.network.RequestBuilder;
import org.magic.services.tools.MTG;

public class MTGNexusComboProvider extends AbstractComboProvider {

    @Override
    public String getName() {
	return "MTGNexus";
    }
    
    @Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LIMIT",
				MTGProperty.newIntegerProperty("50", "Limit results", 10, 100));
	}

    
    
    @Override
    public List<MTGCombo> loadComboWith(MTGCard mc) {
	var result = new ArrayList<MTGCombo>();
	
	  try {
	    var arr = RequestBuilder.build().newClient().get().url("https://api.mtg-nexus.com/api/v1/combos/search")
	    	  								.addContent("q", mc.getName())
	    	  								.addContent("limit", getString("LIMIT"))
	    	  								.toJson()
	    	  								.getAsJsonObject().get("combos").getAsJsonArray();
	    
	    
	    for(var e : arr)
	    {
		var obj = e.getAsJsonObject();
		
		var combo = new MTGCombo();;
		
		combo.setName(obj.get("name").getAsString());	
		combo.setComment(obj.get("description").getAsString());
		combo.setPlugin(this);
		
		for(var el : obj.get("component_cards").getAsJsonArray())
		{
		    var id = el.getAsJsonObject().get("id").getAsString();
		    
		    var card = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(id);
		    
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
