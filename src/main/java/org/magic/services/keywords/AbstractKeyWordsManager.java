package org.magic.services.keywords;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.EVENT;
import org.magic.api.beans.MTGKeyWord.TYPE;
import org.magic.api.beans.MagicCard;
import org.magic.services.MTGLogger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class AbstractKeyWordsManager {

	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	public abstract List<MTGKeyWord> getStaticsAbilities();
	public abstract List<MTGKeyWord> getActivatedAbilities();
	public abstract List<MTGKeyWord> getTriggeredAbilities();
	public abstract List<MTGKeyWord> getKeywordActions();
	public abstract List<MTGKeyWord> getWordsAbilities();
	private static AbstractKeyWordsManager inst;
	
	
	public static AbstractKeyWordsManager getInstance()
	{
		if(inst ==null)
			inst = new MTGGamePediaKeywordProvider();
		
		return inst;
	}

	public List<MTGKeyWord> getList() {
		List<MTGKeyWord> keys = new ArrayList<>();
		keys.addAll(getActivatedAbilities());
		keys.addAll(getStaticsAbilities());
		keys.addAll(getTriggeredAbilities());
		keys.addAll(getKeywordActions());
		keys.addAll(getWordsAbilities());
		return keys;
	}
	
	
	public MTGKeyWord generateFromKeyString(String key) {
		for (MTGKeyWord k : getList())
			if (key.equalsIgnoreCase(k.getKeyword()))
				return k;

		return null;
	}

	public Set<MTGKeyWord> getKeywordsFrom(MagicCard mc) {
		return getKeywordsFrom(mc.getText());
	}
	
	public Set<MTGKeyWord> getKeywordsFrom(String cardContent) {
		return getList().stream()
				   .filter(kw->String.valueOf(cardContent.toLowerCase()).contains(kw.getKeyword().toLowerCase()))
				   .distinct()
				   .collect(Collectors.toSet());
	}
	
	public Set<MTGKeyWord> getKeywordsFrom(MagicCard mc,EVENT t) {
		return getKeywordsFrom(mc).stream()
				   .filter(l->l.getEvent()==t)
				   .distinct()
				   .collect(Collectors.toSet());
	}

	public Set<MTGKeyWord> getKeywordsFrom(MagicCard mc,TYPE t) {
		return getKeywordsFrom(mc).stream()
				   .filter(l->l.getType()==t)
				   .distinct()
				   .collect(Collectors.toSet());
	}

	public JsonObject toJson()
	{
		JsonObject ret = new JsonObject();
		
		for(TYPE t : TYPE.values())
		{
			JsonArray arr = new JsonArray();

			getList().stream().filter(k->k.getType()==t).forEach(kw->{
				JsonObject o = new JsonObject();
						   o.addProperty("name", kw.getKeyword());
						  
						   if(kw.getEvent()!=null)
							   o.addProperty("event", kw.getEvent().name());

				arr.add(o);		   	   
			});
			
			ret.add(t.name().toLowerCase(), arr);
		}
		
		return ret;
	}
	
}
