package org.magic.api.interfaces;

import java.io.Serializable;

public interface MTGStorable extends Serializable{

	public String getStoreId();

	//public JsonObject toLightJson();


	default String getClasseName() {
		return getClass().getName();
	}



}
