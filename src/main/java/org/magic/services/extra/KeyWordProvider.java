package org.magic.services.extra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.EVENT;
import org.magic.api.beans.MTGKeyWord.TYPE;
import org.magic.api.beans.MagicCard;
import org.magic.api.providers.impl.Mtgjson4Provider;
import org.magic.services.MTGLogger;
import org.magic.tools.URLTools;

import com.google.gson.JsonObject;

public class KeyWordProvider {

	private List<MTGKeyWord> list;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	
	public List<MTGKeyWord> getList() {
		return list;
	}

	public KeyWordProvider() {

		if(list==null)
		{
			list = new ArrayList<>();
		}
		
		if(!list.isEmpty())
			return;
		
		
		JsonObject el;
		try {
			el = URLTools.extractJson(Mtgjson4Provider.URL_JSON_KEYWORDS).getAsJsonObject();
			el.get("AbilityWords").getAsJsonArray().forEach(s->list.add(new MTGKeyWord(s.getAsString(),TYPE.WORD)));
			el.get("KeywordAbilities").getAsJsonArray().forEach(s->list.add(new MTGKeyWord(s.getAsString(),TYPE.ABILITIES)));
			el.get("KeywordActions").getAsJsonArray().forEach(s->list.add(new MTGKeyWord(s.getAsString(),TYPE.ACTION)));
		
		} catch (IOException e) {
			logger.error(e);
		}

	}

	public MTGKeyWord generateFromKeyString(String key) {
		for (MTGKeyWord k : list)
			if (key.equalsIgnoreCase(k.getKeyword()))
				return k;

		return null;
	}

	public Set<MTGKeyWord> getKeywordsFrom(MagicCard mc) {
		Set<MTGKeyWord> ret = new LinkedHashSet<>();

		for (MTGKeyWord s : list) {
			if (String.valueOf(mc.getText()).toLowerCase().contains(s.getKeyword().toLowerCase()))
				ret.add(s);
		}
		return ret;
	}

	public Set<MTGKeyWord> getKeywordsFrom(MagicCard mc,TYPE t) {
		return list.stream()
				   .filter(l->l.getType()==t)
				   .filter(l->String.valueOf(mc.getText()).toLowerCase().contains(l.getKeyword().toLowerCase()))
				   .collect(Collectors.toSet());
	}
	
	
}
