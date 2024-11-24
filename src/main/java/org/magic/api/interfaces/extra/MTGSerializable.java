package org.magic.api.interfaces.extra;

import java.io.Serializable;


public interface MTGSerializable extends Serializable{
	
	public String getStoreId();


	default String getClasseName() {
		return getClass().getName();
	}



}
