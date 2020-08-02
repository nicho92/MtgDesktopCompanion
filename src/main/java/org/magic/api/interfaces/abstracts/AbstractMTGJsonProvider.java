package org.magic.api.interfaces.abstracts;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.criterias.CardAttribute;
import org.magic.api.criterias.JsonCriteriaBuilder;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.services.MTGConstants;
import org.magic.tools.Chrono;
import org.magic.tools.FileTools;
import org.magic.tools.URLTools;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

public abstract class AbstractMTGJsonProvider extends AbstractCardsProvider{

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
	protected static final String ORIGINAL_TYPE = "originalType";
	protected static final String ORIGINAL_TEXT = "originalText";
	protected static final String FLAVOR_TEXT = "flavorText";
	protected static final String LAYOUT = "layout";
	protected static final String IS_RESERVED = "isReserved";
	protected static final String IS_FULLART = "isFullArt";
	protected static final String FRAME_VERSION = "frameVersion";
	protected static final String CONVERTED_MANA_COST = "convertedManaCost";
	protected static final String TEXT = "text";
	protected static final String NUMBER = "number";
	protected static final String RARITY = "rarity";
	protected static final String MULTIVERSE_ID = "multiverseId";
	protected static final String MANA_COST = "manaCost";
	protected static final String NAME = "name";
	protected static final String LANGUAGE = "language";
	protected static final String SETCODE="setCode";
	protected static final String FORCE_RELOAD = "FORCE_RELOAD";
	protected static final String KEYWORDS = "keywords";
	protected static final String IS_OVERSIZED = "isOversized";
	protected static final String IS_REPRINT = "isReprint";
	protected static final String WATERMARK = "watermark";

	
	public static final String URL_DECKS_URI = "https://mtgjson.com/json/decks/";
	public static final String MTG_JSON_VERSION = "https://mtgjson.com/api/v5/Meta.json";
	public static final String MTG_JSON_DECKS_LIST = "https://mtgjson.com/api/v5/DeckList.json";
	public static final String MTG_JSON_KEYWORDS="https://mtgjson.com/api/v5/Keywords.json";
	public static final String MTG_JSON_SETS_LIST="https://mtgjson.com/api/v5/SetList.json";
	
	private File tempZipFile = new File(MTGConstants.DATA_DIR,"mtgJsonTempFile.zip");
	private File fversion = new File(MTGConstants.DATA_DIR, "mtgjsonVersion");
	
	
	protected String version;
	protected Chrono chrono = new Chrono();
		
	public abstract File getDataFile();
	public abstract String getOnlineDataFileZip();
	
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
	
	public MTGQueryBuilder<?> getMTGQueryManager() {
		MTGQueryBuilder<?> b= new JsonCriteriaBuilder();

		initBuilder(b);
		
		return b; 
	}

	
	@Override
	public List<CardAttribute> loadQueryableAttributs() {
		
		List<CardAttribute> arr = new ArrayList<>();
		
		for(String s :Lists.newArrayList(NAME,ARTIST,TEXT,FLAVOR_TEXT,FRAME_VERSION,MANA_COST,"type","jsonpath"))
			arr.add(new CardAttribute(s,String.class));
		
		for(String s :Lists.newArrayList(CONVERTED_MANA_COST,POWER,TOUGHNESS,MULTIVERSE_ID,NUMBER))
			arr.add(new CardAttribute(s,Integer.class));
		
		for(String s :Lists.newArrayList(IS_RESERVED,"hasFoil","hasNonFoil"))
			arr.add(new CardAttribute(s,Boolean.class));
		
		
		
		arr.add(new CardAttribute(LAYOUT, MTGLayout.class));
		arr.add(new CardAttribute(RARITY, MTGRarity.class));
		arr.add(new CardAttribute(COLOR_IDENTITY, MTGColor.class));
		arr.add(new CardAttribute(COLORS, MTGColor.class));
		
		return arr;
	}

	
	@Override
	public String[] getLanguages() {
		return new String[] { "English", "Spanish", "French", "German", "Italian", "Portuguese", "Japanese", "Korean", "Russian", "Simplified Chinese","Traditional Chinese","Hebrew","Latin","Ancient Greek", "Arabic", "Sanskrit","Phyrexian" };
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
	public MagicCard getCardByNumber(String num, MagicEdition me) throws IOException {
		
		if(me==null)
			throw new IOException("Edition must not be null");

		return searchCardByCriteria(NUMBER,num,me,true).get(0);
	}
	
	@Override
	public void initDefault() {
		setProperty(FORCE_RELOAD, "false");
	}
	
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(new ImageIcon(AbstractCardsProvider.class.getResource("/icons/plugins/mtgjson.png")).getImage().getScaledInstance(MTGConstants.MENU_ICON_SIZE, MTGConstants.MENU_ICON_SIZE, Image.SCALE_SMOOTH));
	}
	
	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://mtgjson.com");
	}


	protected boolean hasNewVersion() {
		String temp = "";
			try  
			{
				temp = FileTools.readFile(fversion);
			}
			catch(FileNotFoundException ex)
			{
				logger.error(fversion + " doesn't exist"); 
			} catch (IOException e) {
				logger.error(e);
			}
			
			try {
				logger.debug("check new version of " + toString() + " (" + temp + ")");
	
				JsonElement d = URLTools.extractJson(MTG_JSON_VERSION);
				version = d.getAsJsonObject().get("data").getAsJsonObject().get("version").getAsString();
				if (!version.equals(temp)) {
					logger.info("new version datafile exist (" + version + "). Downloading it");
					return true;
				}

			logger.debug("check new version of " + this + ": up to date");
			return false;
		} catch (Exception e) {
			version = temp;
			logger.error("Error getting last version ",e);
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
