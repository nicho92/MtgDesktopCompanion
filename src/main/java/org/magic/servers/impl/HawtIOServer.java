package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;

public class HawtIOServer extends AbstractMTGServer{	

		private Server server;
		
		public void init() throws URISyntaxException
		{
			System.setProperty("hawtio.authenticationEnabled", getString("AUTHENTICATION"));
			
			
			server = new Server(getInt("PORT"));
			 var handlers = new HandlerCollection();
		     handlers.setServer(server);
		     server.setHandler(handlers);
		     handlers.addHandler(createHawtioWebapp(server, "http","/"));
		}
		
		
		private WebAppContext createHawtioWebapp(Server server, String scheme,String context) throws  URISyntaxException {
		  var webapp = new WebAppContext();
		        webapp.setServer(server);
		        webapp.setContextPath(context);
		        webapp.setParentLoaderPriority(true);
		        webapp.setLogUrlOnStart(true);
		        webapp.setInitParameter("scheme", scheme);
		        webapp.setTempDirectory(new File(MTGConstants.DATA_DIR,"hawtio"));
		        
		        webapp.setWar(HawtIOServer.class.getResource("/data/hawtio-war-"+getVersion()+".war").toURI().toString());
		        
		        
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
			return "2.17.0";
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
