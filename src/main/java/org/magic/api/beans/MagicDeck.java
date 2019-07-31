package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.magic.tools.ColorParser;

public class MagicDeck implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<MagicCard, Integer> mapDeck;
	private Map<MagicCard, Integer> mapSideBoard;

	private String description;
	private String name;
	private Date dateCreation;
	private Date dateUpdate;
	private double averagePrice;
	private List<String> tags;
	private MagicCard commander;

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(double averagePrice) {
		this.averagePrice = averagePrice;
	}

	public Date getDateCreation() {
		return dateCreation;
	}
	
	public Date getDateUpdate() {
		return dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	public String toString() {
		return getName();
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

	public boolean hasCard(MagicCard mc) {
		
		for(MagicCard k : mapDeck.keySet())
			if(k.getName().equalsIgnoreCase(mc.getName()))
				return true;
		
		return false;
	}

	public Set<MagicFormat> getLegality() {
		Set<MagicFormat> cmap = new LinkedHashSet<>();
		for (MagicCard mc : mapDeck.keySet()) {
			for (MagicFormat mf : mc.getLegalities()) {
				cmap.add(mf);
			}
		}
		return cmap;
	}

	public String getColors() {
		Set<String> cmap = new LinkedHashSet<>();
		for (MagicCard mc : mapDeck.keySet()) {
			if ((mc.getCmc() != null))
				for (String c : mc.getColors())
					cmap.add(ColorParser.getCodeByName(c,true));
		}
		return cmap.toString();
	}

	public String getName() {
		return name;
	}

	public MagicDeck() {
		mapDeck = new HashMap<>();
		mapSideBoard = new HashMap<>();
		tags = new ArrayList<>();
		averagePrice = 0;
		dateCreation=new Date();
		dateUpdate=new Date();
	}
	
	public boolean isEmpty()
	{
		return mapDeck.isEmpty() && mapSideBoard.isEmpty();
	}

	public void remove(MagicCard mc) {
		if (mapDeck.get(mc) == 0)
			mapDeck.remove(mc);
		else
			mapDeck.put(mc, mapDeck.get(mc) - 1);
	}
	
	public void delete(MagicCard mc)
	{
		mapDeck.remove(mc);
	}
	

	public void add(MagicCard mc) {
		if (mapDeck.get(mc) == null)
			mapDeck.put(mc, 1);
		else
			mapDeck.put(mc, mapDeck.get(mc) + 1);
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

	public MagicCard getValueAt(int pos) {
		return new ArrayList<MagicCard>(mapDeck.keySet()).get(pos);
	}

	public MagicCard getSideValueAt(int pos) {
		return new ArrayList<MagicCard>(mapSideBoard.keySet()).get(pos);
	}

	public List<MagicCard> getAsList() {
		ArrayList<MagicCard> deck = new ArrayList<>();

		for (Entry<MagicCard, Integer> c : mapDeck.entrySet())
			for (int i = 0; i < c.getValue(); i++)
				deck.add(c.getKey());
		return deck;
	}

	public List<MagicCard> getSideAsList() {
		ArrayList<MagicCard> deck = new ArrayList<>();

		for (Entry<MagicCard, Integer> c : mapSideBoard.entrySet())
			for (int i = 0; i < c.getValue(); i++)
				deck.add(c.getKey());
		return deck;
	}

	public boolean isCompatibleFormat(MagicFormat mf) {
		for (MagicCard mc : mapDeck.keySet()) 
		{
			if(mc.getLegalities().stream().filter(f->f.equals(mf)).noneMatch(MagicFormat::isLegal))
					return false;
		}
		return true;
	}

	public int getNbCards() {
		return getAsList().size();
	}
	
	public void setName(String name) {
		this.name = name;

	}
	
	public static MagicDeck toDeck(List<MagicCard> cards)
	{
		MagicDeck d = new MagicDeck();
		d.setName("export");
		d.setDescription("");
		
		if(cards==null)
			return d;
		
		cards.forEach(d::add);
		
		return d;
	}

	public void setCreationDate(Date date) {
		this.dateCreation=date;
	}

	public void setCommander(MagicCard mc) {
		this.commander=mc;
	}

	public MagicCard getCommander() {
		return commander;
	}

	public List<MagicCard> getUniqueCards() {
		
		if(getMap().isEmpty())
			return new ArrayList<>();
		
		return new ArrayList<>(getMap().keySet());
	}
	
}
