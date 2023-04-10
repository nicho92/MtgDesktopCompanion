package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.core.config.CoreAddressConfiguration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.security.CheckType;
import org.apache.activemq.artemis.core.security.Role;
import org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;
import org.apache.commons.lang3.ArrayUtils;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.game.model.Player;
import org.magic.services.MTGConstants;
import org.magic.services.TechnicalServiceManager;
import org.magic.services.network.URLTools;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ActiveMQServer extends AbstractMTGServer {

	private static final String LISTENERS_TCP = "LISTENERS_TCP";
	private static final String LOG_DIR = "LOG_DIR";
	public static final String DEFAULT_ADDRESS = "welcome";
	private ActiveMQServerImpl server;
	private MTGNetworkClient client; 
	
	
	public ActiveMQServer() {
		super();
		server = new ActiveMQServerImpl(new ConfigurationImpl());
		client = new ActiveMQNetworkClient();
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = new HashMap<String,String>();
			 m.put("ENABLE_JMX_MNG", "true");
			 m.put(LISTENERS_TCP, "tcp://"+URLTools.getInternalIP()+":61616");
			 m.put("SECURITY_ENABLED", "false");
			 m.put(LOG_DIR, new File(MTGConstants.DATA_DIR,"activemq").getAbsolutePath());
			 m.put("ADRESSES", "trade,news");
			 m.put("RETENTION_DAYS", "7");
			 m.put("AUTOSTART", "false");
			 return m;
	}
	
	public JsonObject detailsToJson() 
	{
		var obj = new JsonObject();
		
		try {
			obj.add("acceptors", URLTools.toJson(server.getActiveMQServerControl().getAcceptorsAsJSON()).getAsJsonArray());
			obj.add("connections", URLTools.toJson(server.getActiveMQServerControl().listConnectionsAsJSON()).getAsJsonArray());
			obj.add("sessions",URLTools.toJson(server.getActiveMQServerControl().listAllSessionsAsJSON()).getAsJsonArray()); 
			obj.add("consumers",URLTools.toJson(server.getActiveMQServerControl().listAllConsumersAsJSON()).getAsJsonArray());
			obj.add("producers",URLTools.toJson(server.getActiveMQServerControl().listProducersInfoAsJSON()).getAsJsonArray());
			var arr = new JsonArray();
			
			
			for(String s : server.getActiveMQServerControl().getQueueNames())
			{
					arr.add(s);
			}
			
			obj.add("queues",arr);
			
		} catch (Exception e) {
			logger.error(e);
		}
		
		
		return obj;
	}
	
	
	
	private void init() throws IOException 
	{
			try {
				
				for(int i=0;i<getArray(LISTENERS_TCP).length;i++)
					server.getConfiguration().addAcceptorConfiguration("tcp-"+i, getArray(LISTENERS_TCP)[i]);
				
				
				server.getConfiguration().setSecurityEnabled(getBoolean("SECURITY_ENABLED"));
				server.getConfiguration().setJMXManagementEnabled(getBoolean("ENABLE_JMX_MNG"));
				server.getConfiguration().setJournalRetentionPeriod(TimeUnit.DAYS, getInt("RETENTION_DAYS"));
				server.getConfiguration().setJournalDirectory(getString(LOG_DIR));
				server.getConfiguration().setPagingDirectory(getString(LOG_DIR));
				server.getConfiguration().setNodeManagerLockDirectory(getString(LOG_DIR));
				server.getConfiguration().setLargeMessagesDirectory(getString(LOG_DIR));
				server.getConfiguration().setBindingsDirectory(getString(LOG_DIR));
				
				
				
				
				for(String add : ArrayUtils.add(getArray("ADRESSES"),DEFAULT_ADDRESS))
				{
					var addr = new CoreAddressConfiguration();
					addr.setName(add);
					addr.addRoutingType(RoutingType.MULTICAST);
					server.getConfiguration().addAddressConfiguration(addr);
				}
				
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
			
			client.join(new Player("System"),getArray(LISTENERS_TCP)[0],DEFAULT_ADDRESS);
			
			ThreadManager.getInstance().executeThread(new MTGRunnable() {
				
				@Override
				protected void auditedRun() {
					while(isAlive())
					{
						try {
							var msg = client.consume();
							TechnicalServiceManager.inst().store(msg);
						} catch (IOException e) {
							logger.error(e);
						}
					}
				}
			}, "ActiveMQ Client Audit");
			
			
			
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
