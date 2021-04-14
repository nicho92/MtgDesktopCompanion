package org.beta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class MTGJsonPricer {
	
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public static void main(String[] args) throws IOException {
	//	File f = new File("d:\\Téléchargements\\AllPrices.json");
		File f = new File("C:\\Users\\Nicolas\\OneDrive - Ville de Boulogne-Billancourt\\test.json");
		new MTGJsonPricer(f);
	}
	

	public MTGJsonPricer(File f) throws IOException 
	{
		Gson gson = new GsonBuilder().create();
		try(JsonReader reader = new JsonReader(new FileReader(f)))
		{
			reader.beginObject();
			
			if("meta".equalsIgnoreCase(reader.nextName()))
				logger.debug("META" + gson.fromJson(reader, Meta.class));
			
			if("data".equalsIgnoreCase(reader.nextName()))
			{
				reader.beginObject();
				buildPrice(reader);
			}
		}
		
		
		
		
		
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
					logger.debug("provider="+reader.nextName());
					reader.beginObject();
					while(reader.hasNext())
					{
						logger.debug("stock="+reader.nextName());
						reader.beginObject();
						while(reader.hasNext())
						{
							logger.debug("format="+reader.nextName());
							reader.beginObject();
							while(reader.hasNext())
							{
								if(reader.peek()==JsonToken.NAME)
								{ 
									String name =reader.nextName();
									logger.debug("currency=" + reader.nextString());
								}
								else 
								{
									reader.beginObject();
									while(reader.hasNext())
									{
										logger.debug(reader.nextName());
										logger.debug(reader.nextDouble());
									}
									reader.endObject();
								}
							}
							reader.endObject();
						}
						reader.endObject();
					}
					reader.endObject();
				}
				reader.endObject();
			}
			reader.endObject();
		}
		reader.endObject();
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

