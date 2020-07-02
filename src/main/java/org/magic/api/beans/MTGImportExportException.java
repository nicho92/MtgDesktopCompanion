package org.magic.api.beans;

import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGPlugin;

public class MTGImportExportException extends Exception{
	private static final long serialVersionUID = 1L;
	
	
	private String msg;
	private MODS mod;
	private MTGPlugin plugin; 
	
	
	public MTGImportExportException(MTGPlugin plugin,String msg, MTGCardsExport.MODS mod)
	{
		this.msg=msg;
		this.mod=mod;
		this.plugin=plugin;
	}
	
	
	@Override
	public String getMessage() {
		return msg;
	}
	
	public MODS getMod() {
		return mod;
	}
	
	public MTGPlugin getPlugin() {
		return plugin;
	}
	

}
