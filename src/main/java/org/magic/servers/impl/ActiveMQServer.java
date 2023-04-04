package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.security.CheckType;
import org.apache.activemq.artemis.core.security.Role;
import org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;

public class ActiveMQServer extends AbstractMTGServer {

	private static final String LOG_DIR = "LOG_DIR";
	private ActiveMQServerImpl server;
	
	public ActiveMQServer() {
		super();
		var config = new ConfigurationImpl();
		server = new ActiveMQServerImpl(config);

	
	}
	
	public static void main(String[] args) throws IOException {
		new ActiveMQServer().start();
	}
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = new HashMap<String,String>();
			 m.put("ENABLE_JMX_MNG", "true");
			 m.put("LISTENERS_TCP", "tcp://localhost:61616");
			 m.put("SECURITY_ENABLED", "false");
			 m.put(LOG_DIR, new File(MTGConstants.DATA_DIR,"activemq").getAbsolutePath());
			 m.put("QUEUES", "welcome,trade");
			 m.put("RETENTION_DAYS", "7");
			 m.put("AUTOSTART", "false");
			 return m;
	}
	
	
	private void init() throws IOException 
	{
			try {
				
				for(int i=0;i<getArray("LISTENERS_TCP").length;i++)
					server.getConfiguration().addAcceptorConfiguration("tcp-"+i, getArray("LISTENERS_TCP")[i]);
				
				
				server.getConfiguration().setSecurityEnabled(getBoolean("SECURITY_ENABLED"));
				server.getConfiguration().setJMXManagementEnabled(getBoolean("ENABLE_JMX_MNG"));
				server.getConfiguration().setJournalRetentionPeriod(TimeUnit.DAYS, getInt("RETENTION_DAYS"));
				server.getConfiguration().setJournalDirectory(getString(LOG_DIR));
				server.getConfiguration().setPagingDirectory(getString(LOG_DIR));
				server.getConfiguration().setLargeMessagesDirectory(getString(LOG_DIR));
				server.getConfiguration().setBindingsDirectory(getString(LOG_DIR));
				server.setSecurityManager(new ActiveMQSecurityManager() {
					
					@Override
					public boolean validateUserAndRole(String user, String password, Set<Role> roles, CheckType checkType) {
						return true;
					}
					
					@Override
					public boolean validateUser(String user, String password) {
						return true;
					}
				});
				
			for(String s : getArray("QUEUES")) {		
				var cqc = new QueueConfiguration();
						cqc.setAddress(s);
						cqc.setName(s);
						cqc.setDurable(true);
						cqc.setAutoCreated(true);
						cqc.setConfigurationManaged(true);
						cqc.setRoutingType(RoutingType.MULTICAST);
						server.getConfiguration().addQueueConfiguration(cqc);
			}
		
			} catch (Exception e) {
				throw new IOException(e);
			}
			
	
	}
	
	
	@Override
	public void start() throws IOException {
		try {
			init();
			server.start();
			logger.info("{} is started", getName());
		} catch (Exception e) {
			throw new IOException(e);
		}

	}

	@Override
	public void stop() throws IOException {
		try {
			server.stop();
		} catch (Exception e) {
			throw new IOException(e);
		}

	}

	@Override
	public boolean isAlive() {
		return server.isStarted();
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public String description() {
		return "Messaging server";
	}

	@Override
	public String getVersion() {
		return server.getVersion().getFullVersion();
	}
	
	@Override
	public String getName() {
		return "ActiveMQ";
	}
	
}
