package org.magic.api.beans;

import org.magic.api.interfaces.MTGPlugin;

public class MTGImportExportException extends Exception{
	private static final long serialVersionUID = 1L;
	
	
	private String msg;
	private MTGPlugin plugin; 
	
	
	public MTGImportExportException(MTGPlugin plugin,String msg)
	{
		this.msg=msg;
		this.plugin=plugin;
	}
	
	public void setMessage(String msg)
	{
		this.msg=msg;
		
	}
	
	
	@Override
	public String getMessage() {
		return msg;
	}
	
	public MTGPlugin getPlugin() {
		return plugin;
	}
	

}
