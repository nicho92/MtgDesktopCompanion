package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.magic.api.beans.MTGFormat.AUTHORIZATION;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.interfaces.extra.MTGSerializable;


public class MTGDeck implements MTGSerializable {

	private static final long serialVersionUID = 1L;
	private Map<MTGCard, Integer> mapDeck;
	private Map<MTGCard, Integer> mapSideBoard;
	private int id;

	private String description;
	private String name="No Name";
	private Date dateCreation;
	private Date dateUpdate;
	private double averagePrice;
	private List<String> tags;
	private MTGCard commander;

	public enum BOARD {MAIN, SIDE}

	public MTGDeck()
	{
		id=-1;
		mapDeck = new LinkedHashMap<>();
		mapSideBoard = new LinkedHashMap<>();
		tags = new ArrayList<>();
		averagePrice = 0;
		dateCreation=new Date();
		dateUpdate=new Date();
	}


	@Override
	public String getStoreId() {
		return String.valueOf(getId());
	}



  public MTGDeck getMergedDeck() {
    var mergeCardList = new ArrayList<MTGCard>();
    var cardNames = new ArrayList<String>();

    getMainAsList().forEach(mc -> {
      if (! cardNames.contains(mc.getName())) {
        getMainAsList().stream().filter(k->k.getName().equalsIgnoreCase(mc.getName())).forEach(k->mergeCardList.add(mc));
        cardNames.add(mc.getName());
      }
    });

    var mergedDeck = toDeck(mergeCardList);
    mergedDeck.setName("merged cards deck");

    return mergedDeck;
  }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public MTGCard getValueAt(int pos) {
		return new ArrayList<>(getMain().keySet()).get(pos);
	}

	public MTGCard getSideValueAt(int pos) {
		return new ArrayList<>(getSideBoard().keySet()).get(pos);
	}

	public int getNbCards() {
		return getMain().entrySet().stream().mapToInt(Entry::getValue).sum();
	}

	public boolean isEmpty() {
		return getMain().isEmpty() && getSideBoard().isEmpty();
	}

	public List<MTGCard> getUniqueCards() {
		return getMain().keySet().stream().toList();
	}

	public int getCardCountByName(String name) {
			return getMain().entrySet().stream().filter(e->e.getKey().getName().equals(name)).mapToInt(Entry::getValue).sum();
	}


	public void remove(MTGCard mc) {
		if (getMain().get(mc) == 0)
			getMain().remove(mc);
		else
			getMain().put(mc, getMain().get(mc) - 1);
	}

	public void removeSide(MTGCard mc) {
		if (getSideBoard().get(mc) == 0)
			getSideBoard().remove(mc);
		else
			getSideBoard().put(mc, getSideBoard().get(mc) - 1);
	}

	public void delete(MTGCard mc, BOARD board) {
    var deck = ((board==BOARD.SIDE) ? mapSideBoard : mapDeck);
		deck.remove(mc);
	}

	public void add(MTGCard mc) {
		getMain().compute(mc, (k,v)->(v==null)?1:v+1);
	}

	public void addSide(MTGCard mc) {
		getSideBoard().compute(mc, (k,v)->(v==null)?1:v+1);
	}

	public boolean hasCard(MTGCard mc,boolean strict) {

		if(strict)
			return !getMain().keySet().stream().filter(k->k.equals(mc)).findAny().isEmpty();

		return !getMain().keySet().stream().filter(k->k.getName().equalsIgnoreCase(mc.getName())).findAny().isEmpty();
	}

	public Set<MTGFormat> getLegality() {
		var cmap = new LinkedHashSet<MTGFormat>();
		for (var mc : getMain().keySet()) {
			for (var mf : mc.getLegalities()) {
				cmap.add(mf);
			}
		}
		return cmap;
	}

	public String getColors() {

		var cmap = new LinkedHashSet<EnumColors>();
		for (MTGCard mc : getUniqueCards())
		{
			if ((mc.getCmc() != null))
			{
				for (EnumColors c : mc.getColors())
				{
					if(c!=null)
						cmap.add(c);
				}
			}
		}
		var tmp = new StringBuilder();

		cmap.stream().sorted().map(EnumColors::toManaCode).forEach(tmp::append);
		return tmp.toString();
	}

	public List<MTGCard> getMainAsList() {
		return toList(getMain().entrySet());
	}

	public List<MTGCard> getSideAsList() {
		return toList(getSideBoard().entrySet());
	}

	private List<MTGCard> toList(Set<Entry<MTGCard, Integer>> entrySet) {
		var deck = new ArrayList<MTGCard>();

		for (Entry<MTGCard, Integer> c : entrySet)
			for (var i = 0; i < c.getValue(); i++)
				deck.add(c.getKey());

		return deck;

	}

	public boolean isCompatibleFormat(MTGFormat mf) {
		for (MTGCard mc : mapDeck.keySet())
		{
			long num = mc.getLegalities().stream().filter(mf::equals).toList().stream().filter(f->f.getFormatLegality()==AUTHORIZATION.LEGAL || f.getFormatLegality()==AUTHORIZATION.RESTRICTED).count();

			if(num<=0)
				return false;

		}
		return true;
	}


	public static MTGDeck toDeck(List<MTGCard> cards) {
		var d = new MTGDeck();
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

	@Override
	public String toString() {
		return getName();
	}

	public void setMain(Map<MTGCard, Integer> mapDeck) {
		this.mapDeck = mapDeck;
	}

	public Map<MTGCard, Integer> getSideBoard() {
		return mapSideBoard;
	}

	public void setSideBoard(Map<MTGCard, Integer> mapSideBoard) {
		this.mapSideBoard = mapSideBoard;
	}

	public String getName() {
		return name;
	}

	public Map<MTGCard, Integer> getMain() {
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

	public void setCommander(MTGCard mc) {
		this.commander=mc;
	}

	public MTGCard getCommander() {
		return commander;
	}

	public MTGCard getCompanion() {

		Optional<MTGCard> opt= getUniqueCards().stream().filter(MTGCard::isCompanion).findFirst();

		if(opt.isPresent())
			return opt.get();
		else
			return null;


	}

}
