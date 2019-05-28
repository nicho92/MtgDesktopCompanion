package test.providers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.services.PluginRegistry;

import test.TestTools;

public class CacheProviderTests {

	private MagicCard mc;

	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
		mc = TestTools.loadData().get(0);
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
		
		try {
			System.out.println("Size " + p.size() );
		}
		catch(Exception e)
		{
			System.out.println("putPictures ERROR "+e );
		}
		
		
		p.clear();

	}
	
	
}
