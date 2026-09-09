package org.magic.composer.adapters;

import java.lang.reflect.Type;

import org.magic.composer.layer.BorderLayer;
import org.magic.composer.layer.FrameLayer;
import org.magic.composer.layer.Layer;
import org.magic.composer.layer.TextLayer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class LayoutAdapter implements JsonDeserializer<Layer> {

	@Override
	public Layer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		
			switch(json.getAsJsonObject().get("layerType").getAsString())
			{
				case "BorderLayer" : return context.deserialize(json, BorderLayer.class);
				case "FrameLayer" : return context.deserialize(json, FrameLayer.class);
				case "TextLayer" : return context.deserialize(json, TextLayer.class);
			}
			
			throw new JsonParseException("Unknown layer type: " + json.getAsJsonObject().get("layerType").getAsString());
		
	}

}
