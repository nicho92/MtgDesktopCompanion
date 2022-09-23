package org.magic.services.adapters;

import java.lang.reflect.Type;

import org.magic.api.beans.technical.audit.NetworkInfo;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NetworkInfoAdapter implements JsonSerializer<NetworkInfo>, JsonDeserializer<NetworkInfo>
	{
	  @Override
	  public JsonElement serialize(NetworkInfo src, Type typeOfSrc, JsonSerializationContext context)
	  {
	    return src.toJson();
	  }

	  @Override
	  public NetworkInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	  {
	    var ni = new NetworkInfo();
	    ni.fromJson(json.getAsJsonObject());
	    return ni;
	  }

}