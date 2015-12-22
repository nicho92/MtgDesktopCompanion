package org.magic.api.beans;

import java.util.ArrayList;
import java.util.List;

public class MagicCollection {

	private String name;
	

	public void setName(String string) {
		this.name=string;
		
	}
	
	

	@Override
	public String toString() {
	return getName();
	}


	public String getName() {
		return name;
	}

}
