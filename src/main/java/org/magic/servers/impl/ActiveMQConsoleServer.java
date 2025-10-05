package org.magic.servers.impl;

import java.awt.Image;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.extra.AbstractWarServer;
import org.magic.services.MTGConstants;

public class ActiveMQConsoleServer extends AbstractWarServer{	

	
		@Override
		protected String getWarFileName() {
			return "artemis-console-"+getVersion()+".war";
		}
		
		@Override
		protected String warUri() {
			return "https://repo1.maven.org/maven2/org/apache/activemq/artemis-console/"+getVersion()+"/artemis-console-"+getVersion()+".war";
		}
	
		@Override
		public void preinit() {
			System.setProperty("hawtio.authenticationEnabled", getString("AUTHENTICATION"));
			
		}
		
		
		@Override
		public Map<String, MTGProperty> getDefaultAttributes() {
				var m = super.getDefaultAttributes();
					m.put("AUTHENTICATION",MTGProperty.newBooleanProperty("false","enable or not hawt authentication"));
					
					return m;
		}
		
		
		@Override
		public String getVersion() {
			return new ActiveMQServerImpl(new ConfigurationImpl()).getVersion().getFullVersion();
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
