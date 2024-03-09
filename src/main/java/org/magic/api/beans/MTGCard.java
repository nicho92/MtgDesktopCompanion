package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.abstracts.AbstractProduct;
import org.magic.api.beans.enums.EnumBorders;
import org.magic.api.beans.enums.EnumCardVariation;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.enums.EnumSecurityStamp;
import org.magic.services.tools.CryptoUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class MTGCard extends AbstractProduct {
	private static final long serialVersionUID = 1L;

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
				cardName.trim().equalsIgnoreCase("Snow-Covered Forest") ||
				cardName.trim().equalsIgnoreCase("Snow-Covered Wastes")
				);
	}
	private boolean arenaCard;
	private String artist="";
	private String asciiName;
	private EnumBorders border;
	private Integer cmc;
	private List<EnumColors> colorIdentity;
	private List<EnumColors> colorIndicator;
	private List<EnumColors> colors;
	private String cost="";
	private Map<String,String> customMetadata;
	private Date dateUpdated;
	private Integer defense;
	private Integer edhrecRank;
	private List<MTGEdition> editions;
	private List<EnumFinishes> finishes;
	private String flavor="";
	private String flavorName;
	private List<MTGCardNames> foreignNames;
	private List<EnumFrameEffects> frameEffects;
	private String frameVersion;
	private String gathererCode;
	private boolean hasAlternativeDeckLimit;
	private boolean hasContentWarning;
	private String id;
	private boolean isFunny;
	private boolean isRebalanced;
	private boolean isStorySpotlight;
	private List<MTGKeyWord> keywords;
	private EnumLayout layout=EnumLayout.NORMAL;
	private List<MTGFormat> legalities;
	private Integer loyalty;
	private Integer mkmId;
	private Integer mtgArenaId;
	private boolean mtgoCard;
	private Integer mtgstocksId;
	private boolean onlineOnly;
	private String originalReleaseDate;
	private boolean oversized;
	private String power="";
	private boolean promoCard;
	private List<EnumPromoType> promotypes;
	private EnumRarity rarity;
	private boolean reprintedCard;
	private boolean reserved;
	private MTGCard rotatedCard;
	private List<MTGRuling> rulings;
	private String scryfallId;
	private String scryfallIllustrationId;
	private EnumSecurityStamp securityStamp;
	private String side="a";
	private String signature;
	private List<String> subtypes;
	private List<String> supertypes;
	private Integer tcgPlayerId;
	private String text="";
	private String toughness="";
	private List<String> types;
	private String number="";
	@SerializedName(alternate = "multiverse_id", value = "multiverseId") private String multiverseid;
	private String watermarks;
	private boolean fullArt;
	private boolean japanese;
	private boolean timeshifted;
	private boolean retro;
	
	
	
	public MTGCard() {
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
		keywords = new ArrayList<>();
		promotypes = new ArrayList<>();
		colorIndicator = new ArrayList<>();
		finishes = new ArrayList<>();
		customMetadata = new HashMap<>();
		setTypeProduct(EnumItems.CARD);
	}
	
	@Override
	public boolean equals(Object obj) {

		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}

		return CryptoUtils.generateCardId(((MTGCard) obj)).equals(CryptoUtils.generateCardId(this));
	}
	
	public String getMultiverseid() {
		return multiverseid;
	}
	
	public void setMultiverseid(String multiverseid) {
		this.multiverseid = multiverseid;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	

	public String getArtist() {
		return artist;
	}
	
	public String getAsciiName() {
		return asciiName;
	}
	
	
	public EnumBorders getBorder() {
		return border;
	}
	
	public Integer getCmc() {
		return cmc;
	}
	
	public List<EnumColors> getColorIdentity() {
		return colorIdentity;
	}

	public List<EnumColors> getColorIndicator() {
		return colorIndicator;
	}

	public List<EnumColors> getColors() {
		return colors;
	}

	public String getCost() {
		return cost;
	}

	@Deprecated(since = "2.43", forRemoval = true) 
	public MTGEdition getCurrentSet() {
		if(!getEditions().isEmpty()) {
			return getEditions().get(0);
		}

		return null;
	}

	@Override
	public MTGEdition getEdition() {
		if(super.getEdition()==null)
			return getCurrentSet();
		
		return super.getEdition();
	}
	
	
	public Map<String, String> getCustomMetadata() {
		return customMetadata;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}


	public Integer getDefense() {
		return defense;
	}

	public Integer getEdhrecRank() {
		return edhrecRank;
	}


	public List<MTGEdition> getEditions() {
		return editions;
	}


	public List<EnumCardVariation> getExtra()
	{
		var ret = new ArrayList<EnumCardVariation>();
		if(isJapanese()) {
			ret.add(EnumCardVariation.JAPANESEALT);
		}
		if(isShowCase()) {
			ret.add(EnumCardVariation.SHOWCASE);
		}
		if(isFullArt()) {
			ret.add(EnumCardVariation.FULLART);
		}
		if(isExtendedArt()) {
			ret.add(EnumCardVariation.EXTENDEDART);
		}
		if(isBorderLess()) {
			ret.add(EnumCardVariation.BORDERLESS);
		}
		if(isTimeshifted()) {
			ret.add(EnumCardVariation.TIMESHIFTED);
		}
		if(isRetro()) {
			ret.add(EnumCardVariation.RETRO);
		}


		return ret;
	}
	
	
	public boolean isRetro()
	{
		return retro;
	}
	
	public void setRetro(boolean retro) {
		this.retro = retro;
	}

	public List<EnumFinishes> getFinishes() {
		return finishes;
	}

	public String getFlavor() {
		return flavor;
	}

	public String getFlavorName() {
		return flavorName;
	}

	public List<MTGCardNames> getForeignNames() {
		return foreignNames;
	}

	public List<EnumFrameEffects> getFrameEffects() {
		return frameEffects;
	}

	public String getFrameVersion() {
		return frameVersion;
	}

	public String getFullName()
	{
		if(getRotatedCard()!=null) {
			return getName() + " // "+ getRotatedCard().getName();
		} else {
			return getName();
		}
	}

	public String getFullType() {
		var temp = new StringBuilder();
		if (!getSupertypes().isEmpty()) {
			for (String s : getSupertypes()) {
				temp.append(s).append(" ");
			}
		}

		for (String s : getTypes()) {
			temp.append(s).append(" ");
		}

		if (!getSubtypes().isEmpty()) {
			temp.append("- ");
			for (String s : getSubtypes()) {
				temp.append(s).append(" ");
			}
		}

		return temp.toString().trim();
	}

	public String getGathererCode() {
		return gathererCode;
	}

	public String getId() {
		return id;
	}

	public List<MTGKeyWord> getKeywords() {
		return keywords;
	}

	public EnumLayout getLayout() {
		return layout;
	}

	public List<MTGFormat> getLegalities() {
		return legalities;
	}

	public Integer getLoyalty() {
		return loyalty;
	}

	public Integer getMkmId() {
		return mkmId;
	}

	public Integer getMtgArenaId() {
		return mtgArenaId;
	}


	public Integer getMtgstocksId() {
		return mtgstocksId;
	}

	public String getOriginalReleaseDate() {
		return originalReleaseDate;
	}


	public String getPower() {
		return power;
	}


	public List<EnumPromoType> getPromotypes() {
		return promotypes;
	}

	public EnumRarity getRarity() {
		return rarity;
	}



	public MTGCard getRotatedCard() {
		return rotatedCard;
	}

	public List<MTGRuling> getRulings() {
		return rulings;
	}

	public String getScryfallId() {
		return scryfallId;
	}

	public String getScryfallIllustrationId() {
		return scryfallIllustrationId;
	}

	public EnumSecurityStamp getSecurityStamp() {
		return securityStamp;
	}

	public String getSide() {
		return side;
	}


	public String getSignature() {
		return signature;
	}


	@Override
	public String getStoreId() {
		return CryptoUtils.generateCardId(this);
	}

	public List<String> getSubtypes() {
		return subtypes;
	}

	public List<String> getSupertypes() {
		return supertypes;
	}

	public Integer getTcgPlayerId() {
		return tcgPlayerId;
	}

	public String getText() {
		return text;
	}

	public String getToughness() {
		return toughness;
	}

	public List<String> getTypes() {
		return types;
	}

	public String getWatermarks() {
		return watermarks;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public boolean isArenaCard() {
		return arenaCard;
	}

	public boolean isArtifact() {
		return getTypes().toString().toLowerCase().contains("artifact");
	}

	public boolean isBasicLand(){
		return isBasicLand(getName());
	}

	public boolean isBorderLess()
	{
		return border == EnumBorders.BORDERLESS;
	}

	public boolean isColorless() {
		return getColors().isEmpty();
	}

	public boolean isCompanion()
	{
		return isCreature() && getFrameEffects().contains(EnumFrameEffects.COMPANION);
	}

	public boolean isCreature()
	{
		return getTypes().toString().toLowerCase().contains("creature");
	}

	public boolean isDoubleFaced()
	{
		return getLayout()==EnumLayout.MELD || getLayout()==EnumLayout.TRANSFORM || getLayout()==EnumLayout.MODAL_DFC;
	}

	public boolean isEmblem()
	{
		return getLayout()==EnumLayout.EMBLEM;
	}

	public boolean isEnchantment()
	{
		return getTypes().toString().toLowerCase().contains("enchantment");
	}

	public boolean isExtendedArt() {
		return frameEffects.stream().anyMatch(f->f==EnumFrameEffects.EXTENDEDART);
	}

	public boolean isExtraCard()
	{

		try {
			var n = Integer.parseInt(getNumber());

			if(n==0 || getEdition().getCardCountOfficial()==0) {
				return false;
			}

			return n>getEdition().getCardCountOfficial();
			}
		catch(Exception e)
		{
			return false;
		}
	}

	public boolean isFlippable() {
		return getLayout()==EnumLayout.FLIP;
	}

	public boolean isFullArt() {
		return fullArt;
	}

	public boolean isFunny() {
		return isFunny;
	}

	public boolean isHasAlternativeDeckLimit() {
		return hasAlternativeDeckLimit;
	}

	public boolean isHasContentWarning() {
		return hasContentWarning;
	}


	public boolean isInstant()
	{
		return getTypes().toString().toLowerCase().contains("instant");
	}

	public boolean isJapanese() {
		return japanese;
	}

	public boolean isLand() {
		return getTypes().toString().toLowerCase().contains("land");
	}
	public boolean isLegendary() {
		return getSupertypes().toString().toLowerCase().contains("legendary");
	}

	public boolean isMainFace() {
		return (getSide()==null || getSide().equals("a"));
	}

	public boolean isMtgoCard() {
		return mtgoCard;
	}

	public boolean isMultiColor()
	{
		return getColors().size()>1;
	}

	public boolean isOnlineOnly() {
		return onlineOnly;
	}

	public boolean isOversized() {
		return oversized;
	}

	public boolean isPermanent()
	{
		return isLand()||isPlaneswalker()||isCreature()|| isEnchantment()||isArtifact() || isToken() || isEmblem();
	}

	public boolean isPlaneswalker()
	{
		return getTypes().toString().toLowerCase().contains("planeswalker");
	}

	public boolean isPromoCard() {
		return promoCard;
	}

	public boolean isRebalanced() {
		return isRebalanced;
	}

	public boolean isReprintedCard() {
		return reprintedCard;
	}

	public boolean isReserved() {
		return reserved;
	}


	public boolean isRitual()
	{
		return getTypes().toString().toLowerCase().contains("sorcery");
	}

	public boolean isShowCase() {
		return frameEffects.stream().anyMatch(f->f==EnumFrameEffects.SHOWCASE);
	}

	public boolean isSiege()
	{
		return getSubtypes().toString().toLowerCase().contains("siege");
	}



	public boolean isSpecialTokenOrExtra()
	{
		return getLayout()==EnumLayout.ADVENTURE || getLayout()==EnumLayout.ART_SERIES || isToken() || isEmblem();
	}

	public boolean isStorySpotlight() {
		return isStorySpotlight;
	}

	public boolean isTimeshifted() {
		return timeshifted;
	}

	public boolean isToken()
	{
		return getLayout()==EnumLayout.TOKEN || getLayout()==EnumLayout.DOUBLE_FACED_TOKEN;
	}

	public void setArenaCard(boolean arenaCard) {
		this.arenaCard = arenaCard;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setAsciiName(String asciiName) {
		this.asciiName = asciiName;
	}

	public void setBorder(EnumBorders border) {
		this.border = border;
	}



	public void setCmc(Integer cmc) {
		this.cmc = cmc;
	}

	
	public void setColorIdentity(List<EnumColors> colorIdentity) {
		this.colorIdentity = colorIdentity;
	}
	
	public void setColorIndicator(List<EnumColors> colorIndicator) {
		this.colorIndicator = colorIndicator;
	}

	public void setColors(List<EnumColors> colors) {
		this.colors = colors;
	}


	public void setCost(String cost) {
		this.cost = cost;
	}

	public void setCustomMetadata(Map<String, String> customMetadata) {
		this.customMetadata = customMetadata;
	}


	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public void setDefense(Integer defense) {
		this.defense = defense;
	}

	public void setEdhrecRank(Integer edhrecRank) {
		this.edhrecRank = edhrecRank;
	}


	public void setEditions(List<MTGEdition> editions) {
		this.editions = editions;
	}

	public void setFinishes(List<EnumFinishes> finishes) {
		this.finishes = finishes;
	}


	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public void setFlavorName(String flavorName) {
		this.flavorName = flavorName;
	}

	public void setForeignNames(List<MTGCardNames> foreignNames) {
		this.foreignNames = foreignNames;
	}

	public void setFrameEffects(List<EnumFrameEffects> frameEffects) {
		this.frameEffects = frameEffects;
	}



	public void setFrameVersion(String frameVersion) {
		this.frameVersion = frameVersion;
	}

	public void setFullArt(boolean fullArt) {
		this.fullArt = fullArt;
	}

	public void setFunny(boolean isFunny) {
		this.isFunny = isFunny;
	}

	public void setGathererCode(String gathererCode) {
		this.gathererCode = gathererCode;
	}

	public void setHasAlternativeDeckLimit(boolean hasAlternativeDeckLimit) {
		this.hasAlternativeDeckLimit = hasAlternativeDeckLimit;
	}

	public void setHasContentWarning(boolean hasContentWarning) {
		this.hasContentWarning = hasContentWarning;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setJapanese(boolean japanese) {
		this.japanese = japanese;
	}

	public void setKeywords(List<MTGKeyWord> keywords) {
		this.keywords = keywords;
	}

	public void setLayout(EnumLayout layout) {
		this.layout = layout;

	}

	public void setLegalities(List<MTGFormat> legalities) {
		this.legalities = legalities;
	}

	public void setLoyalty(Integer loyalty) {
		this.loyalty = loyalty;
	}

	public void setMkmId(int mkmId) {
		this.mkmId = mkmId;
	}

	public void setMkmId(Integer mkmId) {
		this.mkmId = mkmId;
	}

	public void setMtgArenaId(Integer mtgArenaId) {
		this.mtgArenaId = mtgArenaId;
	}

	public void setMtgoCard(boolean mtgoCard) {
		this.mtgoCard = mtgoCard;
	}

	public void setMtgstocksId(int mtgstocksId) {
		this.mtgstocksId = mtgstocksId;
	}

	public void setMtgstocksId(Integer mtgstocksId) {
		this.mtgstocksId = mtgstocksId;
	}

	public void setOnlineOnly(boolean onlineOnly) {
		this.onlineOnly = onlineOnly;
	}

	public void setOriginalReleaseDate(String originalReleaseDate) {
		this.originalReleaseDate = originalReleaseDate;
	}

	public void setOversized(boolean oversized) {
		this.oversized = oversized;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public void setPromoCard(boolean promoCard) {
		this.promoCard = promoCard;
	}

	public void setPromotypes(List<EnumPromoType> promotypes) {
		this.promotypes = promotypes;
	}

	public void setRarity(EnumRarity rarity) {
		this.rarity = rarity;
	}

	public void setRebalanced(boolean isRebalanced) {
		this.isRebalanced = isRebalanced;
	}

	public void setReprintedCard(boolean reprintedCard) {
		this.reprintedCard = reprintedCard;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	public void setRotatedCard(MTGCard rotatedCard) {
		this.rotatedCard = rotatedCard;
	}

	public void setRulings(List<MTGRuling> rulings) {
		this.rulings = rulings;
	}

	public void setScryfallId(String scryfallId) {
		this.scryfallId = scryfallId;
		setUrl("https://api.scryfall.com/cards/"+scryfallId+"?format=image");
	}

	public void setScryfallIllustrationId(String scryfallIllustrationId) {
		this.scryfallIllustrationId = scryfallIllustrationId;
	}

	public void setSecurityStamp(EnumSecurityStamp securityStamp) {
		this.securityStamp = securityStamp;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setStorySpotlight(boolean isStorySpotlight) {
		this.isStorySpotlight = isStorySpotlight;
	}

	public void setSubtypes(List<String> subtypes) {
		this.subtypes = subtypes;
	}

	public void setSupertypes(List<String> supertypes) {
		this.supertypes = supertypes;
	}

	public void setTcgPlayerId(int tcgPlayerId) {
		this.tcgPlayerId = tcgPlayerId;
	}

	public void setTcgPlayerId(Integer tcgPlayerId) {
		this.tcgPlayerId = tcgPlayerId;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTimeshifted(boolean timeshifted) {
		this.timeshifted = timeshifted;
	}

	public void setToughness(String toughness) {
		this.toughness = toughness;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void setWatermarks(String watermarks) {
		this.watermarks = watermarks;
	}

	public MTGCard toForeign(MTGCardNames fn)
	{
		try {
			var mc2 = new MTGCard();
			var ed = new MTGEdition();

			BeanUtils.copyProperties(mc2,this);
			BeanUtils.copyProperties(ed,this.getEdition());

			mc2.setName(fn.getName());
			mc2.setEditions(new ArrayList<>(getEditions()));
			mc2.getEditions().set(0, ed);
			mc2.setMultiverseid(String.valueOf(fn.getGathererId()));
			mc2.setFlavor(fn.getFlavor());

			mc2.setText(fn.getText());
			return mc2;

		} catch (Exception e) {
			return null;
		}
	}

	
	public JsonObject toLightJson() {
		var obj = new JsonObject();
				obj.addProperty("id", getId());
				obj.addProperty("name", getName());
				obj.addProperty("cost", getCost());
				obj.addProperty("type", getFullType());
				obj.addProperty("text", getText());
				
				if(getEdition()!=null) {
					obj.addProperty("set", getEdition().getSet());
					obj.addProperty("setId", getEdition().getId());
					obj.addProperty("setSize", getEdition().getCardCountOfficial());
					obj.addProperty("number", getNumber());
					obj.addProperty("keyrune", getEdition().getKeyRuneCode());
					obj.addProperty("multiverse", getMultiverseid());
				}
				obj.addProperty("scryfallId", getScryfallId());
				obj.addProperty("showcase", isShowCase());
				obj.addProperty("extendedArt", isExtendedArt());
				obj.addProperty("borderless", isBorderLess());
				obj.addProperty("retro", isRetro());
				obj.addProperty("doubleFaced", isDoubleFaced());
				obj.addProperty("timeshifted", isTimeshifted());
				
				
				if(getRotatedCard()!=null) {
					obj.add("otherSide", getRotatedCard().toLightJson());
				}

		return obj;


	}

	@Override
	public String toString() {
		return getName();
	}



}
