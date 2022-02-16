package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.MTGBorder;
import org.magic.api.beans.enums.MTGCardVariation;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGFinishes;
import org.magic.api.beans.enums.MTGFrameEffects;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGPromoType;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.interfaces.abstracts.extra.AbstractProduct;
import org.magic.tools.IDGenerator;

import com.google.gson.JsonObject;

public class MagicCard extends AbstractProduct {
	private static final long serialVersionUID = 1L;

	private String side="a";
	private String id;
	private Integer cmc;
	private String cost="";
	private String text="";
	private String originalText="";
	private String originalType="";
	private String power="";
	private String toughness="";
	private Integer loyalty;
	private String artist="";
	private String flavor="";
	private String watermarks;
	private MTGRarity rarity;
	private MTGLayout layout=MTGLayout.NORMAL;	
	private List<String> supertypes;
	private List<String> types;
	private List<String> subtypes;
	private List<MTGColor> colors;
	private List<MagicCardNames> foreignNames;
	private List<MagicEdition> editions;
	private List<MagicRuling> rulings;
	private List<MTGColor> colorIdentity;
	private List<MTGColor> colorIndicator;
	private List<MagicFormat> legalities;
	private List<MTGFrameEffects> frameEffects;
	private String flavorName;
	private String imageName;
	private String frameVersion;
	private Integer tcgPlayerId;
	private Integer mtgstocksId;
	private Integer mkmId;
	private Integer edhrecRank;
	private boolean reserved;
	private boolean oversized;
	private boolean mtgoCard;
	private boolean arenaCard;
	private boolean onlineOnly;
	private boolean promoCard;
	private boolean reprintedCard;
	private boolean fullArt;
	private boolean isFunny;
	private Integer mtgArenaId;
	private String scryfallIllustrationId;
	private String gathererCode;
	private String scryfallId;
	private boolean isStorySpotlight;
	private boolean hasAlternativeDeckLimit;
	private MagicCard rotatedCard;
	private List<MTGKeyWord> keywords;
	private boolean hasContentWarning;
	private MTGBorder border;
	private String originalReleaseDate;
	private boolean timeshifted;
	private List<MTGPromoType> promotypes;
	private boolean japanese;
	private Date dateUpdated;
	private List<MTGFinishes> finishes;
	private String securityStamp;
	private boolean isRebalanced;
	private String signature;
	
	public MTGCardVariation getExtra()
	{
		if(isJapanese())
			return MTGCardVariation.JAPANESEALT;
		else if(isShowCase())
			return MTGCardVariation.SHOWCASE;
		else if(isFullArt())
			return MTGCardVariation.FULLART;
		else if(isExtendedArt())
			return MTGCardVariation.EXTENDEDART;
		else if(isBorderLess())
			return MTGCardVariation.BORDERLESS;
		else if(isTimeshifted())
			return MTGCardVariation.TIMESHIFTED;
		
		
		return null;
		
	}
	
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setTcgPlayerId(Integer tcgPlayerId) {
		this.tcgPlayerId = tcgPlayerId;
	}

	public boolean isRebalanced() {
		return isRebalanced;
	}
	
	public void setRebalanced(boolean isRebalanced) {
		this.isRebalanced = isRebalanced;
	}
	
	public String getSecurityStamp() {
		return securityStamp;
	}
	
	public void setSecurityStamp(String securityStamp) {
		this.securityStamp = securityStamp;
	}
	
	
	public List<MTGFinishes> getFinishes() {
		return finishes;
	}
	
	public void setFinishes(List<MTGFinishes> finishes) {
		this.finishes = finishes;
	}
	
	
	@Override
	public String getStoreId() {
		return IDGenerator.generate(this);
	}
	
	
	public String getFullName()
	{
		if(getRotatedCard()!=null)
			return getName() + " // "+ getRotatedCard().getName();
		else
			return getName();
	}

	public boolean isFunny() {
		return isFunny;
	}

	public void setFunny(boolean isFunny) {
		this.isFunny = isFunny;
	}

	public boolean isJapanese() {
		return japanese;
	}

	public void setJapanese(boolean japanese) {
		this.japanese = japanese;
	}

