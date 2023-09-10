package org.magic.api.interfaces;

import java.io.Serializable;


public interface MTGSerializable extends Serializable{
	
	public String getStoreId();


	default String getClasseName() {
		return getClass().getName();
	}



}
