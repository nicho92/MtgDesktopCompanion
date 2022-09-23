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

	private static final String CLASS_LOADER_NAME = "classLoaderName";
	private static final String MODULE_NAME = "moduleName";
	private static final String LINE_NUMBER = "lineNumber";
	private static final String METHOD_NAME = "methodName";
	private static final String CLASS_NAME = "className";
	private static final String FILE_NAME = "fileName";

	@Override
	public StackTraceElement deserialize(JsonElement je, Type arg1, JsonDeserializationContext ctx) throws JsonParseException {

		var obj = je.getAsJsonObject();
		var fName="";
		if(obj.get(FILE_NAME)!=null)
			fName=obj.get(FILE_NAME).getAsString();

		return new StackTraceElement(obj.get(CLASS_NAME).getAsString(), obj.get(METHOD_NAME).getAsString(), fName, obj.get(LINE_NUMBER).getAsInt());

	}

	@Override
	public JsonElement serialize(StackTraceElement je, Type arg1, JsonSerializationContext ctx) {
		var obj = new JsonObject();
			obj.addProperty(CLASS_NAME, je.getClassName());
			obj.addProperty(METHOD_NAME, je.getMethodName());
			obj.addProperty(LINE_NUMBER, je.getLineNumber());
			obj.addProperty(FILE_NAME, je.getFileName());
			obj.addProperty(MODULE_NAME, je.getModuleName());
			obj.addProperty(CLASS_LOADER_NAME, je.getClassLoaderName());
		return obj;
	}

}
