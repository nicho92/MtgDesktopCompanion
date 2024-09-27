package org.magic.servers.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jolokia.config.ConfigKey;
import org.jolokia.jvmagent.JolokiaServer;
import org.jolokia.jvmagent.JolokiaServerConfig;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;

public class JolokiaJMXServer extends AbstractMTGServer {

	JolokiaServer serv;
	private boolean started=false;
	
	
	private void init() throws IOException
	{
		var map = new HashMap<String,String>();
		
		map.put("host", getString("HOST"));
		map.put("port", getString("PORT"));
		map.put(ConfigKey.AGENT_CONTEXT.getKeyValue(), getString("CONTEXT"));
		map.put(ConfigKey.USER.getKeyValue(),getString("USER"));
		map.put(ConfigKey.PASSWORD.getKeyValue(),getString("PASS"));
		map.put(ConfigKey.DISCOVERY_ENABLED.getKeyValue(), "true");
		
		
		
		var jconf = new JolokiaServerConfig(map);
	
	serv = new JolokiaServer(jconf, true);
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = new HashMap<String,MTGProperty>();
		m.put("HOST", new MTGProperty("127.0.0.1","server name or ip"));
		m.put("PORT", MTGProperty.newIntegerProperty("8778", "listening port for webserver", 80, -1));
		m.put("CONTEXT", new MTGProperty("/","context page for the jolokia index"));
		m.put("USER", new MTGProperty("jolokia","user allowed to connect to jolokia"));
		m.put("PASS", new MTGProperty("jolokia","password for the user allowed to connect to jolokia"));
		m.put("AUTOSTART", MTGProperty.newBooleanProperty(FALSE, "Run server at startup"));
		return m;
	}
	
	
	@Override
	public void start() throws IOException {
		init();
		serv.start();
		started=true;
		logger.info("{} started at {}", getName(), serv.getUrl());
	}

	@Override
	public void stop() throws IOException {
	try {
		serv.stop();
		
	}
	catch(Exception e)
	{
		logger.error(e);
	}
	finally {
		started=false;
	}
		
		
	}

	@Override
	public boolean isAlive() {
		return started;
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public String description() {
		return "Jolokia is a JMX-HTTP bridge giving an alternative to JSR-160 connectors. It is an agent based approach with support for many platforms.";
	}

	@Override
	public String getName() {
		return "Jolokia";
	}

}
