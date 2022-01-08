package org.magic.api.interfaces;

import java.io.Serializable;

import com.google.gson.JsonObject;

public interface MTGStorable extends Serializable{
	
	public String getStoreId();
	
	//public JsonObject toLightJson();
	
	
	default String getClasseName() {
		return getClass().getName();
	}
	
	
	
}
