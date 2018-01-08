package org.magic.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class MTGStringUtil {

	public static void prettyPrint(String json)
	{
		 Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	        JsonParser jp = new JsonParser();
	        System.out.println(gson.toJson(jp.parse(json)));
	}
	
}
