package org.magic.services.adapters;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class StackTraceElementAdapter implements JsonSerializer<StackTraceElement>, JsonDeserializer<StackTraceElement>	{

	@Override
	public StackTraceElement deserialize(JsonElement je, Type arg1, JsonDeserializationContext ctx) throws JsonParseException {
		
		var obj = je.getAsJsonObject();
		
		return new StackTraceElement(obj.get("className").getAsString(), obj.get("methodName").getAsString(), obj.get("fileName").getAsString() , obj.get("lineNumber").getAsInt());
		
	}

	@Override
	public JsonElement serialize(StackTraceElement je, Type arg1, JsonSerializationContext ctx) {
		var obj = new JsonObject();
			obj.addProperty("className", je.getClassName());
			obj.addProperty("methodName", je.getMethodName());
			obj.addProperty("lineNumber", je.getLineNumber());
			obj.addProperty("fileName", je.getFileName());
			obj.addProperty("moduleName", je.getModuleName());
			obj.addProperty("classLoaderName", je.getClassLoaderName());
		return obj;
	}

}
