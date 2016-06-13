package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MagicEdition implements Serializable,Comparable<MagicEdition> {

	
	private String set;
	private String rarity;
	private String artist;
	private String multiverse_id;
	private String flavor;
	private String number;
	private String layout;
	private MagicPrice price;
	private String url;
	private String image_url;
	private String set_url;
	private String store_url;
	private String id;
	private String releaseDate;
	private String type;
	private int cardCount;
	private String block;
	private String border;
	private List<Object> booster;
	private Map<String,String> translations;
	public static final long serialVersionUID = 4136786369066180196L;
	
	
	
	public Map<String, String> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<String, String> translations) {
		this.translations = translations;
	}

	public MagicEdition() {
		booster=new ArrayList<Object>();
	}
	
	public List<Object> getBooster() {
		return booster;
	}

	public void setBooster(List<Object> booster) {
		this.booster = booster;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCardCount() {
		return cardCount;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	@Override
	public boolean equals(Object obj) {
		return getId().equals(((MagicEdition)obj).getId());
	}
	
	public String getId()
	{
//		if(id==null)
//			return getSet_url().substring(getSet_url().lastIndexOf("/")+1, getSet_url().length());
//		
		return id;
	}
	
	public void setId(String id)
	{
		this.id=id;
	}
	
	
	@Override
	public String toString() {
		return getSet();
	}
	
	public String getSet() {
		return set;
	}
	public void setSet(String set) {
		this.set = set;
	}
	public String getRarity() {
		return rarity;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getMultiverse_id() {
		return multiverse_id;
	}
	public void setMultiverse_id(String multiverse_id) {
		this.multiverse_id = multiverse_id;
	}
	public String getFlavor() {
		return flavor;
	}
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public MagicPrice getPrice() {
		return price;
	}
	public void setPrice(MagicPrice price) {
		this.price = price;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getSet_url() {
		return set_url;
	}
	public void setSet_url(String set_url) {
		this.set_url = set_url;
	}
	public String getStore_url() {
		return store_url;
	}
	public void setStore_url(String store_url) {
		this.store_url = store_url;
	}

	public int compare(MagicEdition o1, MagicEdition o2) {
		return o1.getSet().compareTo(o2.getSet());
	}

	 @Override
	 public int hashCode() {
	    return set.hashCode();
	 }

	@Override
	public int compareTo(MagicEdition o) {
		return compare(this, o);
	}
	
}
