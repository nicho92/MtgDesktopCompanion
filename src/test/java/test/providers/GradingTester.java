package test.providers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.interfaces.MTGGraders;
import org.magic.services.tools.MTG;

import test.TestTools;

public class GradingTester {

	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
	}
	
	
	

	@Test
	public void launch() throws IOException
	{
		var grads = TestTools.loadGraderData().get("GRADING").getAsJsonObject();
		grads.entrySet().forEach(e->{
			var p = MTG.getPlugin(e.getKey(),MTGGraders.class);
			testPlugin(p);
		});
	}
	
	public void testPlugin(MTGGraders p)
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
		

	}
	
	
}
