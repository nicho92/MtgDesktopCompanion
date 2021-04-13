package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.jayway.jsonpath.JsonPath;

public class MTGJsonPricer {
	
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public static void main(String[] args) throws JsonParseException, IOException {
		File f = new File("C:\\Users\\Pihen\\OneDrive - Ville de Boulogne-Billancourt\\test.json");
		MTGControler.getInstance();
		new MTGJsonPricer(f);
	}
	
	public MTGJsonPricer(File f) throws IOException 
	{
		List<Map<String, Object>> books =  JsonPath.parse(f).read("$.data.00010d56-fe38-5e35-8aed-518019aa36a5.paper.cardkingdom.retail.foil");
		books.stream().forEach(m->m.entrySet().stream().forEach(System.out::println));
	}

	private void buildPrice(JsonReader reader) throws IOException {
		while(reader.hasNext())
		{
			logger.debug("Card ID = " + reader.nextName());
			reader.beginObject();
			while(reader.hasNext())
			{
				logger.debug("support="+reader.nextName());
				reader.beginObject();
				while(reader.hasNext())
				{
					String name =reader.nextName();
					if(name.equals("currency"))
					{
						reader.nextName();
						logger.debug("currency=" + reader.nextString());
					}
					else 
					{
						reader.beginObject();
						logger.debug("format =" + reader.nextName());
						while(reader.hasNext())
						{
							reader.beginObject();
							while(reader.hasNext())
							{
								logger.debug(reader.nextName() + " "+  reader.nextDouble());
							}
						}
					}
				}
			}
		}
	}

}

class Meta
{
	private String date;
	private String version;
	
	@Override
	public String toString() {
		return getDate() + " " + getVersion();
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
} 


class MTGJsonPrice
{
	String mtgjsonId;
}

