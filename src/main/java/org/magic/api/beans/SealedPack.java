package org.magic.api.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SealedPack {

	Map<MagicEdition, Integer> pack;
	
	public SealedPack() {
		pack=new HashMap<>();
	}
	
	public Integer getQty(MagicEdition ed)
	{
		return pack.get(ed);
	}
	
	
	public List<MagicEdition> listEditions()
	{
		return new ArrayList<>(pack.keySet());
	}
	
	public void set(MagicEdition ed,int qty)
	{
		pack.put(ed, qty);
	}
	
	public void add(MagicEdition ed,int qty)
	{
		if(pack.get(ed)!=null)
			pack.put(ed, pack.get(ed)+qty);
		else
			pack.put(ed, qty);
	}
	
	public void remove(MagicEdition ed,int qty)
	{
		int res = pack.get(ed)-qty;
		if(res<0)
			res=0;
		
		pack.put(ed, res);
	}
	
	public void remove(MagicEdition ed)
	{
		pack.remove(ed);
	}
	
	public Map<MagicEdition, Integer> get()
	{
		return pack;
	}

	public void clear() {
		pack.clear();
		
	}

	public int size() {
		return pack.size();
	}
}
