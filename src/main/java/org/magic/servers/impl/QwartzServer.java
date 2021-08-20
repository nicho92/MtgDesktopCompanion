package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdSchedulerFactory;


public class QwartzServer extends AbstractMTGServer {

	private static final String ORG_QUARTZ_PLUGIN_JOB_INITIALIZER_FILE_NAMES = "org.quartz.plugin.jobInitializer.fileNames";
	private  Scheduler scheduler;
	
	public QwartzServer() {
		try {
			
			if(!getFile(ORG_QUARTZ_PLUGIN_JOB_INITIALIZER_FILE_NAMES).exists())
			{
				logger.debug("creating quartz config file");
				FileUtils.copyURLToFile(getClass().getResource("/data/default-quartz.xml"),getFile(ORG_QUARTZ_PLUGIN_JOB_INITIALIZER_FILE_NAMES));
			}
			
			
			scheduler = new StdSchedulerFactory(getProperties()).getScheduler();
		} catch (Exception e) {
			logger.error(e);
		} 
	}
	

	
	@Override
	public void start() throws IOException {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public void stop() throws IOException {
		try {
			scheduler.standby();
			
		} catch (SchedulerException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public boolean isAlive() {
		try {
			
			return scheduler.isStarted() && !scheduler.isInStandbyMode();
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void unload() {
		try {
			if(scheduler!=null)
				scheduler.shutdown();
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	@Override
	public String getVersion() {
		return QuartzScheduler.getVersionMajor() +"."+QuartzScheduler.getVersionMinor() +"."+QuartzScheduler.getVersionIteration();
	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public String description() {
		return "Scheduling scripts";
	}
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = new HashMap<String,String>();
		
		m.put("org.quartz.scheduler.instanceName", "MTGCompanion-schedule");
		m.put("org.quartz.threadPool.threadCount", "3");
		m.put("org.quartz.jobStore.class","org.quartz.simpl.RAMJobStore");
		
		m.put("org.quartz.plugin.jobInitializer.class","org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin");
		m.put(ORG_QUARTZ_PLUGIN_JOB_INITIALIZER_FILE_NAMES,new File(MTGConstants.DATA_DIR,"quartz-config.xml").getAbsolutePath());
		m.put("org.quartz.plugin.jobInitializer.failOnFileNotFound","true");
	
		m.put("AUTOSTART", "false");
		
		return m;
	}

	
	@Override
	public MTGDocumentation getDocumentation() {
		try {
			return new MTGDocumentation(new URL("http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/ConfigMain.html"),FORMAT_NOTIFICATION.HTML);
		} catch (MalformedURLException e) {
			return super.getDocumentation();
		}
	}
	
	@Override
	public String getName() {
		return "Scheduler";
	}

}





