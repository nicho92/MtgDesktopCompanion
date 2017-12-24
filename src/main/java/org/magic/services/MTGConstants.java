package org.magic.services;

import java.awt.Color;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.GamingRoomPanel;
import org.magic.gui.CollectionPanelGUI;
import org.magic.gui.DeckBuilderGUI;
import org.magic.gui.MagicGUI;
import org.magic.gui.StockPanelGUI;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.dashlet.BestCardsDashlet;
import org.magic.gui.dashlet.BoosterBoxDashlet;
import org.magic.gui.dashlet.TrendingDashlet;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.gui.renderer.ManaCellRenderer;

public class MTGConstants {

	public static String MTG_DESKTOP_ISSUES_URL = "https://github.com/nicho92/MtgDesktopCompanion/issues";
	public static String MTG_DESKTOP_WIKI_URL = "https://github.com/nicho92/MtgDesktopCompanion/wiki";
	public static String MTG_DESKTOP_UPDATE_URL = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/data/version";
	public static String MTG_DESKTOP_APP_ZIP = "https://github.com/nicho92/MtgDesktopCompanion/blob/master/executable/mtgcompanion.zip?raw=true";
	public static String MTG_BOOSTERS_URI = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/data/boosters.xml";
	public static int MTG_DESKTOP_TABBED_POSITION = JTabbedPane.LEFT;
	public static String TABLE_ALTERNATE_ROW_COLOR="#E1E4F2";
	public static Color THUMBNAIL_BACKGROUND_COLOR=SystemColor.windowBorder;
	
	
	public static Color COLLECTION_100PC=Color.GREEN;
	public static Color COLLECTION_90PC=new Color(188,245,169);
	public static Color COLLECTION_50PC=Color.ORANGE;
	public static Color COLLECTION_5PC=Color.YELLOW;
	
	public static final String KEYSTORE_NAME = "jssecacerts"; 
	public static final String KEYSTORE_PASS = "changeit";
	public static final String MTG_TEMPLATES_DIR = "./templates";

	public static final ImageIcon ICON_EXPORT = new ImageIcon(MTGConstants.class.getResource("/icons/export.png"));
	public static final ImageIcon ICON_SEARCH=new ImageIcon(MTGConstants.class.getResource("/icons/search.png"));
	public static final ImageIcon ICON_FILTER = new ImageIcon(MTGConstants.class.getResource("/icons/filter.png"));
	public static final ImageIcon ICON_CLEAR = new ImageIcon(MTGConstants.class.getResource("/icons/09_clear_location.png"));
	public static final ImageIcon ICON_LOADING = new ImageIcon(MTGConstants.class.getResource("/icons/load.gif"));
	public static final ImageIcon ICON_IMPORT = new ImageIcon(MTGConstants.class.getResource("/icons/import.png"));
	public static final ImageIcon ICON_EURO = new ImageIcon(MTGConstants.class.getResource("/icons/euro.png"));
	public static final ImageIcon ICON_NEW = new ImageIcon(MTGConstants.class.getResource("/icons/new.png"));
	public static final ImageIcon ICON_REFRESH = new ImageIcon(MTGConstants.class.getResource("/icons/refresh.png"));
	public static final ImageIcon ICON_DELETE = new ImageIcon(MTGConstants.class.getResource("/icons/delete.png"));
	public static final ImageIcon ICON_CHECK = new ImageIcon(MTGConstants.class.getResource("/icons/check.png"));
	public static final ImageIcon ICON_WEBSITE = new ImageIcon(MTGConstants.class.getResource("/icons/website.png"));
	public static final ImageIcon ICON_MANUAL = new ImageIcon(MTGConstants.class.getResource("/icons/manual.png"));
	public static final ImageIcon ICON_SAVE = new ImageIcon(MTGConstants.class.getResource("/icons/save.png"));
	public static final ImageIcon ICON_SEARCH_2 = new ImageIcon(MTGConstants.class.getResource("/icons/search.gif"));
	public static final ImageIcon ICON_COLLECTION = new ImageIcon(MTGConstants.class.getResource("/icons/collection.png"));
	public static final ImageIcon ICON_GAME_HAND = new ImageIcon(MTGConstants.class.getResource("/icons/hand.png"));
	public static final ImageIcon ICON_GAME_LIBRARY = new ImageIcon(MTGConstants.class.getResource("/icons/librarysize.png"));
	public static final ImageIcon ICON_GAME_PLANESWALKER = new ImageIcon(MTGConstants.class.getResource("/icons/planeswalker.png"));
	public static final ImageIcon ICON_GAME_LIFE = new ImageIcon(MTGConstants.class.getResource("/icons/heart.png"));
	public static final ImageIcon ICON_GAME_POISON = new ImageIcon(MTGConstants.class.getResource("/icons/poison.png"));
	public static final ImageIcon ICON_COLLECTION_SMALL = new ImageIcon(MTGConstants.class.getResource("/icons/bottom.png"));
	public static final ImageIcon ICON_COLORS = new ImageIcon(MTGConstants.class.getResource("/icons/colors.gif"));
	public static final ImageIcon ICON_ALERT = new ImageIcon(MTGConstants.class.getResource("/icons/bell.png"));
	public static final ImageIcon ICON_UP = new ImageIcon(MTGConstants.class.getResource("/icons/up.png"));
	public static final ImageIcon ICON_DOWN = new ImageIcon(MTGConstants.class.getResource("/icons/down.png"));
	public static final ImageIcon ICON_DOLLARS = new ImageIcon(MTGConstants.class.getResource("/icons/dollars.png"));
	public static final ImageIcon ICON_DASHBOARD = new ImageIcon(MTGConstants.class.getResource("/icons/dashboard.png"));
	public static final ImageIcon ICON_OPEN = new ImageIcon(MTGConstants.class.getResource("/icons/open.png"));
	public static final ImageIcon ICON_DECK = new ImageIcon(MTGConstants.class.getResource("/icons/book_icon.jpg"));
	public static final ImageIcon ICON_STOCK = new ImageIcon(MTGConstants.class.getResource("/icons/stock.png"));
	public static final ImageIcon ICON_SHOP =  new ImageIcon(MTGConstants.class.getResource("/icons/shop.png"));
	public static final ImageIcon ICON_BUILDER = new ImageIcon(MTGConstants.class.getResource("/icons/create.png"));
	public static final ImageIcon ICON_RSS = new ImageIcon(MTGConstants.class.getResource("/icons/rss.png"));
	public static final ImageIcon ICON_CONFIG = new ImageIcon(MTGConstants.class.getResource("/icons/build.png"));
	
	public static final Image IMAGE_LOGO = Toolkit.getDefaultToolkit().getImage(MTGConstants.class.getResource("/icons/logo.gif"));
	public static final URL URL_MANA_SYMBOLS = MTGConstants.class.getResource("/icons/Mana.png");
	
	public static final URL URL_MANA_GOLD = ManaCellRenderer.class.getResource("/icons/gold.png");
	public static final URL URL_MANA_INCOLOR = ManaCellRenderer.class.getResource("/icons/uncolor.jpg");
	public static final URL URL_COLLECTION = ManaCellRenderer.class.getResource("/icons/bottom.png");
	
	
}
