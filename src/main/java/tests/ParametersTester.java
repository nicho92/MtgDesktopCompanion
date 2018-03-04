package tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.magic.api.pricers.impl.EbayPricer;

public class ParametersTester {

	public static void main(String[] args) throws ConfigurationException {
		Parameters params = new Parameters();
		File propertiesFile = new File("config.properties");
	
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
		    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
		    .configure(params.fileBased()
		    .setFile(propertiesFile));
		
		if(!propertiesFile.exists())
			try {
				propertiesFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		   Configuration config = builder.getConfiguration();
		   System.out.println(config);
	
	}
}
