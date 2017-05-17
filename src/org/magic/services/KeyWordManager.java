package org.magic.services;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.TYPE;
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
	}
	
	public MTGKeyWord generateFromString(String key)
	{
		for(MTGKeyWord k : list)
			if(key.toLowerCase().equals(k.getKeyword().toLowerCase()))
				return k;
		
		return null;
	}
	
	
	public static void main(String[] args) {
		KeyWordManager k = new KeyWordManager();
		
		MTGKeyWord act = k.generateFromString("Transform");
		
		System.out.println(act.getDescription());
		System.out.println(k.generateActionFrom(act));
		
	}
	
	
	public AbstractAction generateActionFrom(MTGKeyWord k)
	{
		try{
				ClassLoader classLoader = KeyWordManager.class.getClassLoader();
				return (AbstractAction)classLoader.loadClass("org.magic.game.actions.cards."+k.getKeyword()+"Actions").newInstance();
			}catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
	}
	
	
	
	public Set<MTGKeyWord> getKeywordsFrom(MagicCard mc)
	{
		String[] text = mc.getText().split(" ");
		Set<MTGKeyWord> ret = new LinkedHashSet<MTGKeyWord>();
		for(String s : text)
		{	
			MTGKeyWord k = generateFromString(s);
			if(k!=null)
				ret.add(k);
		}
		return ret;
	}
	
	public boolean hasKeyWord(MagicCard mc, MTGKeyWord k)
	{
		for(MTGKeyWord kw : getKeywordsFrom(mc))
		{
			if(kw.equals(k))
				return true;
		}
		return false;
	}
	

	public Set<MTGKeyWord> getKeywordsFrom(MagicCard magicCard, TYPE t) {
		Set<MTGKeyWord> s = getKeywordsFrom(magicCard);
		Set<MTGKeyWord> ret = new HashSet<MTGKeyWord>();
		
		for(MTGKeyWord k : s)
			if(k.getType()==t)
				ret.add(k);
		
		
		return ret;
	}
	
}




