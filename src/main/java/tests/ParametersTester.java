package tests;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.wallpaper.impl.WizardsOfTheCoastWallpaperProvider;
import org.magic.services.MTGLogger;

public class ParametersTester {

	private FileBasedConfigurationBuilder<PropertiesConfiguration> builder;
	private Parameters params;
	private Configuration config;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	public void setProperty(String k, String v)
	{
		config.setProperty(k, v);
	}

	
	
	public void load()
	{
		try {
			config=builder.getConfiguration();
		} catch (ConfigurationException e) {
			logger.error(e);
		}
		
	}
	
	public void setProperty(String k, Object o)
	{
		config.setProperty(k, o);
	}
	
	public void save()
	{
		try {
			builder.save();
		} catch (ConfigurationException e) {
			logger.error(e);
		}
	}
	
	
	public ParametersTester(MTGPlugin plugin)
	{
		load();
	}
	
	
	

	public static void main(String[] args)
	{
		ParametersTester test = new ParametersTester(new WizardsOfTheCoastWallpaperProvider());
		test.setProperty("foo", "bar");
		test.save();
		
	}
}
