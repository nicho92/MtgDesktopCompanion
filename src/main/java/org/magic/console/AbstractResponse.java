package org.magic.console;

public abstract class AbstractResponse<T> {
	

	
	@Override
	public String toString() {
		return show();
	}
	
	public abstract String show();

}
