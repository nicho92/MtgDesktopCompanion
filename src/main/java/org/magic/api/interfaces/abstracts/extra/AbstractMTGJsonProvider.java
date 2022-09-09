package org.magic.api.interfaces.abstracts.extra;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.ArrayUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGFinishes;
import org.magic.api.beans.enums.MTGFrameEffects;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGPromoType;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGCrit.OPERATOR;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.JsonCriteriaBuilder;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.tools.Chrono;
import org.magic.tools.FileTools;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

public abstract class AbstractMTGJsonProvider extends AbstractCardsProvider{
	protected static final String HAS_NON_FOIL = "hasNonFoil";
	protected static final String HAS_FOIL = "hasFoil";
	protected static final String MTG_ARENA_ID = "mtgArenaId";
	protected static final String HAS_CONTENT_WARNING = "hasContentWarning";
	protected static final String UUID = "uuid";
	protected static final String AVAILABILITY = "availability";
	protected static final String SCRYFALL_ID = "scryfallId";
	protected static final String PRINTINGS = "printings";
	protected static final String ARTIST = "artist";
	protected static final String TYPE = "type";
	protected static final String FOREIGN_DATA = "foreignData";
	protected static final String RULINGS = "rulings";
	protected static final String LEGALITIES = "legalities";
	protected static final String LOYALTY = "loyalty";
	protected static final String COLOR_IDENTITY = "colorIdentity";
	protected static final String COLORS = "colors";
	protected static final String TOUGHNESS = "toughness";
	protected static final String POWER = "power";
	protected static final String SUBTYPES = "subtypes";
	protected static final String TYPES = "types";
	protected static final String SUPERTYPES = "supertypes";
	protected static final String FLAVOR_TEXT = "flavorText";
	protected static final String FLAVOR_NAME = "flavorName";
	protected static final String LAYOUT = "layout";
	protected static final String IS_RESERVED = "isReserved";
	protected static final String IS_FULLART = "isFullArt";
	protected static final String FRAME_VERSION = "frameVersion";
	protected static final String CONVERTED_MANA_COST = "manaValue";
	protected static final String TEXT = "text";
	protected static final String NUMBER = "number";
	protected static final String RARITY = "rarity";
	protected static final String MULTIVERSE_ID = "multiverseId";
	protected static final String MANA_COST = "manaCost";
	protected static final String NAME = "name";
	protected static final String LANGUAGE = "language";
	protected static final String SETCODE="setCode";
	protected static final String KEYWORDS = "keywords";
	protected static final String IS_OVERSIZED = "isOversized";
	protected static final String IS_REPRINT = "isReprint";
	protected static final String IS_ONLINE_ONLY = "isOnlineOnly";
	protected static final String IS_PROMO = "isPromo";
	protected static final String IS_FOIL_ONLY = "isFoilOnly";
	protected static final String MCM_ID = "mcmId";
	protected static final String MCM_NAME = "mcmName";
	protected static final String MTGSTOCKS_ID = "mtgstocksId";
	protected static final String ORIGINAL_RELEASE_DATE="originalReleaseDate";
	protected static final String SIDE = "side";
	protected static final String WATERMARK = "watermark";
	protected static final String FRAME_EFFECTS = "frameEffects";
	protected static final String EDHREC_RANK = "edhrecRank";
	protected static final String IS_STORY_SPOTLIGHT = "isStorySpotlight";
	protected static final String HAS_ALTERNATIVE_DECK_LIMIT = "hasAlternativeDeckLimit";
	protected static final String TCGPLAYER_PRODUCT_ID = "tcgplayerProductId";
	protected static final String TCGPLAYER_GROUP_ID ="tcgplayerGroupId";
	protected static final String BORDER_COLOR = "borderColor";
	protected static final String KEYRUNE_CODE = "keyruneCode";
	protected static final String SCRYFALL_ILLUSTRATION_ID = "scryfallIllustrationId";
	protected static final String TIMESHIFTED = "isTimeshifted";
	protected static final String ISPREVIEW  = "isPartialPreview";
	protected static final String IS_FOREIGN_ONLY = "isForeignOnly";
	protected static final String PROMO_TYPE = "promoTypes";
	protected static final String COLOR_INDICATOR = "colorIndicator";
	protected static final String IS_FUNNY = "isFunny";
	protected static final String SECURITYSTAMP = "securityStamp";
	protected static final String FINISHES = "finishes";
	protected static final String IS_REBALANCED="isRebalanced";
	protected static final String SIGNATURE="signature";
			
	protected static final String FORCE_RELOAD = "FORCE_RELOAD";
	
	public static final String MTGJSON_API_URL="https://mtgjson.com/api/v5";
	public static final String MTG_JSON_DECKS =		     MTGJSON_API_URL+"/decks/";
	public static final String MTG_JSON_VERSION = 	     MTGJSON_API_URL+"/Meta.json";
	public static final String MTG_JSON_DECKS_LIST =     MTGJSON_API_URL+"/DeckList.json";
	public static final String MTG_JSON_KEYWORDS= 	     MTGJSON_API_URL+"/Keywords.json";
	public static final String MTG_JSON_SETS_LIST=	     MTGJSON_API_URL + "/SetList.json";
	public static final String MTG_JSON_ENUM_VALUES =    MTGJSON_API_URL+ "/EnumValues.json";
	public static final String MTG_JSON_ALL_PRICES_ZIP = MTGJSON_API_URL+"/AllPrices.json.zip";
	
