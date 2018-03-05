package tests;

import java.io.IOException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.pricers.impl.MagicBazarPricer;
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
		System.out.println(plugin.getConfFile().getAbsolutePath());
		params = new Parameters();
		if(!plugin.getConfFile().exists())
			try {
				FileUtils.touch(plugin.getConfFile());
				//plugin.initDefault();
			} catch (IOException e) {
				logger.error(e);
			}
		
		
		builder=new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class).configure(params.fileBased()
																									.setFile(plugin.getConfFile()));
		load();
	}
	
	
	

	public static void main(String[] args)
	{
		ParametersTester test = new ParametersTester(new MagicBazarPricer());
		test.setProperty("coucou", "tata");
		test.save();
		
	}
}
