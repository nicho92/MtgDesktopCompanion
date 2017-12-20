package org.magic.services;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.JTabbedPane;

public class MTGConstants {

	public static String MTG_DESKTOP_ISSUES_URL = "https://github.com/nicho92/MtgDesktopCompanion/issues";
	public static String MTG_DESKTOP_WIKI_URL = "https://github.com/nicho92/MtgDesktopCompanion/wiki";
	public static String MTG_DESKTOP_UPDATE_URL = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/data/version";
	public static String MTG_DESKTOP_APP_ZIP = "https://github.com/nicho92/MtgDesktopCompanion/blob/master/executable/mtgcompanion.zip?raw=true";
	public static String MTG_BOOSTERS_URI = "https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/data/boosters.xml";
	public static int MTG_DESKTOP_TABBED_POSITION = JTabbedPane.LEFT;
	
	
	public static Color COLLECTION_100PC=Color.GREEN;
	public static Color COLLECTION_90PC=new Color(188,245,169);
	public static Color COLLECTION_50PC=Color.ORANGE;
	public static Color COLLECTION_5PC=Color.YELLOW;
	
	public static final String KEYSTORE_NAME = "jssecacerts"; 
	public static final String KEYSTORE_PASS = "changeit";
	public static final String MTG_TEMPLATES_DIR = "./templates";
	

	
	public static Color THUMBNAIL_BACKGROUND_COLOR=SystemColor.windowBorder;
	
}
