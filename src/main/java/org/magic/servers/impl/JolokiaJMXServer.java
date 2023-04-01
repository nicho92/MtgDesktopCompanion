package org.magic.servers.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jolokia.config.ConfigKey;
import org.jolokia.jvmagent.JolokiaServer;
import org.jolokia.jvmagent.JolokiaServerConfig;
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
	public Map<String, String> getDefaultAttributes() {
		var m = new HashMap<String,String>();
		m.put("HOST", "127.0.0.1");
		m.put("PORT", "8778");
		m.put("CONTEXT", "/");
		m.put("USER", "jolokia");
		m.put("PASS", "jolokia");
		m.put("AUTOSTART", "false");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Jolokia";
	}

}
