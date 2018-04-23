package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MagicCard implements Serializable {

	/**
	* 
	*/

	public enum LAYOUT {
		Normal, Token, Emblem
	}

	private static final long serialVersionUID = 1L;
	private String name;
	private String id;
	private String url;
	private List<String> supertypes;
	private List<String> types;
	private List<String> subtypes;
	private List<String> colors;
	private List<MagicCardNames> foreignNames;
	private Integer cmc;
	private String cost;
	private String text;
	private List<MagicEdition> editions;
	private String originalText;
	private String originalType;
	private String power;
	private String toughness;
	private Integer loyalty;
	private String artist;
	private String flavor;
	private List<MagicRuling> rulings;
	private String number;
	private List<Integer> variations;
	private List<String> colorIdentity;
	private String watermarks;
	private String layout;
	private Integer multiverseid;
	private List<MagicFormat> legalities;
	private String rarity;
	private String gathererCode;
	private String mciNumber;
	private String imageName;

	private boolean flippable = false;
	private boolean tranformable = false;

	private boolean reserved;

	public MagicEdition getCurrentSet() {
		return getEditions().get(0);
	}
	
	
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	public String getMciNumber() {
		return mciNumber;
	}

	public void setMciNumber(String mciNumber) {
		this.mciNumber = mciNumber;
	}

	public boolean isFlippable() {
		return flippable;
	}

	public void setFlippable(boolean flippable) {
		this.flippable = flippable;
	}

	public boolean isTranformable() {
		return tranformable;
	}

	public void setTranformable(boolean tranformable) {
		this.tranformable = tranformable;
	}

	public String getGathererCode() {
		return gathererCode;
	}

	public void setGathererCode(String gathererCode) {
		this.gathererCode = gathererCode;
	}

	private String rotatedCardName;

	public String getRotatedCardName() {
		return rotatedCardName;
	}

	public void setRotatedCardName(String rotatedCardName) {
		this.rotatedCardName = rotatedCardName;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public MagicCard() {
		editions = new ArrayList<>();
		types = new ArrayList<>();
		supertypes = new ArrayList<>();
		subtypes = new ArrayList<>();
		colors = new ArrayList<>();
		foreignNames = new ArrayList<>();
		rulings = new ArrayList<>();
		variations = new ArrayList<>();
		colorIdentity = new ArrayList<>();
		legalities = new ArrayList<>();

	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public String getOriginalType() {
		return originalType;
	}

	public void setOriginalType(String originalType) {
		this.originalType = originalType;
	}

	public List<MagicFormat> getLegalities() {
		return legalities;
	}

	public void setLegalities(List<MagicFormat> legalities) {
		this.legalities = legalities;
	}

	public Integer getMultiverseid() {
		return multiverseid;
	}

	public void setMultiverseid(Integer multiverseid) {
		this.multiverseid = multiverseid;
	}

	public String getWatermarks() {
		return watermarks;
	}

	public void setWatermarks(String watermarks) {
		this.watermarks = watermarks;
	}

	public List<String> getColorIdentity() {
		return colorIdentity;
	}

	public void setColorIdentity(List<String> colorIdentity) {
		this.colorIdentity = colorIdentity;
	}

	public List<Integer> getVariations() {
		return variations;
	}

	public void setVariations(List<Integer> variations) {
		this.variations = variations;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public List<MagicRuling> getRulings() {
		return rulings;
	}

	public void setRulings(List<MagicRuling> rulings) {
		this.rulings = rulings;
	}

	public Integer getLoyalty() {
		return loyalty;
	}

	public void setLoyalty(Integer loyalty) {
		this.loyalty = loyalty;
	}

	public List<MagicCardNames> getForeignNames() {
		return foreignNames;
	}

	public void setForeignNames(List<MagicCardNames> foreignNames) {
		this.foreignNames = foreignNames;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getToughness() {
		return toughness;
	}

	public void setToughness(String toughness) {
		this.toughness = toughness;
	}

	public List<String> getColors() {
		return colors;
	}

	public void setColors(List<String> colors) {
		this.colors = colors;
	}

	public Integer getCmc() {
		return cmc;
	}

	public void setCmc(Integer cmc) {
		this.cmc = cmc;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<MagicEdition> getEditions() {
		return editions;
	}

	public void setEditions(List<MagicEdition> editions) {
		this.editions = editions;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFullType() {
		StringBuilder temp = new StringBuilder();
		if (getSupertypes() != null)
			for (String s : getSupertypes())
				temp.append(s).append(" ");

		for (String s : getTypes())
			temp.append(s).append(" ");

		if (getSubtypes() != null) {
			temp.append(" - ");
			for (String s : getSubtypes())
				temp.append(s).append(" ");
		}

		return temp.toString();
	}

	public List<String> getSubtypes() {
		return subtypes;
	}

	public void setSubtypes(List<String> subtypes) {
		this.subtypes = subtypes;
	}

	public List<String> getSupertypes() {
		return supertypes;
	}

	public void setSupertypes(List<String> supertypes) {
		this.supertypes = supertypes;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getFlavor() {
		return flavor;
	}

	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public String toString() {
		return getName();
	}

	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;

		if (this.getClass() != obj.getClass())
			return false;

		return ((MagicCard) obj).getId().equals(this.getId());
	}

	public void setLayout(String layout) {
		this.layout = layout;

	}

	public String getLayout() {
		return layout;
	}

}
