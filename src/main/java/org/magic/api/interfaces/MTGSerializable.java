package org.magic.api.interfaces;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;


public interface MTGSerializable extends Serializable{
	Gson gson = new Gson();

	
	public String getStoreId();

	default JsonElement toJson()
	{
		return gson.toJsonTree(this);
	}


	default String getClasseName() {
		return getClass().getName();
	}



}
