package test.providers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.services.PluginRegistry;

import test.TestTools;

public class NotifierProviderTests {

	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
	}
	
	
	

	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGNotifier.class).forEach(p->{
			testPlugin(p);	
		});
	}
	
	
	
	
	public void testPlugin(MTGNotifier p)
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
		System.out.println("CURRENCY " + p.getFormat());
		
		
			try {
				p.send(new MTGNotification("test", "send from JUnit", MESSAGE_TYPE.WARNING));
				System.out.println("send  OK");
			} catch (IOException e) {
				System.out.println("send ERROR "+e);
				e.printStackTrace();
			}
			

			try {
				p.send("send from JUnit String");
				System.out.println("send  OK");
			} catch (IOException e) {
				System.out.println("send ERROR "+e);
				e.printStackTrace();
			}
			
	}
	
	
}
