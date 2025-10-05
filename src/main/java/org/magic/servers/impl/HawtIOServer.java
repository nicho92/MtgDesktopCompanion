package org.magic.servers.impl;

import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.extra.AbstractWarServer;

public class HawtIOServer extends AbstractWarServer{	

		@Override
		protected String warUri() {
			return "https://github.com/hawtio/hawtio/releases/download/hawtio-"+getVersion()+"/hawtio-war-"+getVersion()+".war";
		}


		@Override
		protected String getWarFileName() {
			return "hawtio-war-"+getVersion()+".war";
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
			return "4.5.0";
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
