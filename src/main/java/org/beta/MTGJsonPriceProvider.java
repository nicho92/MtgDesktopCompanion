package org.beta;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class MTGJsonPriceProvider{
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public static void main(String[] args) throws IOException {
		
		new MTGJsonPriceProvider().init();
	}

	private void init() throws IOException {
		JsonFactory jsonFactory = new JsonFactory();  
		try(JsonParser jsonParser = jsonFactory.createParser(new File("D:\\Desktop\\AllPrices.json")))
		{
     
		 while (jsonParser.nextToken() != JsonToken.END_OBJECT) 
		 {
			 String fieldname = jsonParser.getCurrentName();
			 
			 if("meta".equals(fieldname))
				 parseMeta("meta",jsonParser);
			 
			 if("data".equals(fieldname))
				 parseMeta("data",jsonParser);
			 
			 
         }
		}
		
	}

	private void parseMeta(String fieldname, JsonParser jsonParser) throws IOException {
		 
		logger.debug("Parsing " + fieldname);
		if(jsonParser.nextToken() == JsonToken.START_OBJECT)
		 {
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    System.out.println(jsonParser.getText());
                }
		 }
		
	}

}
