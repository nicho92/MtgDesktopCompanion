package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;

public class HawtIOServer extends AbstractMTGServer{	

	
		Server server;
		
		public void init()
		{
			String file = HawtIOServer.class.getResource("/data/hawtio-war-"+getVersion()+".war").getPath().substring(1);
			System.setProperty("hawtio.authenticationEnabled", getString("AUTHENTICATION"));
		
			server = new Server(getInt("PORT"));
			 var handlers = new HandlerCollection();
		     handlers.setServer(server);
		     server.setHandler(handlers);
		     var webapp = createHawtioWebapp(server, "http","/",file);
		     handlers.addHandler(webapp);
		}
		
		
		private WebAppContext createHawtioWebapp(Server server, String scheme,String context,String warLocation) {
			 WebAppContext webapp = new WebAppContext();
		        webapp.setServer(server);
		        webapp.setContextPath(context);
		        webapp.setWar(warLocation);
		        webapp.setParentLoaderPriority(true);
		        webapp.setLogUrlOnStart(true);
		        webapp.setInitParameter("scheme", scheme);
		        webapp.setTempDirectory(new File(MTGConstants.DATA_DIR,"hawtio"));
		        
		        logger.info("Init hawtIO on {}://localhost:{}{}. deploying war {} at {} ", scheme,getInt("PORT"),context,webapp.getWar(),webapp.getTempDirectory());
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
			return "2.17.0";
		}

		@Override
		public void start() throws IOException {
			init();
			
			try {
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
