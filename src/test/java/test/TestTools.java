package test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Level;
import org.magic.api.beans.MagicCard;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class TestTools {

	public static List<MagicCard> loadData() throws IOException, URISyntaxException
	{
		return new JsonExport().importDeck(new File(TestTools.class.getResource("/sample.json").toURI())).getAsList();
		
	}
	
	public static void initTest()
	{
//		MTGConstants.CONF_DIR = new File(System.getProperty("user.home") + "/.magicDeskCompanion-test/");
//		MTGConstants.DATA_DIR = new File(MTGConstants.CONF_DIR.getAbsolutePath(),"data");
		MTGLogger.changeLevel(Level.OFF);
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
	}
	
	
}
