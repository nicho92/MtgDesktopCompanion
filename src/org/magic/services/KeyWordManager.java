package org.magic.services;

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

public class KeyWordManager {

	List<MTGKeyWord> list;
	
	public List<MTGKeyWord> getList() {
		return list;
	}
	
	
	public KeyWordManager() {
		
			Gson s = new Gson();
			Type listType = new TypeToken<ArrayList<MTGKeyWord>>(){}.getType();
			list = s.fromJson(new InputStreamReader(this.getClass().getResourceAsStream("/res/data/keywords.json")), listType);
			if(list==null)
				list=new ArrayList<MTGKeyWord>();
		
	}
	
	public MTGKeyWord generateFromString(String key)
	{
		for(MTGKeyWord k : list)
			if(key.toLowerCase().equals(k.getKeyword().toLowerCase()))
				return k;
		
		return null;
	}

	
	public Set<MTGKeyWord> getKeywordsFrom(MagicCard mc)
	{
		Set<MTGKeyWord> ret = new LinkedHashSet<MTGKeyWord>();
		
		for(MTGKeyWord s : list)
		{	
			/*for(String st : texts)
			{
				if(st.equalsIgnoreCase(s.getKeyword()))
					ret.add(s);
			}*/
				if(mc.getText().toLowerCase().contains(s.getKeyword().toLowerCase()))
					ret.add(s);
		}
		return ret;
	}
//	
//	public boolean hasKeyWord(MagicCard mc, MTGKeyWord k)
//	{
//		for(MTGKeyWord kw : getKeywordsFrom(mc))
//		{
//			if(kw.equals(k))
//				return true;
//		}
//		return false;
//	}
//	

	public Set<MTGKeyWord> getKeywordsFrom(MagicCard magicCard, EVENT... t) {
		Set<MTGKeyWord> s = getKeywordsFrom(magicCard);
		Set<MTGKeyWord> ret = new HashSet<MTGKeyWord>();
		
		for(MTGKeyWord k : s)
			for(EVENT ev : t)
				if(k.getEvent().equals(ev))
						ret.add(k);
		
		
		return ret;
	}
	
}




