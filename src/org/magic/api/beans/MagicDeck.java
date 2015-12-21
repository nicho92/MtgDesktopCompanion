package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MagicDeck implements Serializable{

	private static final long serialVersionUID = 1L;
	Map<MagicCard,Integer> mapDeck;
	Map<MagicCard,Integer> mapSideBoard;
	
	String description;
	String name;
	
	public String toString()
	{
		return getName() +" ("+mapDeck.size()+")";
	}
	
	public void setMapDeck(Map<MagicCard, Integer> mapDeck) {
		this.mapDeck = mapDeck;
	}



	public Map<MagicCard, Integer> getMapSideBoard() {
		return mapSideBoard;
	}



	public void setMapSideBoard(Map<MagicCard, Integer> mapSideBoard) {
		this.mapSideBoard = mapSideBoard;
	}

	public Set<MagicFormat> getLegality() {
		Set<MagicFormat> cmap = new LinkedHashSet<MagicFormat>();
		for(MagicCard mc : mapDeck.keySet())
		{
			for(MagicFormat mf : mc.getLegalities())
			{
				cmap.add(mf);
			}
		}
		return cmap;
	}
	//TODO correct color identity of cards
	public String getColors() {
		Set<String> cmap = new LinkedHashSet<String>();
		for(MagicCard mc : mapDeck.keySet())
		{
			if((mc.getCmc()!=null))
				for(String c : mc.getColorIdentity())
					cmap.add("{"+c+"}");
		}
		return cmap.toString();
	}

	public String getName() {
		return name;
	}

	public MagicDeck() {
		mapDeck = new HashMap<MagicCard,Integer>();
		mapSideBoard = new HashMap<MagicCard,Integer>();
	}
	
	public Map<MagicCard, Integer> getMap() {
		return mapDeck;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public MagicCard getValueAt(int pos)
    {
		return new ArrayList<MagicCard>(mapDeck.keySet()).get(pos);
    }
	
	public List<MagicCard> getAsList()
	{
		ArrayList<MagicCard> deck = new ArrayList<MagicCard>();
		
		for(MagicCard c : mapDeck.keySet())
			for(int i=0;i<mapDeck.get(c);i++)
				deck.add(c);
		return deck;
	}
	
	//TODO test format soit un retour des format autorisé soit un test
	public boolean isCompatibleFormat(MagicFormat mf)
	{
		for(MagicCard mc : mapDeck.keySet())
		{	if(!mc.getLegalities().contains(mf))
				return false;
			
				
		}
		return true;
	}
	
	public int getNbCards()
	{
		return getAsList().size();
	}

	public void setName(String name) {
		this.name=name;
		
	}
	
	
}
