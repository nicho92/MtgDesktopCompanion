package org.magic.services.adapters;

import java.lang.reflect.Type;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGStockItem;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class MTGStockItemAdapter implements JsonDeserializer<MTGStockItem>, JsonSerializer<MTGStockItem> {

    @Override
	public MTGStockItem deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
      try {
    	  return context.deserialize(elem, typeForName(EnumItems.valueOf(elem.getAsJsonObject().get("product").getAsJsonObject().get("typeProduct").getAsString())));
      }catch(Exception e)
      {
    	  return context.deserialize(elem, typeForName(EnumItems.SEALED));
      }
    }

    private Type typeForName(final EnumItems t) {
    	if(t.equals(EnumItems.CARD))
    		return MTGCardStock.class;

    	return MTGSealedStock.class;

    }

	@Override
	public JsonElement serialize(MTGStockItem src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src);
	}

}