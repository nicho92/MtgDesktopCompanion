package org.magic.api.interfaces;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;


public interface MTGSerializable extends Serializable{
	
	public String getStoreId();


	default String getClasseName() {
		return getClass().getName();
	}



}
