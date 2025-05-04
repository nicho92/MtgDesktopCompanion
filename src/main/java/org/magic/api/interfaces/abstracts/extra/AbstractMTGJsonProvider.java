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

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.enums.EnumCardVariation;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.enums.EnumSecurityStamp;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGCrit.OPERATOR;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.JsonCriteriaBuilder;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.tools.Chrono;
import org.magic.services.tools.FileTools;

import com.google.common.collect.Lists;

public abstract class AbstractMTGJsonProvider extends AbstractCardsProvider{
	protected static final String BOOSTER_INDEX = "boosterIndex";
	protected static final String BOOSTER_SHEET_NAME = "sheetName";
	protected static final String BOOSTER_CARD_WEIGHT = "cardWeight";
	protected static final String BOOSTER_WEIGHT = "boosterWeight";
	protected static final String BOOSTER_SHEET_PICKS = "sheetPicks";
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
	protected static final String FACENAME = "faceName";
	protected static final String LANGUAGE = "language";
	protected static final String SETCODE="setCode";
	protected static final String KEYWORDS = "keywords";
	protected static final String IS_OVERSIZED = "isOversized";
	protected static final String IS_REPRINT = "isReprint";
	protected static final String IS_ONLINE_ONLY = "isOnlineOnly";
	protected static final String IS_PROMO = "isPromo";
	protected static final String IS_FOIL_ONLY = "isFoilOnly";
	protected static final String IS_ALTERNATIVE="isAlternative";
	protected static final String MCM_ID = "mcmId";
	protected static final String MCM_NAME = "mcmName";
	protected static final String MTGSTOCKS_ID = "mtgstocksId";
	protected static final String ORIGINAL_RELEASE_DATE="originalReleaseDate";
	protected static final String SIDE = "side";
	protected static final String PARENT_CODE="parentCode";
	protected static final String WATERMARK = "watermark";
	protected static final String FRAME_EFFECTS = "frameEffects";
	protected static final String EDHREC_RANK = "edhrecRank";
	protected static final String IS_STORY_SPOTLIGHT = "isStorySpotlight";
	protected static final String HAS_ALTERNATIVE_DECK_LIMIT = "hasAlternativeDeckLimit";
	protected static final String TCGPLAYER_PRODUCT_ID = "tcgplayerProductId";
	protected static final String TCGPLAYER_GROUP_ID ="tcgplayerGroupId";
	protected static final String CARDKINGDOM_PRODUCT_ID = "cardKingdomId";
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
	protected static final String ASCII_NAME="asciiName";
	protected static final String ATTRACTION_LIGHTS="attractionLights";
	protected static final String SUBSETS="subsets";
	protected static final String DEFENSE="defense";
	

	protected static final String FORCE_RELOAD = "FORCE_RELOAD";

	public static final String MTGJSON_API_URL="https://mtgjson.com/api/v5";
	public static final String MTG_JSON_DECKS =		     MTGJSON_API_URL+"/decks/";
	public static final String MTG_JSON_VERSION = 	     MTGJSON_API_URL+"/Meta.json";
	public static final String MTG_JSON_DECKS_LIST =     MTGJSON_API_URL+"/DeckList.json";
	public static final String MTG_JSON_KEYWORDS= 	     MTGJSON_API_URL+"/Keywords.json";
	public static final String MTG_JSON_SETS_LIST=	     MTGJSON_API_URL + "/SetList.json";
	public static final String MTG_JSON_ENUM_VALUES =    MTGJSON_API_URL+ "/EnumValues.json";
	public static final String MTG_JSON_ALL_PRICES_ZIP = MTGJSON_API_URL+"/AllPrices.json.zip";
	public static final String MTG_JSON_PRODUCTS ="https://github.com/mtgjson/mtg-sealed-content/raw/main/outputs/products.json";
	
	
	private File tempZipFile = new File(MTGConstants.DATA_DIR,"mtgJsonTempFile.zip");
	private File fversion = new File(MTGConstants.DATA_DIR, "mtgjsonVersion");