	public List<MTGColor> getColorIndicator() {
		return colorIndicator;
	}

	public void setColorIndicator(List<MTGColor> colorIndicator) {
		this.colorIndicator = colorIndicator;
	}

	public List<MTGPromoType> getPromotypes() {
		return promotypes;
	}

	public void setPromotypes(List<MTGPromoType> promotypes) {
		this.promotypes = promotypes;
	}

	public boolean isTimeshifted() {
		return timeshifted;
	}

	public void setTimeshifted(boolean timeshifted) {
		this.timeshifted = timeshifted;
	}

	public boolean isExtraCard()
	{
		
		try {
			var number = Integer.parseInt(getCurrentSet().getNumber());
		
			if(number==0 || getCurrentSet().getCardCountOfficial()==0)
				return false;
			
			return number>getCurrentSet().getCardCountOfficial();
			}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public boolean isBorderLess()
	{
		return border == MTGBorder.BORDERLESS;
	}

	public boolean isShowCase() {
		return frameEffects.stream().anyMatch(f->f==MTGFrameEffects.SHOWCASE);
	}

	public boolean isExtendedArt() {
		return frameEffects.stream().anyMatch(f->f==MTGFrameEffects.EXTENDEDART);
	}
	
	public boolean isFullArt() {
		return fullArt;
	}

	public void setFullArt(boolean fullArt) {
		this.fullArt = fullArt;
	}

	
	public void setBorder(MTGBorder border) {
		this.border = border;
	}
	
	public MTGBorder getBorder() {
		return border;
	}
	
	
	public boolean isHasAlternativeDeckLimit() {
		return hasAlternativeDeckLimit;
	}
	
	
	public boolean isHasContentWarning() {
		return hasContentWarning;
	}
	
	public void setHasContentWarning(boolean hasContentWarning) {
		this.hasContentWarning = hasContentWarning;
	}
	

	
	public String getOriginalReleaseDate() {
		return originalReleaseDate;
	}

	public void setOriginalReleaseDate(String originalReleaseDate) {
		this.originalReleaseDate = originalReleaseDate;
	}

	public void setHasAlternativeDeckLimit(boolean hasAlternativeDeckLimit) {
		this.hasAlternativeDeckLimit = hasAlternativeDeckLimit;
	}
	
	public boolean isStorySpotlight() {
		return isStorySpotlight;
	}
	
	public void setStorySpotlight(boolean isStorySpotlight) {
		this.isStorySpotlight = isStorySpotlight;
	}
	
	public void setSide(String side) {
		this.side = side;
	}
	
	
	public String getSide() {
		return side;
	}
	
	
	public List<MTGKeyWord> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(List<MTGKeyWord> keywords) {
		this.keywords = keywords;
	}
	
	public String getFlavorName() {
		return flavorName;
	}

	public void setFlavorName(String flavorName) {
		this.flavorName = flavorName;
	}

	public boolean isCompanion()
	{
		return isCreature() && getFrameEffects().contains(MTGFrameEffects.COMPANION);
	}
	
	public String getScryfallId() {
		return scryfallId;
	}

	public void setScryfallId(String scryfallId) {
		this.scryfallId = scryfallId;
	}

	public Integer getMtgArenaId() {
		return mtgArenaId;
	}

	public void setMtgArenaId(Integer mtgArenaId) {
		this.mtgArenaId = mtgArenaId;
	}

	public boolean isMtgoCard() {
		return mtgoCard;
	}

	public void setMtgoCard(boolean mtgoCard) {
		this.mtgoCard = mtgoCard;
	}

	public boolean isArenaCard() {
		return arenaCard;
	}

	public void setArenaCard(boolean arenaCard) {
		this.arenaCard = arenaCard;
	}

	public boolean isOnlineOnly() {
		return onlineOnly;
	}

	public void setOnlineOnly(boolean onlineOnly) {
		this.onlineOnly = onlineOnly;
	}

	public boolean isPromoCard() {
		return promoCard;
	}

	public void setPromoCard(boolean promoCard) {
		this.promoCard = promoCard;
	}

	public boolean isReprintedCard() {
		return reprintedCard;
	}

	public void setReprintedCard(boolean reprintedCard) {
		this.reprintedCard = reprintedCard;
	}

	public String getScryfallIllustrationId() {
		return scryfallIllustrationId;
	}

	public void setScryfallIllustrationId(String scryfallIllustrationId) {
		this.scryfallIllustrationId = scryfallIllustrationId;
	}

	public void setMtgstocksId(Integer mtgstocksId) {
		this.mtgstocksId = mtgstocksId;
	}

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
	
	public boolean isMultiColor()
	{
		return getColors().size()>1;
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
		return isLand()||isPlaneswalker()||isCreature()|| isEnchantment()||isArtifact() || isToken() || isEmblem();
	}
	
	public boolean isToken()
	{
		return getLayout()==MTGLayout.TOKEN || getLayout()==MTGLayout.DOUBLE_FACED_TOKEN;
	}
	
	public boolean isEmblem()
	{
		return getLayout()==MTGLayout.EMBLEM;
	}
	
	public boolean isSpecialTokenOrExtra()
	{
		return getLayout()==MTGLayout.ADVENTURE || getLayout()==MTGLayout.ART_SERIES || isToken() || isEmblem();
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
		return getLayout()==MTGLayout.MELD || getLayout()==MTGLayout.TRANSFORM || getLayout()==MTGLayout.MODAL_DFC;
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
	
	public boolean isBasicLand(){
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


	public MagicCard getRotatedCard() {
		return rotatedCard;
	}
	
	public void setRotatedCard(MagicCard rotatedCard) {
		this.rotatedCard = rotatedCard;
	}

	public MTGRarity getRarity() {
		return rarity;
	}

	public void setRarity(MTGRarity rarity) {
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
		colorIdentity = new ArrayList<>();
		legalities = new ArrayList<>();
		frameEffects = new ArrayList<>();
		keywords = new ArrayList<>();
		promotypes = new ArrayList<>();
		colorIndicator = new ArrayList<>();
		finishes = new ArrayList<>();
		setTypeProduct(EnumItems.CARD);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullType() {
		var temp = new StringBuilder();
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

	@Override
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
			var mc2 = new MagicCard();
			var ed = new MagicEdition();
			
			BeanUtils.copyProperties(mc2,this);
			BeanUtils.copyProperties(ed,this.getCurrentSet());

			mc2.setName(fn.getName());
			mc2.setEditions(new ArrayList<>(getEditions()));
			mc2.getEditions().set(0, ed);
			ed.setMultiverseid(String.valueOf(fn.getGathererId()));
			mc2.getCurrentSet().setMultiverseid(String.valueOf(fn.getGathererId()));
			mc2.setFlavor(fn.getFlavor());
			
			mc2.setText(fn.getText());
			return mc2;
			
		} catch (Exception e) {
			return null;
		}
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public JsonObject toLightJson() {
		var obj = new JsonObject();
				obj.addProperty("id", getId());
				obj.addProperty("name", getName());
				obj.addProperty("cost", getCost());
				obj.addProperty("type", getFullType());
				obj.addProperty("text", getText());
				obj.addProperty("set", getCurrentSet().getSet());
				obj.addProperty("setId", getCurrentSet().getId());
				obj.addProperty("setSize", getCurrentSet().getCardCountOfficial());
				obj.addProperty("number", getCurrentSet().getNumber());
				obj.addProperty("keyrune", getCurrentSet().getKeyRuneCode());
				obj.addProperty("multiverse", getCurrentSet().getMultiverseid());
				obj.addProperty("scryfallId", getScryfallId());
				obj.addProperty("showcase", isShowCase());
				obj.addProperty("extendedArt", isExtendedArt());
				obj.addProperty("borderless", isBorderLess());
				obj.addProperty("doubleFaced", isDoubleFaced());
				obj.addProperty("timeshifted", isTimeshifted());
				
				if(isDoubleFaced() && isMainFace())
					obj.add("otherSide", getRotatedCard().toLightJson());
				
		return obj;
		
		
	}

	public boolean isMainFace() {
		return (getSide()==null || getSide().equals("a"));
	}


}
