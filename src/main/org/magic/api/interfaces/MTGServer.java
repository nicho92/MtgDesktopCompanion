package org.magic.api.interfaces;

public interface MTGServer extends MTGPlugin{

	
	public void start() throws Exception ;
	public void stop() throws Exception;
	public boolean isAlive();
	public boolean isAutostart();
	public String description();
	
}
