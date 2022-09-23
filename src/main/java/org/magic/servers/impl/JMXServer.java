package org.magic.servers.impl;

import static org.magic.tools.MTG.listPlugins;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.PluginRegistry;
import org.magic.tools.Chrono;
public class JMXServer extends AbstractMTGServer {

	private MBeanServer mbs ;
	
	private List<ObjectName> names;
	
	
	
	public JMXServer() {
		super();
		names = new ArrayList<>();
	}
	
	
	
	@Override
	public void start() throws IOException {
		var c = new Chrono();
		c.start();
		mbs = ManagementFactory.getPlatformMBeanServer(); 
			
		PluginRegistry.inst().listClasses().forEach(entry->
		listPlugins(entry).forEach(o->{
				try {
					mbs.registerMBean(new StandardMBean(o, entry),o.getObjectName());
					names.add(o.getObjectName());
				} catch (NotCompliantMBeanException e) {
					logger.trace(e);
				} catch (Exception e) {
					logger.error(e);
				} 
			})
		);
		logger.debug("{} started in {}s.",getName(),c.stop());
	}

	@Override
	public void stop() throws IOException {
		
		logger.debug("{} is stopping",getName());
		var ok=true;
		for(ObjectName n : names)
		{
			try {
				mbs.unregisterMBean(n);
				logger.debug("unloading {}",n);
			} catch (Exception e) {
				ok=false;
				logger.error("error unloading {}",n,e);
			}
		}
		
		if(ok)
			names.clear();
		
	}

	@Override
	public boolean isAlive() {
		return !names.isEmpty();
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("AUTOSTART", "false");
	}

	@Override
	public String description() {
		return "JConsole management beans";
	}

	@Override
	public String getName() {
		return "MXBean Server";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(JMXServer.class.getResource("/icons/plugins/bean.png"));
	}

}
