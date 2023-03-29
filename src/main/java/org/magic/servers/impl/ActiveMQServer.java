package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;

public class ActiveMQServer extends AbstractMTGServer {

	private ActiveMQServerImpl server;
	
	public ActiveMQServer() {
		super();
		var config = new ConfigurationImpl();
		server = new ActiveMQServerImpl(config);
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
			 m.put("ENABLE_JMX_MNG", "true");
			 m.put("LISTENERS_TCP", "tcp://localhost:8081");
			 m.put("SECURITY_ENABLED", "false");
			 m.put("LOG_DIR", new File(MTGConstants.DATA_DIR,"activemq").getAbsolutePath());
			 m.put("QUEUES", "welcome,trade");
			 m.put("RETENTION_DAYS", "7");
			 return m;
	};
	
	
	private void init() throws Exception
	{
		
			server.getConfiguration().addAcceptorConfiguration("tcp", getString("LISTENERS_TCP"));
			server.getConfiguration().setSecurityEnabled(getBoolean("SECURITY_ENABLED"));
			server.getConfiguration().setJMXManagementEnabled(getBoolean("ENABLE_JMX_MNG"));
			server.getConfiguration().setJournalDirectory(getString("LOG_DIR"));
			server.getConfiguration().setPagingDirectory(getString("LOG_DIR"));
			server.getConfiguration().setLargeMessagesDirectory(getString("LOG_DIR"));
			server.getConfiguration().setBindingsDirectory(getString("LOG_DIR"));
			server.getConfiguration().setJournalRetentionPeriod(TimeUnit.DAYS, getInt("RETENTION_DAYS"));
			
			
			String s = "welcome";
			//for(String s : getArray("QUEUES"))
			{
				QueueConfiguration config = new QueueConfiguration();
				config.setAddress(s);
				config.setName(s);
				config.setDurable(true);
				server.createQueue(config);	
			}
			
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
			throw new IOException(e);
		}

	}

	@Override
	public boolean isAlive() {
		return server.isActive();
	}

	@Override
	public boolean isAutostart() {
		// TODO Auto-generated method stub
		return false;
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
	
	public static void main(String[] args) throws IOException {
		new ActiveMQServer().start();
	}
	

}
