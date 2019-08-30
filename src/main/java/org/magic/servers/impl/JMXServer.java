package org.magic.servers.impl;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
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
		
			MTGControler.getInstance().getPlugins(MTGDao.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGDao.class),o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGCardsProvider.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGCardsProvider.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGPictureProvider.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGPictureProvider.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
		

			MTGControler.getInstance().getPlugins(MTGCardsExport.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGCardsExport.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGDeckSniffer.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGDeckSniffer.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGNotifier.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGNotifier.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			

			MTGControler.getInstance().getPlugins(MTGNewsProvider.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGNewsProvider.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});

			MTGControler.getInstance().getPlugins(MTGServer.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGServer.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGCardsIndexer.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGCardsIndexer.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGTextGenerator.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGTextGenerator.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGDashBoard.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGDashBoard.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGPicturesCache.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGPicturesCache.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			

			MTGControler.getInstance().getPlugins(MTGPricesProvider.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGPricesProvider.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGShopper.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGShopper.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGScript.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGScript.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
			MTGControler.getInstance().getPlugins(MTGPool.class).forEach(o->{
				try {
					names.add(o.getObjectName());
					mbs.registerMBean(new StandardMBean(o, MTGPool.class), o.getObjectName());
				} catch (Exception e) {
					logger.error(e);
				} 
			});
			
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