	private File tempZipFile = new File(MTGConstants.DATA_DIR,"mtgJsonTempFile.zip");
	private File fversion = new File(MTGConstants.DATA_DIR, "mtgjsonVersion");
	
	
	protected String version;
	protected Chrono chrono = new Chrono();
		
	public abstract File getDataFile();
	public abstract String getOnlineDataFileZip();
	public abstract List<MagicCard> listToken(MagicEdition ed) throws IOException;
	public abstract MagicCard getTokenFor(MagicCard mc, MTGLayout layout) throws IOException;
	
	
	protected MTGQueryBuilder<?> queryBuilder=  new JsonCriteriaBuilder();
	
	protected void download() {
		try {
			if (hasNewVersion()||!getDataFile().exists() || getDataFile().length() == 0 || getBoolean(FORCE_RELOAD)) {
				logger.info("Downloading "+version + " datafile");
				URLTools.download(getOnlineDataFileZip(), tempZipFile);
				FileTools.unZipIt(tempZipFile,getDataFile());
				FileTools.saveFile(fversion,version);
				setProperty(FORCE_RELOAD, "false");
			}
		} catch (Exception e1) {
			logger.error(e1);
		}
		
	}
	
	@Override
	public MagicCard getCardByScryfallId(String crit) throws IOException {
		
		try {
		return searchCardByCriteria(SCRYFALL_ID, crit, null, true).get(0);
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}
	
	
	@Override
	public MagicCard getCardById(String id, MagicEdition ed) throws IOException {
		try {
			return searchCardByCriteria(UUID, id, ed, true).get(0);
		}catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	@Override
	public MagicCard getCardByArenaId(String id) {
		try{
			return searchByCriteria(new MTGCrit<>(MTG_ARENA_ID, OPERATOR.EQ, id)).get(0);
		}
		catch(Exception e)
		{
			logger.error(e);
			return null;
		}
	}
	
	
	@Override
	public List<QueryAttribute> loadQueryableAttributs() {
		
		List<QueryAttribute> arr = new ArrayList<>();
		
		for(String s :Lists.newArrayList(NAME,ARTIST,TEXT,FLAVOR_TEXT,FRAME_VERSION,MANA_COST,TYPE,FRAME_VERSION,WATERMARK))
			arr.add(new QueryAttribute(s,String.class));
		
		for(String s :Lists.newArrayList(CONVERTED_MANA_COST,POWER,TOUGHNESS,MULTIVERSE_ID,NUMBER))
			arr.add(new QueryAttribute(s,Integer.class));
		
		for(String s :Lists.newArrayList(IS_RESERVED,HAS_FOIL,HAS_NON_FOIL,IS_FUNNY))
			arr.add(new QueryAttribute(s,Boolean.class));
		
		
		
		arr.add(new QueryAttribute(LAYOUT, MTGLayout.class));
		arr.add(new QueryAttribute(RARITY, MTGRarity.class));
		arr.add(new QueryAttribute(COLOR_IDENTITY, MTGColor.class));
		arr.add(new QueryAttribute(COLORS, MTGColor.class));
		arr.add(new QueryAttribute(PROMO_TYPE, MTGPromoType.class));
		arr.add(new QueryAttribute(FRAME_EFFECTS, MTGFrameEffects.class));
		arr.add(new QueryAttribute(FINISHES, MTGFinishes.class));
		
		return arr;
	}

	
	@Override
	public String[] getLanguages() {
		
		var ret = new String[0];
		try {
			URLTools.extractAsJson(MTG_JSON_ENUM_VALUES).getAsJsonObject().get("data").getAsJsonObject().get(FOREIGN_DATA).getAsJsonObject().get(LANGUAGE).getAsJsonArray().forEach(je->ArrayUtils.add(ret, je.getAsString()));
		} catch (IOException ex) {
			logger.error(ex);
		}
		
		return ret;
	}
	

	
	
	@Override
	public MagicCard getCardByNumber(String num, MagicEdition me) throws IOException {
		
		if(me==null)
			throw new IOException("Edition must not be null");

		return searchCardByCriteria(NUMBER,num,me,true).get(0);
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = new HashMap<String,String>();	
		m.put(FORCE_RELOAD, "false");
		
		return m;
	}
	
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(new ImageIcon(AbstractCardsProvider.class.getResource("/icons/plugins/mtgjson.png")).getImage().getScaledInstance(MTGConstants.MENU_ICON_SIZE, MTGConstants.MENU_ICON_SIZE, Image.SCALE_SMOOTH));
	}
	
	@Override
	public String getVersion() {
		return version;
	}

	protected boolean hasNewVersion() {
		var temp = "";
			try  
			{
				temp = FileTools.readFile(fversion);
			}
			catch(FileNotFoundException ex)
			{
				logger.error("{} doesn't exist",fversion); 
			} catch (IOException e) {
				logger.error(e);
			}
			
			try {
				logger.debug("check new version of {} ({})",this,temp);
	
				JsonElement d = URLTools.extractAsJson(MTG_JSON_VERSION);
				version = d.getAsJsonObject().get("data").getAsJsonObject().get("version").getAsString();
				if (!version.equals(temp)) {
					logger.info("new version datafile exist ({}). Downloading it",version);
					return true;
				}

			logger.debug("check new version of {} : up to date",this);
			return false;
		} catch (Exception e) {
			version = temp;
			logger.error("Error getting last version",e);
			return false;
		}

	}

	

	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	
	
}
