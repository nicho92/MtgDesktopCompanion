package org.magic.services;

import java.awt.Color;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXSearchField.SearchMode;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsExport;

import freemarker.template.Configuration;
import freemarker.template.Version;

public class MTGConstants {

	private MTGConstants() {
	}
	public static final String SSL_PROTO = "TLS";
	public static final String CONF_FILENAME = "mtgcompanion-conf.xml";
	public static final String CONF_JSON_BOOSTER = "/data/keywords.json";
	public static final File CONF_DIR = new File(System.getProperty("user.home") + "/.magicDeskCompanion/");
	public static final File MTG_DECK_DIRECTORY = new File(MTGConstants.CONF_DIR, "decks");
	public static final File MTG_WALLPAPER_DIRECTORY = new File(MTGConstants.CONF_DIR, "downloadWallpaper");
	public static final String MTG_APP_NAME = "MTG Desktop Companion";
	public static final String MESSAGE_BUNDLE = "locales.lang";
	public static final String FONT="Tahoma";
	public static final String MTG_DESKTOP_ISSUES_URL = "https://github.com/nicho92/MtgDesktopCompanion/issues";
	public static final String MTG_DESKTOP_WIKI_URL = "https://github.com/nicho92/MtgDesktopCompanion/wiki";
	public static final String MTG_DESKTOP_POM_URL = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/pom.xml";
	public static final String MTG_DESKTOP_APP_ZIP = "https://github.com/nicho92/MtgDesktopCompanion/releases/";
	public static final String MTG_BOOSTERS_URI = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/data/boosters.xml";
	public static final URL MTG_CHROME_PLUGIN = MTGConstants.class.getResource("/mtg-chrome-companion");
	
	public static final URL MTG_BOOSTERS_LOCAL_URI = MTGConstants.class.getResource("/data/boosters.xml");	
	
	public static final String WIZARD_EVENTS_URL = "https://magic.wizards.com/en/calendar-node-field-event-date-ajax/month/";
	public static final String SET_ICON_DIR = "/icons/set/";
	public static final String COMMANDS_PACKAGE = "org.magic.api.commands.impl.";
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36";
	public static final String DEFAULT_ENCODING="UTF-8";
	
	public static final int DEFAULT_PIC_WIDTH = 223;
	public static final int DEFAULT_PIC_HEIGHT = 310;
	public static final double CARD_PICS_RATIO = 1.39;
	
	public static final int DPI=300;
	
	public static final String DEFAULT_NOTIFIER_NAME="Tray";
	

	public static final String TABLE_ALTERNATE_ROW_COLOR = "#E1E4F2";
	public static final Color THUMBNAIL_BACKGROUND_COLOR = SystemColor.windowBorder;

	public static final MTGCardsExport DEFAULT_SERIALIZER = new JsonExport();
	
	
	public static final Color COLLECTION_100PC = Color.GREEN;
	public static final Color COLLECTION_90PC = new Color(188, 245, 169);
	public static final Color COLLECTION_50PC = Color.ORANGE;
	public static final Color COLLECTION_5PC = Color.YELLOW;

	public static final String KEYSTORE_NAME = "jssecacerts";
	public static final String KEYSTORE_PASS = "changeit";

	public static final URL WEBUI_LOCATION = MTGConstants.class.getResource("/web-ui");
	public static final String MTG_TEMPLATES_DIR = "./templates";
	public static final String MTG_DESKTOP_VERSION_FILE = "/version";
	public static final String ICON_DIR="/icons";
	public static final String MTG_REPORTS_DIR = "/report";
	public static final SearchMode SEARCH_MODE = SearchMode.REGULAR;
	public static final Version FREEMARKER_VERSION=Configuration.VERSION_2_3_28;

	public static final int TREE_ROW_HEIGHT = 32;
	public static final int TABLE_ROW_HEIGHT = 18;
	public static final int TABLE_ROW_WIDTH = 18;

	
	public static final URL URL_MANA_SYMBOLS = MTGConstants.class.getResource(ICON_DIR+"/mana/Mana.png");
	public static final ImageIcon ICON_MANA_GOLD = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/mana/gold.png"));
	public static final ImageIcon ICON_MANA_INCOLOR = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/mana/uncolor.png"));



	public static final String GAME_FONT = "Tahoma";

	public static final String HTML_TAG_TABLE = "table";
	public static final String HTML_TAG_TBODY = "tbody";
	public static final String HTML_TAG_TR = "tr";
	public static final String HTML_TAG_TD = "td";
	
	

