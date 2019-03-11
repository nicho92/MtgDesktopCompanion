package test.providers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.cache.impl.FileCache;
import org.magic.api.cache.impl.JCSCache;
import org.magic.api.cache.impl.MemoryCache;
import org.magic.api.cache.impl.NoCache;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.PluginRegistry;

import test.data.LoadingData;

public class CacheProviderTests {

	private MagicCard mc;

	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		MTGConstants.CONF_DIR = new File(System.getProperty("user.home") + "/.magicDeskCompanion-test/");
		MTGLogger.changeLevel(Level.OFF);
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		mc = new LoadingData().cardsTest().get(0);
	}
	
	
	

	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGPicturesCache.class).forEach(p->{
			testPlugin(p);	
		});
	}
	
	
	
	
	
	public void testPlugin(MTGPicturesCache p)
	{
		
		System.out.println("*****************************"+p.getName());
		System.out.println("STAT "+p.getStatut());
		System.out.println("PROP "+p.getProperties());
		System.out.println("TYPE "+p.getType());
		System.out.println("ENAB "+p.isEnable());
		System.out.println("ICON "+p.getIcon());
		System.out.println("VERS "+p.getVersion());
		System.out.println("JMX NAME "+p.getObjectName());
		System.out.println("CONF FILE " + p.getConfFile());
		
		try {
			p.put(new ScryFallPicturesProvider().getPicture(mc, mc.getCurrentSet()), mc, mc.getCurrentSet());
			System.out.println("putPictures OK" );
		}
		catch(Exception e)
		{
			System.out.println("putPictures ERROR "+e );
		}
		
		
		try {
			
			p.getPic(mc, mc.getCurrentSet());
			System.out.println("getPictures OK" );
		} catch (Exception e) {
			System.out.println("getPictures ERROR "+e );
		}
		
	try {
			
			p.put(new ScryFallPicturesProvider().getPicture(mc, mc.getCurrentSet()),mc,null);
			System.out.println("setPictures Null ed OK" );
		} catch (Exception e) {
			System.out.println("setPictures Null ed ERROR "+e );
		}
		
		
		try {
			
			p.getPic(mc, null);
			System.out.println("getPictures Null ed OK" );
		} catch (Exception e) {
			System.out.println("getPictures Null ed ERROR "+e );
		}
		
		
		p.clear();

	}
	
	
}
