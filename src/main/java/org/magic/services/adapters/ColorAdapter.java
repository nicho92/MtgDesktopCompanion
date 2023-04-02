package org.magic.services.adapters;

import java.awt.Color;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color>
	{
	  @Override
	  public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context)
	  {
		  	var obj = new JsonObject();
	    	obj.addProperty("R", src.getRed());
	    	obj.addProperty("G", src.getGreen());
	    	obj.addProperty("B", src.getBlue());
	    	obj.addProperty("A", src.getAlpha());
	    	return obj;
	  }

	  @Override
	  public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	  {
		  var obj = json.getAsJsonObject();
		  
		  
	    return new Color(obj.get("R").getAsInt(), obj.get("G").getAsInt(), obj.get("B").getAsInt(), obj.get("A").getAsInt());
	  }

}