	public static final Image IMAGE_LOGO = Toolkit.getDefaultToolkit().getImage(MTGConstants.class.getResource(ICON_DIR+"/logo.png"));

	
	private static String iconPack="flat";

	
	public static final ImageIcon ICON_GAME_HAND = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/hand.png"));
	public static final ImageIcon ICON_GAME_LIBRARY = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/librarysize.png"));
	public static final ImageIcon ICON_GAME_PLANESWALKER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/planeswalker.png"));
	public static final ImageIcon ICON_GAME_LIFE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/heart.png"));
	public static final ImageIcon ICON_GAME_POISON = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/poison.png"));
	public static final ImageIcon ICON_GAME_STATIC = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/static.png"));
	public static final ImageIcon ICON_GAME_TRIGGER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/trigger.png"));
	public static final ImageIcon ICON_GAME_ACTIVATED = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/game/activated.png"));
	public static final ImageIcon ICON_GAME_COLOR = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/colors.gif"));
	
	public static final ImageIcon ICON_CHROME = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/chrome.png"));
	public static final ImageIcon ICON_RSS = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/rss.png"));
	public static final ImageIcon ICON_TWITTER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/twitter.png"));
	public static final ImageIcon ICON_FORUM = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/forum.png"));
	
	public static final ImageIcon ICON_SEARCH = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/search.png"));
	public static final ImageIcon ICON_SEARCH_24 = new ImageIcon(ICON_SEARCH.getImage().getScaledInstance(24, 24, BufferedImage.SCALE_SMOOTH));

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
	public static final ImageIcon ICON_STORY = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/stories.png"));
	public static final ImageIcon ICON_WALLPAPER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/wallpaper.png"));
	
	public static final ImageIcon ICON_EXPORT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/export.png"));
	public static final ImageIcon ICON_FILTER = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/filter.png"));
	public static final ImageIcon ICON_CLEAR = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/clear.png"));
	public static final ImageIcon ICON_LOADING = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/load.gif"));
	public static final ImageIcon ICON_IMPORT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/import.png"));
	public static final ImageIcon ICON_MASS_IMPORT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/massImport.png"));

	public static final ImageIcon ICON_EURO = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/euro.png"));
	public static final ImageIcon ICON_NEW = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/new.png"));
	public static final ImageIcon ICON_REFRESH = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/refresh.png"));
	public static final ImageIcon ICON_DELETE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/delete.png"));
	public static final ImageIcon ICON_CHECK = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/check.png"));
	public static final ImageIcon ICON_WEBSITE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/website.png"));
	public static final ImageIcon ICON_MANUAL = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/manual.png"));
	public static final ImageIcon ICON_SAVE = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/save.png"));
	public static final ImageIcon ICON_UP = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/up.png"));
	public static final ImageIcon ICON_DOWN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/down.png"));
	public static final ImageIcon ICON_DOLLARS = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/dollars.png"));
	public static final ImageIcon ICON_OPEN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/"+iconPack+"/open.png"));
	public static final ImageIcon ICON_ABOUT = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/about.jpg"));
	public static final ImageIcon ICON_SPLASHSCREEN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/magic-logo.png"));
	public static final ImageIcon ICON_DEFAULT_PLUGIN = new ImageIcon(MTGConstants.class.getResource(ICON_DIR+"/plugins/default.png"));

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
	public static final ImageIcon ICON_TAB_RESULTS=new ImageIcon(ICON_SEARCH.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_DECK = new ImageIcon(ICON_DECK.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_CHAT = new ImageIcon(ICON_FORUM.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_GAME = new ImageIcon(ICON_GAME.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_STOCK = new ImageIcon(ICON_STOCK.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_MANA = new ImageIcon(ICON_GAME_COLOR.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_SHOP = new ImageIcon(ICON_SHOP.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_WALLPAPER = new ImageIcon(ICON_WALLPAPER.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_CACHE = new ImageIcon(ICON_CLEAR.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_TAB_NEWS = new ImageIcon(ICON_NEWS.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
	public static final ImageIcon ICON_MASS_IMPORT_SMALL = new ImageIcon(ICON_MASS_IMPORT.getImage().getScaledInstance(24, 24, BufferedImage.SCALE_SMOOTH));

	public static final ImageIcon ICON_BACK = new ImageIcon(ICON_COLLECTION.getImage().getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));


	
	public static final int SEALED_SIZE = 40;
	public static final URL DEFAULT_BACK_CARD = MTGConstants.class.getResource("/icons/back.jpg");
	
	
		

	
	
	
	
	
}
