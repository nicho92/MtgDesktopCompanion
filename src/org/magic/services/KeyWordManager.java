package org.magic.services;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.magic.api.beans.KeyWord;
import org.magic.api.beans.MagicCard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class KeyWordManager {

	List<KeyWord> list;
	
	public List<KeyWord> getList() {
		return list;
	}
	
	public KeyWordManager() {
		
		Gson s = new Gson();
		
		Type listType = new TypeToken<ArrayList<KeyWord>>(){}.getType();
		list = s.fromJson(new InputStreamReader(this.getClass().getResourceAsStream("/res/data/keywords.json")), listType);
	}
	
	public KeyWord generateFromString(String key)
	{
		for(KeyWord k : list)
			if(key.toLowerCase().equals(k.getKeyword().toLowerCase()))
				return k;
		
		return null;
	}
	
	public Set<KeyWord> getKeywordsFrom(MagicCard mc)
	{
		String[] text = mc.getText().split(" ");
		Set<KeyWord> l = new LinkedHashSet<KeyWord>();
		for(String s : text)
		{	
			KeyWord k = generateFromString(s);
			if(k!=null)
				l.add(k);
		}
		
		System.out.println(l);
		return l;
		
	}
	
}




