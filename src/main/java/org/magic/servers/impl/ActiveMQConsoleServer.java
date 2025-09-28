package org.magic.servers.impl;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public class ActiveMQConsoleServer extends AbstractMTGServer{	

		private Server server;
		
		private File downloadVersion() throws IOException
		{
				var url = "https://repo1.maven.org/maven2/org/apache/activemq/artemis-console/"+getVersion()+"/artemis-console-2.42.0.war";
				var f = new File(MTGConstants.DATA_DIR,"artemis-console-"+getVersion()+".war");
				
				if(!f.exists())
					URLTools.download(url, f);
				
				return f;
		}
		
		
		public void init() 
		{
			System.setProperty("hawtio.authenticationEnabled", getString("AUTHENTICATION"));
			
			
			server = new Server(getInt("PORT"));
			 var handlers = new Handler.Sequence();
		     handlers.setServer(server);
		     server.setHandler(handlers);
		     try {
				handlers.addHandler(createWebapp(server, "http","/"));
			} catch (IOException e) {
				logger.error("error on hawtio Init", e);
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
		        
		        
		        logger.info("Init ArtemisConsole on {}://localhost:{}{}. deploying war {}", scheme,getInt("PORT"),context,webapp.getWar());
		        return webapp;
		}


		@Override
		public Map<String, MTGProperty> getDefaultAttributes() {
				return Map.of("AUTOSTART", MTGProperty.newBooleanProperty(FALSE, "Run server at startup"),
									"PORT", MTGProperty.newIntegerProperty("8082", "listening port for webserver", 80, -1),
									 "AUTHENTICATION",MTGProperty.newBooleanProperty("false","enable or not hawt authentication"));
		}
		
		@Override
		public String getVersion() {
			return new ActiveMQServerImpl(new ConfigurationImpl()).getVersion().getFullVersion();
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

		@Override
		public String description() {
			return "A modular web console for managing your Artemis Server";
		}

		@Override
		public String getName() {
			return "Artemis Console";
		}
		
		@Override
		public Icon getIcon() {
			try {
				return new ImageIcon(new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/activemq.png")).getImage().getScaledInstance(MTGConstants.MENU_ICON_SIZE, MTGConstants.MENU_ICON_SIZE, Image.SCALE_SMOOTH));
			}
			catch(Exception _)
			{
				return MTGConstants.ICON_DEFAULT_PLUGIN;
			}
		}
		
	
}
