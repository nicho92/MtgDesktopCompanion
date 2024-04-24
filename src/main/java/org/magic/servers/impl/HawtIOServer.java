package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public class HawtIOServer extends AbstractMTGServer{	

		private Server server;
		
		private File downloadVersion() throws IOException
		{
				var url = "https://github.com/hawtio/hawtio/releases/download/hawtio-"+getVersion()+"/hawtio-war-"+getVersion()+".war";
				var f = new File(MTGConstants.DATA_DIR,"hawtio-war-"+getVersion()+".war");
				
				if(!f.exists())
					URLTools.download(url, f);
				
				return f;
		}
		
		
		public void init() 
		{
			System.setProperty("hawtio.authenticationEnabled", getString("AUTHENTICATION"));
			
			
			server = new Server(getInt("PORT"));
			 var handlers = new HandlerCollection();
		     handlers.setServer(server);
		     server.setHandler(handlers);
		     try {
				handlers.addHandler(createHawtioWebapp(server, "http","/"));
			} catch (IOException e) {
				logger.error("error on hawtio Init", e);
			}
		}
		
		
		private WebAppContext createHawtioWebapp(Server server, String scheme,String context) throws   IOException {
		  var webapp = new WebAppContext();
		        webapp.setServer(server);
		        webapp.setContextPath(context);
		        webapp.setParentLoaderPriority(true);
		        webapp.setLogUrlOnStart(true);
		        webapp.setInitParameter("scheme", scheme);
		        webapp.setTempDirectory(new File(MTGConstants.DATA_DIR,"hawtio"));
		        
		        webapp.setWar(downloadVersion().toURI().toString());
		        
		        
		        logger.info("Init hawtIO on {}://localhost:{}{}. deploying war {}", scheme,getInt("PORT"),context,webapp.getWar());
		        return webapp;
		}


		@Override
		public Map<String, String> getDefaultAttributes() {
				return Map.of("AUTOSTART","false",
									 "PORT","8082",
									 "AUTHENTICATION","false");
		}
		
		@Override
		public String getVersion() {
			return "4.0.0";
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
			} catch (Exception e) {
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

		@Override
		public String description() {
			return "A modular web console for managing your Java stuff";
		}

		@Override
		public String getName() {
			return "Hawtio";
		}
	
}
