package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.magic.api.beans.MagicFormat.AUTHORIZATION;
import org.magic.api.beans.enums.MTGColor;
import org.magic.tools.IDGenerator;


public class MagicDeck implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<MagicCard, Integer> mapDeck;
	private Map<MagicCard, Integer> mapSideBoard;
	private int id;
	
	private String description;
	private String name="No Name";
	private Date dateCreation;
	private Date dateUpdate;
	private double averagePrice;
	private List<String> tags;
	private MagicCard commander;

	public enum BOARD {MAIN, SIDE}
	
	public MagicDeck() 
	{
		id=-1;
		mapDeck = new LinkedHashMap<>();
		mapSideBoard = new LinkedHashMap<>();
		tags = new ArrayList<>();
		averagePrice = 0;
		dateCreation=new Date();
		dateUpdate=new Date();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public MagicCard getValueAt(int pos) {
		return new ArrayList<>(getMain().keySet()).get(pos);
	}

	public MagicCard getSideValueAt(int pos) {
		return new ArrayList<>(getSideBoard().keySet()).get(pos);
	}
	
	public int getNbCards() {
		return getMain().entrySet().stream().mapToInt(Entry::getValue).sum();
	}
	
	public boolean isEmpty() {
		return getMain().isEmpty() && getSideBoard().isEmpty();
	}

	public List<MagicCard> getUniqueCards() {
		return getMain().keySet().stream().toList();
	}

	public void remove(MagicCard mc) {
		if (getMain().get(mc) == 0)
			getMain().remove(mc);
		else
			getMain().put(mc, getMain().get(mc) - 1);
	}
	
	public void removeSide(MagicCard mc) {
		if (getSideBoard().get(mc) == 0)
			getSideBoard().remove(mc);
		else
			getSideBoard().put(mc, getSideBoard().get(mc) - 1);
	}
	
	public void delete(MagicCard mc) {
		mapDeck.remove(mc);
	}
		
	public void add(MagicCard mc) {
		getMain().compute(mc, (k,v)->(v==null)?1:v+1);
	}
	
	public void addSide(MagicCard mc) {
		getSideBoard().compute(mc, (k,v)->(v==null)?1:v+1);
	}

	public boolean hasCard(MagicCard mc,boolean strict) {
		
		if(strict)
			return !getMain().keySet().stream().filter(k->IDGenerator.generate(k).equals(IDGenerator.generate(mc))).findAny().isEmpty();
		
		return !getMain().keySet().stream().filter(k->k.getName().equalsIgnoreCase(mc.getName())).findAny().isEmpty();
	}

	public Set<MagicFormat> getLegality() {
		Set<MagicFormat> cmap = new LinkedHashSet<>();
		for (MagicCard mc : getMain().keySet()) {
			for (MagicFormat mf : mc.getLegalities()) {
				cmap.add(mf);
			}
		}
		return cmap;
	}

	public String getColors() {
		
		Set<MTGColor> cmap = new LinkedHashSet<>();
		for (MagicCard mc : getUniqueCards())
		{
			if ((mc.getCmc() != null))
			{
				for (MTGColor c : mc.getColors())
				{
					if(c!=null)
						cmap.add(c);
				}
			}
		}
		var tmp = new StringBuilder();
		
		cmap.stream().sorted().map(MTGColor::toManaCode).forEach(tmp::append);
		return tmp.toString();
	}
	
	public List<MagicCard> getMainAsList() {
		return toList(getMain().entrySet());
	}


	public List<MagicCard> getSideAsList() {
		return toList(getSideBoard().entrySet());
	}
	
	private List<MagicCard> toList(Set<Entry<MagicCard, Integer>> entrySet) {
		ArrayList<MagicCard> deck = new ArrayList<>();

		for (Entry<MagicCard, Integer> c : entrySet)
			for (var i = 0; i < c.getValue(); i++)
				deck.add(c.getKey());
		
		return deck;
		
	}
	
	public boolean isCompatibleFormat(MagicFormat mf) {
		for (MagicCard mc : mapDeck.keySet()) 
		{
			long num = mc.getLegalities().stream().filter(mf::equals).toList().stream().filter(f->f.getFormatLegality()==AUTHORIZATION.LEGAL || f.getFormatLegality()==AUTHORIZATION.RESTRICTED).count();
			
			if(num<=0)
				return false;
			
		}
		return true;
	}


	public static MagicDeck toDeck(List<MagicCard> cards) {
		var d = new MagicDeck();
		d.setName("export");
		d.setDescription("");

		if (cards == null)
			return d;

		cards.forEach(d::add);

		return d;
	}
	
	
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

	public void setMain(Map<MagicCard, Integer> mapDeck) {
		this.mapDeck = mapDeck;
	}

	public Map<MagicCard, Integer> getSideBoard() {
		return mapSideBoard;
	}

	public void setSideBoard(Map<MagicCard, Integer> mapSideBoard) {
		this.mapSideBoard = mapSideBoard;
	}

	public String getName() {
		return name;
	}
	
	public Map<MagicCard, Integer> getMain() {
		return mapDeck;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public MagicCard getCompanion() {

		Optional<MagicCard> opt= getUniqueCards().stream().filter(MagicCard::isCompanion).findFirst();
		
		if(opt.isPresent())
			return opt.get();
		else
			return null;
		
		
	}
	
	
}
