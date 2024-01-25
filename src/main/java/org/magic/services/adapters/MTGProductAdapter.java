package org.magic.services.adapters;

import java.lang.reflect.Type;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGProduct;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class MTGProductAdapter implements JsonDeserializer<MTGProduct>, JsonSerializer<MTGProduct> {

    @Override
	public MTGProduct deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
      try {
    	  return context.deserialize(elem, typeForName(EnumItems.valueOf(elem.getAsJsonObject().get("typeProduct").getAsString())));
      }catch(Exception e)
      {
    	  return context.deserialize(elem, typeForName(EnumItems.SEALED));
      }
    }

    private Type typeForName(final EnumItems t) {
    	if(t.equals(EnumItems.CARD))
    		return MTGCard.class;

    	return MTGSealedStock.class;

    }

	@Override
	public JsonElement serialize(MTGProduct src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src);
	}

}