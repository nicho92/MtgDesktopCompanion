package org.magic.services;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.util.PaintUtils;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.shop.Contact;
import org.magic.services.providers.IconsProvider;

import freemarker.template.Configuration;
import freemarker.template.Version;

public class MTGConstants {

	private MTGConstants() { }


	
//DEFAULT APPS CONSTANTS
	public static final String MTG_APP_NAME = "MTG Companion";
	public static final String COPYRIGHT_STRING="Wizards of the Coast, Magic: The Gathering, and their logos are trademarks of Wizards of the Coast LLC. \u00A9 1995-"+ Calendar.getInstance().get(Calendar.YEAR)+ " Wizards. All rights reserved. This app is not affiliated with Wizards of the Coast LLC.";
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";
	public static final Version FREEMARKER_VERSION=Configuration.VERSION_2_3_32;


//CONFIG FILES CONSTANTS
	public static final String MTG_DESKTOP_VERSION_FILE = "/version";
	public static final String CONF_FILENAME = "mtgcompanion-conf.xml";
	public static final String CONF_WEBSHOP_FILENAME = "webshop-conf.json";
	public static final File CONF_DIR = new File(SystemUtils.getUserHome() + "/.magicDeskCompanion/");
	public static final File DATA_DIR = new File(CONF_DIR,"data");
	public static final File MTG_WALLPAPER_DIRECTORY = new File(MTGConstants.DATA_DIR, "downloadWallpaper");
	public static final File NATIVE_DIR = new File("./natives");
	public static final URL TOOLTIPS_FILE = MTGConstants.class.getResource("/data/tips.properties");
	public static final URL MTG_DESKTOP_ALIASES_FILE =  MTGConstants.class.getResource("/data/pluginsAliases.json");
	public static final URL MTG_DESKTOP_SETS_FILE =  MTGConstants.class.getResource("/data/sets_codeAliases.json");
	
	
	
	public static final String ADMINUI_LOCATION = "web/admin-ui";
	public static final String WEBSHOP_LOCATION = "web/shop-ui";
	public static final String WEBCOLLECTION_LOCATION = "web/collection-ui";
	public static final String WEBTRADES_LOCATION = "web/trades-ui";
	public static final String WEBPRICES_LOCATION = "web/prices-ui";
	public static final String SET_ICON_DIR = "/icons/set/";
	public static final String MTG_CHROME_PLUGIN_DIR = "mtg-chrome-companion";
	private static final String ICON_DIR="/icons";
	public static final String MTG_REPORTS_DIR = "/report";
	public static final String MESSAGE_BUNDLE = "locales.lang";
	

//EXTERNAL URL CONSTANTS
	public static final String MTG_DESKTOP_ISSUES_URL = "https://github.com/nicho92/MtgDesktopCompanion/issues";
	public static final String MTG_DESKTOP_DONATE_URL_PAYPAL= "https://www.paypal.com/donate/?business=ZXJKNZZQ2S7US&no_recurring=0&item_name=Help+me+to+continue+MTGCompanion+developpement%2C+and+online+service+like+Discord+bot+&currency_code=EUR";
	
	public static final String MTG_DESKTOP_WIKI_URL = "https://github.com/nicho92/MtgDesktopCompanion/wiki";
	public static final String MTG_DESKTOP_WIKI_RAW_URL = "https://raw.githubusercontent.com/wiki/nicho92/MtgDesktopCompanion";
	public static final String MTG_BOOSTERS_URI = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/data/boosters.xml";
	public static final String MTG_SUPPORTERS_URI = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/data/supporters.json";
	public static final String MTG_DESKTOP_ALIASES_URL = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/data/pluginsAliases.json";
	public static final String MTG_DESKTOP_WEBSITE = "https://www.mtgcompanion.org/";
	public static final String MTG_DESKTOP_GITHUB_RELEASE_API = "https://api.github.com/repos/nicho92/MTGDesktopCompanion/releases";
	public static final String CURRENCY_API = "https://currencylayer.com/";
	
//DEFAULT CONSTANTS
	public static final Contact DEFAULT_CONTACT;
	private static final String[] DEFAULT_COLLECTIONS_NAMES = new String[] { "Library", "Needed", "For Sell", "Favorites" };

	static {
		DEFAULT_CONTACT = new Contact();
		DEFAULT_CONTACT.setId(1);
		DEFAULT_CONTACT.setName("MTG");
		DEFAULT_CONTACT.setLastName("Companion");
		DEFAULT_CONTACT.setTelephone("123456789");
		DEFAULT_CONTACT.setAddress("Somewhere");
		DEFAULT_CONTACT.setCity("In the middle of nowhere");
		DEFAULT_CONTACT.setWebsite(MTG_DESKTOP_WEBSITE);
		DEFAULT_CONTACT.setEmail("mtgdesktopcompanion@gmail.com");
		DEFAULT_CONTACT.setActive(true);
		DEFAULT_CONTACT.setEmailAccept(true);
	}

