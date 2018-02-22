package org.magic.services.extra;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.EVENT;
import org.magic.api.beans.MagicCard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class KeyWordProvider {

	List<MTGKeyWord> list;
	
	public List<MTGKeyWord> getList() {
		return list;
	}
	
	
	public KeyWordProvider() {
		
			Gson s = new Gson();
			Type listType = new TypeToken<ArrayList<MTGKeyWord>>(){}.getType();
			list = s.fromJson(new InputStreamReader(this.getClass().getResourceAsStream("/data/keywords.json")), listType);
			if(list==null)
				list=new ArrayList<>();
		
	}
	
	public MTGKeyWord generateFromString(String key)
	{
		for(MTGKeyWord k : list)
			if(key.equalsIgnoreCase(k.getKeyword()))
				return k;
		
		return null;
	}

	
	public Set<MTGKeyWord> getKeywordsFrom(MagicCard mc)
	{
		Set<MTGKeyWord> ret = new LinkedHashSet<>();
		
		for(MTGKeyWord s : list)
		{	
			if(String.valueOf(mc.getText()).toLowerCase().contains(s.getKeyword().toLowerCase()))
					ret.add(s);
		}
		return ret;
	}

	public Set<MTGKeyWord> getKeywordsFrom(MagicCard magicCard, EVENT... t) {
		Set<MTGKeyWord> s = getKeywordsFrom(magicCard);
		Set<MTGKeyWord> ret = new HashSet<>();
		
		for(MTGKeyWord k : s)
			for(EVENT ev : t)
				if(k.getEvent().equals(ev))
						ret.add(k);
		
		
		return ret;
	}
	
}