	protected String version;
	protected Chrono chrono = new Chrono();

	protected abstract File getDataFile();
	protected abstract String getOnlineDataFileZip();
	public abstract List<MTGCard> listToken(MTGEdition ed) throws IOException;
	public abstract MTGCard getTokenFor(MTGCard mc, EnumLayout layout) throws IOException;


	protected MTGQueryBuilder<?> queryBuilder=  new JsonCriteriaBuilder();

	protected void download() {
		try {
			if (hasNewVersion()||!getDataFile().exists() || getDataFile().length() == 0 || getBoolean(FORCE_RELOAD)) {
				logger.info("Downloading {} datafile",version);
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
	public MTGCard getCardByScryfallId(String crit) throws IOException {

		try {
		return searchCardByCriteria(SCRYFALL_ID, crit, null, true).get(0);
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}

	
	@Override
	public MTGCard getCardById(String id) throws IOException {
		try {
			return searchCardByCriteria(UUID, id, null, true).get(0);
		}catch(IndexOutOfBoundsException _)
		{
			return null;
		}
	}
	
	@Override
	public MTGCard getCardByArenaId(String id) {
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
	public List<MTGCard> searchCardByName(String name, MTGEdition me, boolean exact) throws IOException {
		return searchCardByName(name, me, exact,null);
	}

	@Override
	public List<MTGCard> searchCardByName(String name, MTGEdition me, boolean exact, EnumCardVariation extra) throws IOException{

		var ret = searchCardByCriteria(NAME,name, me, exact,extra);
		
		if(ret.isEmpty())
			ret = searchCardByCriteria(FACENAME,name, me, exact,extra);
		
		if(ret.isEmpty())
			ret = searchCardByCriteria(ASCII_NAME,name, me, false,extra);
		
		return ret;
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



		arr.add(new QueryAttribute(LAYOUT, EnumLayout.class));
		arr.add(new QueryAttribute(RARITY, EnumRarity.class));
		arr.add(new QueryAttribute(COLOR_IDENTITY, EnumColors.class));
		arr.add(new QueryAttribute(COLORS, EnumColors.class));
		arr.add(new QueryAttribute(PROMO_TYPE, EnumPromoType.class));
		arr.add(new QueryAttribute(FRAME_EFFECTS, EnumFrameEffects.class));
		arr.add(new QueryAttribute(FINISHES, EnumFinishes.class));
		arr.add(new QueryAttribute(KEYWORDS, MTGKeyWord.class));
		arr.add(new QueryAttribute(SECURITYSTAMP, EnumSecurityStamp.class));
		return arr;
	}


	@Override
	public List<String> loadCardsLangs() {

		var ret = new ArrayList<String>();
		URLTools.extractAsJson(MTG_JSON_ENUM_VALUES).getAsJsonObject().get("data").getAsJsonObject().get(FOREIGN_DATA).getAsJsonObject().get(LANGUAGE).getAsJsonArray().forEach(je->ret.add(je.getAsString()));
		return ret;
	}




	@Override
	public MTGCard getCardByNumber(String num, MTGEdition me) throws IOException {

		if(me==null)
			throw new IOException("Edition must not be null");

		return searchCardByCriteria(NUMBER,num,me,true).get(0);
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = new HashMap<String,MTGProperty>();
		m.put(FORCE_RELOAD, MTGProperty.newBooleanProperty("false", "force data reloading at initialisation. Will be back to false after update"));
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
			catch(FileNotFoundException _)
			{
				logger.error("{} doesn't exist",fversion);
			} catch (IOException e) {
				logger.error(e);
			}

			try {
				logger.debug("check new version of {} ({})",this,temp);

				var d = URLTools.extractAsJson(MTG_JSON_VERSION);
				version = d.getAsJsonObject().get("data").getAsJsonObject().get("version").getAsString();
				if (!version.equals(temp)) {
					logger.info("new version datafile exist ({}).",version);
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
