package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


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
		m.put("org.quartz.plugin.jobInitializer.scanInterval","60");
		m.put("AUTOSTART", "false");
		
		return m;
	}
	
	
	public void runJob(Job job, String name) throws SchedulerException
	{
		var jobKey = JobKey.jobKey(name, "instantJob");
	    var jobd =JobBuilder.newJob(job.getClass()).withIdentity(jobKey).storeDurably().build();
	    scheduler.addJob(jobd, true);
	    scheduler.triggerJob(jobKey);
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
		return "Qwartz";
	}
	
	public JsonObject toJsonDetails() throws SchedulerException {
		  var schedulerMetaData = scheduler.getMetaData();
		  var quartzInformation = new JsonObject();
		  quartzInformation.addProperty("version",schedulerMetaData.getVersion());
		  quartzInformation.addProperty("schedulerName",schedulerMetaData.getSchedulerName());
		  quartzInformation.addProperty("schedulerInstanceId",schedulerMetaData.getSchedulerInstanceId());
		  quartzInformation.addProperty("threadPoolClass",schedulerMetaData.getThreadPoolClass().getCanonicalName());
		  quartzInformation.addProperty("threadPoolSize",schedulerMetaData.getThreadPoolSize());
		  quartzInformation.addProperty("schedulerClass",schedulerMetaData.getSchedulerClass().getCanonicalName());
		  quartzInformation.addProperty("isCluster",schedulerMetaData.isJobStoreClustered());
		  quartzInformation.addProperty("jobStoreClass",schedulerMetaData.getJobStoreClass().getCanonicalName());
		  quartzInformation.addProperty("numberOfJobsExecuted",schedulerMetaData.getNumberOfJobsExecuted());
		  quartzInformation.addProperty("isStandByMode",schedulerMetaData.isInStandbyMode());
		  quartzInformation.addProperty("runningSince",UITools.formatDateTime(schedulerMetaData.getRunningSince()));
		  var simpleJobList = new JsonArray();
		  
		  for (String groupName : scheduler.getJobGroupNames()) {
		    for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
		      for(Trigger trigger : scheduler.getTriggersOfJob(jobKey)) {
		    	  var jobObj = new JsonObject();
		    	  					 jobObj.addProperty("jobGroup", jobKey.getGroup());
		    	  					 jobObj.addProperty("jobName", jobKey.getName());
		    	  					 jobObj.addProperty("nextFireTime", trigger.getNextFireTime().getTime());
		    	  					 jobObj.addProperty("lastFireTime", trigger.getPreviousFireTime().getTime());
		    	  					simpleJobList.add(jobObj);
		      }
		    }
		  }
		  quartzInformation.add("jobs", simpleJobList);
		  return quartzInformation;
		}



	public List<JsonObject> getJobs() {
		var ret = new ArrayList<JsonObject>();
		
		try {
			toJsonDetails().get("jobs").getAsJsonArray().forEach(je->ret.add(je.getAsJsonObject()));
		} catch (Exception e) {
			logger.error(e);
		}
		return ret;
	}
	
	
}