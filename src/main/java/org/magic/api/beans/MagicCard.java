package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGFrameEffects;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.tools.IDGenerator;

public class MagicCard implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name="";
	private String id;
	private String url;
	private List<String> supertypes;
	private List<String> types;
	private List<String> subtypes;
	private List<MTGColor> colors;
	private List<MagicCardNames> foreignNames;
	private Integer cmc;
	private String cost="";
	private String text="";
	private List<MagicEdition> editions;
	private String originalText="";
	private String originalType="";
	private String power="";
	private String toughness="";
	private Integer loyalty;
	private String artist="";
	private String flavor="";
	private List<MagicRuling> rulings;
	private List<MTGColor> colorIdentity;
	private String watermarks;
	private MTGLayout layout=MTGLayout.NORMAL;
	private List<MagicFormat> legalities;
	private MTGRarity rarity;
	private String gathererCode;
	private String imageName;
	private String rotatedCardName;
	private boolean reserved;
	private String frameVersion;
	private Integer tcgPlayerId;
	private Integer mtgstocksId;
	private Integer mkmId;
	private Integer edhrecRank;
	private boolean oversized;
	private List<MTGFrameEffects> frameEffects;
	
	
	public boolean isOversized() {
		return oversized;
	}
	
	public void setOversized(boolean oversized) {
		this.oversized = oversized;
	}
	
	public void setFrameEffects(List<MTGFrameEffects> frameEffects) {
		this.frameEffects = frameEffects;
	}
	
	public List<MTGFrameEffects> getFrameEffects() {
		return frameEffects;
	}
	
	
	public String getFlavor() {
		return flavor;
	}
	
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}
	
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public Integer getEdhrecRank() {
		return edhrecRank;
	}

	public void setEdhrecRank(Integer edhrecRank) {
		this.edhrecRank = edhrecRank;
	}

	public void setTcgPlayerId(Integer tcgPlayerId) {
		this.tcgPlayerId = tcgPlayerId;
	}

	public void setMtgstocksId(int mtgstocksId) {
		this.mtgstocksId = mtgstocksId;
	}

	public void setMkmId(Integer mkmId) {
		this.mkmId = mkmId;
	}

	public Integer getMtgstocksId() {
		return mtgstocksId;
	}

	public Integer getMkmId() {
		return mkmId;
	}

	public void setMkmId(int mkmId) {
		this.mkmId = mkmId;
	}

	public Integer getTcgPlayerId() {
		return tcgPlayerId;
	}

	public void setTcgPlayerId(int tcgPlayerId) {
		this.tcgPlayerId = tcgPlayerId;
	}

	public String getFrameVersion() {
		return frameVersion;
	}

	public void setFrameVersion(String frameVersion) {
		this.frameVersion = frameVersion;
	}


	public MagicEdition getCurrentSet() {
		if(!getEditions().isEmpty())
			return getEditions().get(0);
		
		return null;
	}
	
	public boolean isInstant()
	{
		return getTypes().toString().toLowerCase().contains("instant");
	}
	
	public boolean isRitual()
	{
		return getTypes().toString().toLowerCase().contains("sorcery");
	}
	
	public boolean isCreature()
	{
		return getTypes().toString().toLowerCase().contains("creature");
	}
	
	public boolean isEnchantment()
	{
		return getTypes().toString().toLowerCase().contains("enchantment");
	}
	
	public boolean isPermanent()
	{
		return isLand()||isPlaneswalker()||isCreature()|| isEnchantment()||isArtifact();
	}
	
	public boolean isArtifact() {
		return getTypes().toString().toLowerCase().contains("artifact");
	}

	public boolean isPlaneswalker()
	{
		return getTypes().toString().toLowerCase().contains("planeswalker");
	}
	
	public boolean isDoubleFaced()
	{
		return getLayout()==MTGLayout.MELD || getLayout()==MTGLayout.TRANSFORM;
	}
	
	
	public static boolean isBasicLand(String cardName)
	{
		return (cardName.trim().equalsIgnoreCase("Plains")  || 
				cardName.trim().equalsIgnoreCase("Island")  || 
				cardName.trim().equalsIgnoreCase("Swamp")   || 
				cardName.trim().equalsIgnoreCase("Mountain")|| 
				cardName.trim().equalsIgnoreCase("Forest") ||
				cardName.trim().equalsIgnoreCase("Wastes") ||
				cardName.trim().equalsIgnoreCase("Snow-Covered Plains") ||
				cardName.trim().equalsIgnoreCase("Snow-Covered Island") ||
				cardName.trim().equalsIgnoreCase("Snow-Covered Swamp") ||
				cardName.trim().equalsIgnoreCase("Snow-Covered Mountain") ||
				cardName.trim().equalsIgnoreCase("Snow-Covered Forest")
				);
	}
	
	public boolean isBasicLand()
	{
		return isBasicLand(getName());
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

	public boolean isFlippable() {
		return getLayout()==MTGLayout.FLIP;
	}


	public String getGathererCode() {
		return gathererCode;
	}

	public void setGathererCode(String gathererCode) {
		this.gathererCode = gathererCode;
	}


	public String getRotatedCardName() {
		return rotatedCardName;
	}
	
	public void setRotatedCardName(String rotatedCardName) {
		this.rotatedCardName = rotatedCardName;
	}

	public MTGRarity getRarity() {
		return rarity;
	}

	public void setRarity(MTGRarity rarity) {
		this.rarity = rarity;
	}
	
	public void setRarity(String r) {
		try {
			rarity = MTGRarity.valueOf(r.toUpperCase());
		}
		catch(Exception e)
		{
			rarity=null;
		}
	}

	public MagicCard() {
		editions = new ArrayList<>();
		types = new ArrayList<>();
		supertypes = new ArrayList<>();
		subtypes = new ArrayList<>();
		colors = new ArrayList<>();
		foreignNames = new ArrayList<>();
		rulings = new ArrayList<>();
		colorIdentity = new ArrayList<>();
		legalities = new ArrayList<>();
		frameEffects = new ArrayList<>();
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

	public String getWatermarks() {
		return watermarks;
	}

	public void setWatermarks(String watermarks) {
		this.watermarks = watermarks;
	}
	
	public List<MTGColor> getColorIdentity() {
		return colorIdentity;
	}

	public void setColorIdentity(List<MTGColor> colorIdentity) {
		this.colorIdentity = colorIdentity;
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

	public List<MTGColor> getColors() {
		return colors;
	}

	public void setColors(List<MTGColor> colors) {
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
		if (!getSupertypes().isEmpty())
			for (String s : getSupertypes())
				temp.append(s).append(" ");

		for (String s : getTypes())
			temp.append(s).append(" ");

		if (!getSubtypes().isEmpty()) {
			temp.append("- ");
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

		return IDGenerator.generate(((MagicCard) obj)).equals(IDGenerator.generate(this));
	}

	public void setLayout(MTGLayout layout) {
		this.layout = layout;

	}

	public MTGLayout getLayout() {
		return layout;
	}

	public boolean isLand() {
		return getTypes().toString().toLowerCase().contains("land");
	}
	
	public boolean isLegendary() {
		return getSupertypes().toString().toLowerCase().contains("legendary");
	}

	public MagicCard toForeign(MagicCardNames fn)
	{
		try {
			MagicCard mc2 = new MagicCard();
			MagicEdition ed = new MagicEdition();
			
			BeanUtils.copyProperties(mc2,this);
			BeanUtils.copyProperties(ed,this.getCurrentSet());

			mc2.setName(fn.getName());
			mc2.setEditions(new ArrayList<>(getEditions()));
			mc2.getEditions().set(0, ed);
			ed.setMultiverseid(String.valueOf(fn.getGathererId()));
			mc2.getCurrentSet().setMultiverseid(String.valueOf(fn.getGathererId()));
			mc2.getCurrentSet().setFlavor(fn.getFlavor());
			mc2.setText(fn.getText());
			return mc2;
			
		} catch (Exception e) {
			return null;
		}
	}


}
