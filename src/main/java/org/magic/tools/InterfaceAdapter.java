package org.magic.tools;

import java.lang.reflect.Type;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.EnumItems;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public final class InterfaceAdapter<T> implements JsonDeserializer<T> {
  
    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
       return context.deserialize(elem, typeForName(EnumItems.valueOf(elem.getAsJsonObject().get("typeStock").getAsString())));
    }

    private Type typeForName(final EnumItems t) {
        	if(t.equals(EnumItems.CARD))
        		return MagicCardStock.class;
        	
        	return SealedStock.class;
  
    }

}