	public static String[] getDefaultCollectionsNames() {
		return DEFAULT_COLLECTIONS_NAMES;
	}
	
	
	
	
	
//PLUGINS CONSTANTS
	public static final String DEFAULT_NOTIFIER_NAME="Tray";
	public static final String EMAIL_NOTIFIER_NAME = "email";
	public static final String DEFAULT_CLIPBOARD_NAME = "clipboard";
	public static final String DEFAULT_MANUAL_IMPORT_SYNTAX = "MTGO";
	
//HTML CONSTANTS
	public static final String HTML_TAG_TABLE = "table";
	public static final String HTML_TAG_TBODY = "tbody";
	public static final String HTML_TAG_TR = "tr";
	public static final String HTML_TAG_TD = "td";
	
//TIMEOUT CONSTANTS
	public static final int CONNECTION_TIMEOUT =0;
	public static final int ROTATED_TIMEOUT = 15;
	public static final int DISCORD_MAX_CHARACTER = 2000;
	public static final int DAY_ANNOUNCES_UPDATE = 10;
	
// UI CONSTANTS
	public static final Charset DEFAULT_ENCODING=StandardCharsets.UTF_8;
	public static final Paint PICTURE_PAINTER = PaintUtils.NIGHT_GRAY;
	public static final int CONFIG_MODULE_TAB_POSITION = SwingConstants.LEFT;
	public static final int PROGRESS_BAR_SEALED_SIZE = 40;
	public static final Color THUMBNAIL_BACKGROUND_COLOR = SystemColor.windowBorder;
	public static final boolean IS_GRAPHICAL_UI = !GraphicsEnvironment.isHeadless();
	public static final int DEFAULT_PIC_WIDTH = 223;
	public static final int DEFAULT_PIC_HEIGHT = 310;
	public static final double CARD_PICS_RATIO = 1.39;
	public static final int MENU_ICON_SIZE=24;
	public static final  int TECHNICAL_REFRESH=5000;
	public static final int DPI=300;
	public static final int TREE_ROW_HEIGHT = 32;
	public static final int TABLE_ROW_HEIGHT = 18;
	public static final int TABLE_ROW_WIDTH = 18;
	public static final Font DEFAULT_FONT=new Font("Arial Unicode MS", Font.PLAIN, 12);
	public static final Color COLLECTION_100PC = new Color(115, 230, 0);
	public static final Color COLLECTION_90PC = new Color(188, 245, 169);
	public static final Color COLLECTION_50PC = Color.ORANGE;
	public static final Color COLLECTION_1PC = Color.YELLOW;
	public static final Color COLOR_UPDATE = new Color(0, 220, 0);
	
//IMAGES CONSTANTS
	public static final URL URL_MANA_SYMBOLS = MTGConstants.class.getResource(ICON_DIR+"/mana/Mana.png");
	public static final ImageIcon ICON_MANA_GOLD = getManaSymbol("gold");
	public static final ImageIcon ICON_MANA_INCOLOR = getManaSymbol("uncolor");
	public static final Image IMAGE_LOGO = Toolkit.getDefaultToolkit().getImage(MTGConstants.class.getResource(ICON_DIR+"/logo.png"));
	public static final ImageIcon ICON_LOGO = new ImageIcon(IMAGE_LOGO.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_SPLASHSCREEN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/magic-logo2.png"));
	public static final URL DEFAULT_BACK_CARD = MTGConstants.class.getResource("/icons/back.jpg");
	public static final URL ANNIVERSARY_BACK_CARD = MTGConstants.class.getResource("/icons/back30.jpg");
	public static final URL COLLECTOR_BACK_CARD = MTGConstants.class.getResource("/icons/backCE.jpg");
	public static final Image SAMPLE_PIC = Toolkit.getDefaultToolkit().getImage(MTGConstants.class.getResource("/data/sample.png"));
	
	public static ImageIcon getManaSymbol(String symb)
	{
		return new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/mana/"+symb.toLowerCase()+".png"));
	}
	
	
	
//ICONS CONSTANTS 	
	private static String iconPack="flat";

	public static final ImageIcon ICON_GAME_HAND = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/hand.png"));
	public static final ImageIcon ICON_GAME_LIBRARY = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/librarysize.png"));
	public static final ImageIcon ICON_GAME_PLANESWALKER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/planeswalker.png"));
	public static final ImageIcon ICON_GAME_LIFE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/heart.png"));
	public static final ImageIcon ICON_GAME_POISON = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/poison.png"));
	public static final ImageIcon ICON_GAME_TRIGGER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/trigger.png"));
	public static final ImageIcon ICON_GAME_ACTIVATED = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/activated.png"));
	public static final ImageIcon ICON_GAME_COLOR = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/colors.gif"));

	public static final ImageIcon ICON_CHROME = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/chrome.png"));
	public static final ImageIcon ICON_FORUM = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/forum.png"));

	public static final ImageIcon ICON_SEARCH = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/search.png"));
	public static final ImageIcon ICON_SEARCH_24 = new ImageIcon(ICON_SEARCH.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_SEARCH_ADVANCED = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/advanced_search.png"));
	public static final ImageIcon ICON_EXIT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/exit.png"));
	public static final ImageIcon ICON_HELP = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/help.png"));
	public static final ImageIcon ICON_BUG = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/bug.png"));
	public static final ImageIcon ICON_CANCEL = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/back.png"));
	public static final ImageIcon ICON_BINDERS = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/binders.png"));
	public static final ImageIcon ICON_COLLECTION = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/collection.png"));
	public static final ImageIcon ICON_GAME = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/game.png"));
	public static final ImageIcon ICON_ALERT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/bell.png"));
	public static final ImageIcon ICON_NEWS = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/news.png"));
	public static final ImageIcon ICON_DASHBOARD = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/dashboard.png"));
	public static final ImageIcon ICON_SHOP = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/shop.png"));
	public static final ImageIcon ICON_BUILDER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/create.png"));
	public static final ImageIcon ICON_CONFIG = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/admin.png"));
	public static final ImageIcon ICON_DECK = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/deck.png"));
	public static final ImageIcon ICON_STOCK = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/stock.png"));

	public static final ImageIcon ICON_WALLPAPER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/wallpaper.png"));
	public static final ImageIcon PLAY_ICON = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/play.png"));
	public static final ImageIcon ICON_EXPORT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/export.png"));
	public static final ImageIcon ICON_FILTER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/filter.png"));
	public static final ImageIcon ICON_CLEAR = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/clear.png"));
	public static final ImageIcon ICON_LOADING = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/load.gif"));
	public static final ImageIcon ICON_IMPORT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/import.png"));
	public static final ImageIcon ICON_MASS_IMPORT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/massImport.png"));
	public static final ImageIcon ICON_GRADING = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/grade.png"));
	public static final ImageIcon ICON_GED = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/ged.png"));
	public static final ImageIcon ICON_SHORTCUT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/shortcut.png"));
	public static final ImageIcon ICON_USER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/user.png"));
	public static final ImageIcon ICON_IA = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/ia.png"));
	public static final ImageIcon ICON_INVOICE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/invoice.png"));
	public static final ImageIcon ICON_PIN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/pin.png"));
	
	public static final ImageIcon ICON_ANNOUNCES = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/announces.png"));
	public static final ImageIcon ICON_EURO = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/euro.png"));
	public static final ImageIcon ICON_NEW = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/new.png"));
	public static final ImageIcon ICON_REFRESH = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/refresh.png"));
	public static final ImageIcon ICON_DELETE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/delete.png"));
	public static final ImageIcon ICON_CHECK = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/check.png"));
	public static final ImageIcon ICON_EQUALS = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/equals.png"));
	public static final ImageIcon ICON_WEBSITE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/website.png"));
	public static final ImageIcon ICON_WEBSITE_24 = new ImageIcon(ICON_WEBSITE.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_MANUAL = new ImageIcon(new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/manual.png")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_CONTACT = new ImageIcon(ICON_USER.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_SAVE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/save.png"));
	public static final ImageIcon ICON_UP = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/up.png"));
	public static final ImageIcon ICON_DOWN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/down.png"));
	public static final ImageIcon ICON_STANDBY = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/standby.png"));
	public static final ImageIcon ICON_SCRIPT=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/script.png"));
	public static final ImageIcon ICON_RECOGNITION = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/recognition.png"));
	public static final ImageIcon ICON_RANDOM = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/random.png"));
	public static final ImageIcon ICON_WEBCAM = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/webcam.png"));
	public static final ImageIcon ICON_EVENTS = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/events.png"));
	public static final ImageIcon ICON_DOLLARS = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/dollars.png"));
	public static final ImageIcon ICON_OPEN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/open.png"));
	public static final ImageIcon ICON_COPY = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/copy.png"));
	public static final ImageIcon ICON_PASTE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/paste.png"));
	public static final ImageIcon ICON_MERGE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/merge.png"));
	public static final ImageIcon ICON_EXT_SHOP = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/extshop.png"));
	public static final ImageIcon ICON_DATABASE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/database.png"));
	public static final ImageIcon ICON_NETWORK = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/network.png"));
	public static final ImageIcon ICON_DEFAULT_PLUGIN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/default.png"));
	public static final ImageIcon ICON_PACKAGE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/package.png"));

	public static final ImageIcon ICON_TAB_CARD = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/bottom.png"));
	public static final ImageIcon ICON_TAB_SIMILARITY=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/similarity.png"));
	public static final ImageIcon ICON_TAB_NOTIFICATION=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/notify.png"));
	public static final ImageIcon ICON_TAB_THUMBNAIL=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/thumbnail.png"));
	public static final ImageIcon ICON_TAB_ANALYSE=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/analyse.png"));
	public static final ImageIcon ICON_TAB_DETAILS=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/details.png"));
	public static final ImageIcon ICON_TAB_PRICES=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/prices.png"));
	public static final ImageIcon ICON_TAB_RULES=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/rules.png"));
	public static final ImageIcon ICON_TAB_VARIATIONS=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/variation.png"));
	public static final ImageIcon ICON_TAB_JSON=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/json.png"));
	public static final ImageIcon ICON_TAB_CONSTRUCT=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/construct.png"));
	public static final ImageIcon ICON_TAB_SEALED=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/sealed.png"));
	public static final ImageIcon ICON_TAB_PICTURE=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/pictures.png"));
	public static final ImageIcon ICON_TAB_TYPE=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/types.png"));
	public static final ImageIcon ICON_TAB_RARITY=new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/rarity.png"));
	public static final ImageIcon ICON_TAB_ADMIN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/config.png"));
	public static final ImageIcon ICON_TAB_PLUGIN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/plugin.png"));
	public static final ImageIcon ICON_TAB_DAO = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/database.png"));
	public static final ImageIcon ICON_TAB_IMPORT_EXPORT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/import-export.png"));
	public static final ImageIcon ICON_TAB_SUGGESTION = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/suggestion.png"));
	public static final ImageIcon ICON_TAB_IMPORT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/import.png"));
	public static final ImageIcon ICON_TAB_SERVER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/servers.png"));
	public static final ImageIcon ICON_TAB_ACTIVESERVER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/active-server.png"));
	public static final ImageIcon ICON_TAB_SYNC = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/sync.png"));
	public static final ImageIcon ICON_TAB_DELIVERY = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/delivery.png"));
	public static final ImageIcon ICON_TAB_LOCK = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/lock.png"));

	public static final ImageIcon ICON_TAB_BACK = new ImageIcon(ICON_COLLECTION.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_IA = new ImageIcon(ICON_IA.getImage().getScaledInstance(16,16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_EXT_SHOP =  new ImageIcon(ICON_EXT_SHOP.getImage().getScaledInstance(16,16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_EVENTS = new ImageIcon(ICON_EVENTS.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_RESULTS=new ImageIcon(ICON_SEARCH.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_DECK = new ImageIcon(ICON_DECK.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_CHAT = new ImageIcon(ICON_FORUM.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_GAME = new ImageIcon(ICON_GAME.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_STOCK = new ImageIcon(ICON_STOCK.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_MANA = new ImageIcon(ICON_GAME_COLOR.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_SHOP = new ImageIcon(ICON_SHOP.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_WALLPAPER = new ImageIcon(ICON_WALLPAPER.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_CACHE = new ImageIcon(ICON_CLEAR.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_NEWS = new ImageIcon(ICON_NEWS.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_GRADING = new ImageIcon(ICON_GRADING.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_GED = new ImageIcon(ICON_GED.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_RECOGNITION = new ImageIcon(ICON_RECOGNITION.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_USER = new ImageIcon(ICON_USER.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_NETWORK = new ImageIcon(ICON_NETWORK.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_PACKAGE = new ImageIcon(ICON_PACKAGE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));


	public static final ImageIcon ICON_MASS_IMPORT_SMALL = new ImageIcon(ICON_MASS_IMPORT.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_POOL = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/pool.png"));
	public static final ImageIcon ICON_TAB_COMBO = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/tabs/combo.png"));

	public static final ImageIcon ICON_SMALL_EQUALS = new ImageIcon(ICON_EQUALS.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_SMALL_CHECK = new ImageIcon(ICON_CHECK.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_SMALL_DELETE = new ImageIcon(ICON_DELETE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_SMALL_CLEAR = new ImageIcon(ICON_CLEAR.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ICON_SMALL_HELP = new ImageIcon(ICON_HELP.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static  final ImageIcon ICON_SMALL_SCRIPT= new ImageIcon(ICON_SCRIPT.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));

	public static ImageIcon getIconFor(Class<?> c) {
		try {

			if(c==MTGCollection.class)
			{
				return ICON_TAB_BACK;
			}

			if(c==MTGEdition.class)
			{
				return IconsProvider.getInstance().get16("PMEI");
			}
			return new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/classtype/"+c.getSimpleName().toLowerCase()+".png"));
		} catch (Exception e) {
			return ICON_TAB_CARD;
		}
	}

//EnumExtra CONSTANTS
	public static final float WEIGHT_BOOSTER=28.7f;
	
	


}
