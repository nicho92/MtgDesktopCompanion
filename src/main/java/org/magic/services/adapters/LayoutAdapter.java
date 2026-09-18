package org.magic.services.adapters;

import java.lang.reflect.Type;

import org.magic.api.beans.layer.BorderLayer;
import org.magic.api.beans.layer.FrameLayer;
import org.magic.api.beans.layer.IllustrationLayer;
import org.magic.api.beans.layer.ManaLayer;
import org.magic.api.beans.layer.TextLayer;
import org.magic.api.interfaces.extra.Layer;

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
				case "IllustrationLayer" : return context.deserialize(json, IllustrationLayer.class);
				case "ManaLayer" : return context.deserialize(json, ManaLayer.class);
			}
			
			throw new JsonParseException("Unknown layer type: " + json.getAsJsonObject().get("layerType").getAsString());
		
	}

}
