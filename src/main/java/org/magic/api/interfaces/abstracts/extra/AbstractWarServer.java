package org.magic.api.interfaces.abstracts.extra;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public abstract class AbstractWarServer extends AbstractMTGServer{	

		private Server server;
		
		protected abstract String warUri() ;
		protected abstract String getWarFileName();
		
		private File downloadVersion() throws IOException
		{
				var f = new File(MTGConstants.DATA_DIR,getWarFileName());
				
				if(!f.exists())
					URLTools.download(warUri(), f);
				
				return f;
		}
		
		
		public void init() 
		{
			preinit();
			server = new Server(getInt("PORT"));
			 var handlers = new Handler.Sequence();
		     handlers.setServer(server);
		     server.setHandler(handlers);
		     try {
				handlers.addHandler(createWebapp(server, "http","/"));
			} catch (IOException e) {
				logger.error("error on Init", e);
			}
		}
		
		
		private WebAppContext createWebapp(Server server, String scheme,String context) throws   IOException {
		  var webapp = new WebAppContext();
		        webapp.setServer(server);
		        webapp.setContextPath(context);
		        webapp.setParentLoaderPriority(true);
		        webapp.setLogUrlOnStart(true);
		        webapp.setInitParameter("scheme", scheme);
		        webapp.setTempDirectory(new File(MTGConstants.DATA_DIR,"artemis"));
		        
		        webapp.setWar(downloadVersion().toURI().toString());
		        
		        
		        logger.info("Init {} on {}://localhost:{}{}. deploying war {}",getName(), scheme,getInt("PORT"),context,webapp.getWar());
		        return webapp;
		}


		@Override
		public Map<String, MTGProperty> getDefaultAttributes() {
				var m = new HashMap<String, MTGProperty>();
				m.put("AUTOSTART", MTGProperty.newBooleanProperty(FALSE, "Run server at startup"));
				m.put("PORT", MTGProperty.newIntegerProperty("8083", "listening port for webserver", 80, -1));
				return m;
		}
		
		@Override
		public void start() throws IOException {
			try {
				init();
				server.start();
			} catch (Exception e) {
				throw new IOException(e);
			}
			
		}

		@Override
		public void stop() throws IOException {
			try {
				server.stop();
			} catch (Exception _) {
				throw new IOException();
			}
		
		}

		@Override
		public boolean isAlive() {
			if(server!=null)
				return server.isRunning();
			
			return false;
		}


		@Override
		public boolean isAutostart() {
			return getBoolean("AUTOSTART");
		}
	
}
