package org.magic.servers.impl;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGGraders;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;
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
		Chrono c = new Chrono();
		c.start();
		mbs = ManagementFactory.getPlatformMBeanServer(); 
			
		PluginRegistry.inst().listClasses().forEach(entry->
			MTGControler.getInstance().getPlugins(entry).forEach(o->{
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
		logger.debug(getName() +" started in " + c.stop() +"s.");
	}

	@Override
	public void stop() throws IOException {
		
		logger.debug(getName() +" is stopping");
		boolean ok=true;
		for(ObjectName n : names)
		{
			try {
				mbs.unregisterMBean(n);
				logger.debug("unloading "+n);
			} catch (Exception e) {
				ok=false;
				logger.error("error unloading" + n,e);
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
	public void initDefault() {
		setProperty("AUTOSTART", "false");
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
