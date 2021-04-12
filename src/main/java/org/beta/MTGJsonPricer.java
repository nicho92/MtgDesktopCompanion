package org.beta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.magic.api.beans.HistoryPrice;
import org.magic.services.MTGLogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


public class MTGJsonPricer {
	
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public static void main(String[] args) throws JsonParseException, IOException {
		File f = new File("C:\\Users\\Pihen\\Downloads\\AllPrices.json");
		new MTGJsonPricer(f);
	}
	
	public MTGJsonPricer(File f) throws IOException {

		Gson gson = new GsonBuilder().create();
		
		try(JsonReader reader = new JsonReader(new FileReader(f)))
		{
			reader.beginObject();
			
			if("meta".equalsIgnoreCase(reader.nextName()))
				logger.debug(gson.fromJson(reader, Meta.class));
			
			if("data".equalsIgnoreCase(reader.nextName()))
			{
				while (reader.hasNext()) {
					JsonToken t = reader.peek();
					switch(t)
					{
					case BEGIN_OBJECT:reader.beginObject();break;
		            case END_OBJECT:reader.endObject();break;
		            case BEGIN_ARRAY:reader.beginArray();break;
		            case END_ARRAY:reader.endArray();break;
		            case NAME:System.out.println(reader.nextName());break;
					case BOOLEAN:System.out.println(reader.nextBoolean());break;
					case END_DOCUMENT:break;
					case NULL:reader.nextNull();break;
					case NUMBER:System.out.println(reader.nextDouble());break;
					case STRING:System.out.println(reader.nextString());break;
					default:break;
						
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

