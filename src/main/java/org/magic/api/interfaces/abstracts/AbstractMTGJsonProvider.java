package org.magic.api.interfaces.abstracts;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.services.MTGConstants;
import org.magic.tools.Chrono;
import org.magic.tools.FileTools;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;
import com.jayway.jsonpath.JsonPath;

public abstract class AbstractMTGJsonProvider extends AbstractCardsProvider {

	public static final String URL_JSON_VERSION = "https://mtgjson.com/json/version.json";
	public static final String URL_JSON_ALL_SETS = "https://mtgjson.com/json/AllSets.json";
	public static final String URL_JSON_SETS_LIST="https://mtgjson.com/json/SetList.json";
	public static final String URL_JSON_KEYWORDS="https://mtgjson.com/json/Keywords.json";
	public static final String URL_JSON_ALL_SETS_ZIP ="https://mtgjson.com/json/AllSets.json.zip";
	public static final String URL_JSON_DECKS_LIST = "https://mtgjson.com/json/DeckLists.json";
	public static final String URL_DECKS_URI = "https://mtgjson.com/json/decks/";
	private String version;
	private File fileSetJsonTemp = new File(MTGConstants.DATA_DIR,"AllSets-x4.json.zip");
	private File fileSetJson = new File(MTGConstants.DATA_DIR, "AllSets-x4.json");
	public static final File fversion = new File(MTGConstants.DATA_DIR, "version4");
	
	private static final String FORCE_RELOAD = "FORCE_RELOAD";

	
	
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(new ImageIcon(AbstractCardsProvider.class.getResource("/icons/plugins/mtgjson.png")).getImage().getScaledInstance(MTGConstants.MENU_ICON_SIZE, MTGConstants.MENU_ICON_SIZE, Image.SCALE_SMOOTH));

	}
	
	@Override
	public String[] getLanguages() {
		return new String[] { "English", "Spanish", "French", "German", "Italian", "Portuguese", "Japanese", "Korean", "Russian", "Simplified Chinese","Traditional Chinese","Hebrew","Latin","Ancient Greek", "Arabic", "Sanskrit","Phyrexian" };
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://mtgjson.com");
	}

	
	@Override
	public void init() {
		try {

			logger.debug("loading file " + fileSetJson);

			if (hasNewVersion()||!fileSetJson.exists() || fileSetJson.length() == 0 || getBoolean(FORCE_RELOAD)) {
				logger.info("Downloading "+version + " datafile");
				URLTools.download(URL_JSON_ALL_SETS_ZIP, fileSetJsonTemp);
				FileTools.unZipIt(fileSetJsonTemp,fileSetJson);
				FileTools.saveFile(fversion,version);
				setProperty(FORCE_RELOAD, "false");
			}
			Chrono chr = new Chrono();
			chr.start();
			logger.debug(this + " : parsing db file");
			ctx = JsonPath.parse(fileSetJson);
			logger.debug(this + " : parsing OK in " + chr.stop()+"s");
			
		} catch (Exception e1) {
			logger.error(e1);
		}
		
	}
	

	private boolean hasNewVersion() {
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
	
				JsonElement d = URLTools.extractJson(URL_JSON_VERSION);
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
	
	
}
