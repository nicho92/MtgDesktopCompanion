package org.magic.services.adapters;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgent.MutableUserAgent;
	
public class UserAgentAdapter implements JsonSerializer<UserAgent>, JsonDeserializer<UserAgent>	{
	  @Override
	  public JsonElement serialize(UserAgent src, Type typeOfSrc, JsonSerializationContext context)
	  {
		  var objUa = new JsonObject();
		  src.toMap().entrySet().forEach(e->objUa.addProperty(e.getKey(), e.getValue()));
		  return objUa;
	  }

	  @Override
	  public UserAgent deserialize(JsonElement obj, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	  {
		  var ua = new MutableUserAgent();
		  obj.getAsJsonObject().entrySet().forEach(s->ua.set(s.getKey(), s.getValue().getAsString(), 1));
		  return ua;
	  }
	
}