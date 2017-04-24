package org.magic.api.beans;

public class MagicCollection {

	private String name;
	
	public MagicCollection() {
		
	}
	
	public MagicCollection(String name)
	{
		this.name=name;
	}

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
