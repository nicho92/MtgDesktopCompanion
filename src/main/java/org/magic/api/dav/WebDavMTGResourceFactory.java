package org.magic.api.dav;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.magic.api.fs.MTGFileSystem;
import org.magic.api.fs.MTGPath;
import org.magic.api.interfaces.MTGDao;
import org.magic.servers.impl.WebDAVServer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.PluginRegistry;

import io.milton.common.Path;
import io.milton.http.ResourceFactory;
import io.milton.resource.Resource;

public class WebDavMTGResourceFactory implements ResourceFactory
{
	private MTGFileSystem fs;
	protected Logger log = MTGLogger.getLogger(this.getClass());
	protected WebDAVServer serv;
	
	
    
	public WebDavMTGResourceFactory() throws SQLException {
		fs = new MTGFileSystem(MTGControler.getInstance().getEnabled(MTGDao.class));
		
		serv=(WebDAVServer)PluginRegistry.inst().getPlugin("WebDAV");
	}
	
	
	
    @Override
    public Resource getResource(String host, String url) {
        log.trace("getResource: host: " + host + " - url:" + url);
        
        Path ioPath = Path.path(url);
        MTGPath mtgpath = (MTGPath) fs.getPath(ioPath.toPath());
        
        if(mtgpath.isCard())
        	return new MTGDavFileResource(mtgpath,fs,serv.getLogin(),serv.getPassword());
        else
        	return new MTGDavFolderResource(mtgpath,fs,ioPath.isRoot(),serv.getLogin(),serv.getPassword());

     }
    
}