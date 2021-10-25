package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;


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
	
	public QuartzInformation getSchedulerInformation() throws SchedulerException {
		  SchedulerMetaData schedulerMetaData = scheduler.getMetaData();

		  QuartzInformation quartzInformation = new QuartzInformation();
		  quartzInformation.setVersion(schedulerMetaData.getVersion());
		  quartzInformation.setSchedulerName(schedulerMetaData.getSchedulerName());
		  quartzInformation.setInstanceId(schedulerMetaData.getSchedulerInstanceId());

		  quartzInformation.setThreadPoolClass(schedulerMetaData.getThreadPoolClass().getCanonicalName());
		  quartzInformation.setNumberOfThreads(schedulerMetaData.getThreadPoolSize());

		  quartzInformation.setSchedulerClass(schedulerMetaData.getSchedulerClass().getCanonicalName());
		  quartzInformation.setClustered(schedulerMetaData.isJobStoreClustered());

		  quartzInformation.setJobStoreClass(schedulerMetaData.getJobStoreClass().getCanonicalName());
		  quartzInformation.setNumberOfJobsExecuted(schedulerMetaData.getNumberOfJobsExecuted());

		  quartzInformation.setInStandbyMode(schedulerMetaData.isInStandbyMode());
		  quartzInformation.setStartTime(schedulerMetaData.getRunningSince());
		    List<String> simpleJobList = new ArrayList<>();
		  for (String groupName : scheduler.getJobGroupNames()) {
		

		    for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
		      String jobName = jobKey.getName();
		      String jobGroup = jobKey.getGroup();

		      for(Trigger trigger : scheduler.getTriggersOfJob(jobKey)) {
			      Date nextFireTime = trigger.getNextFireTime();
			      Date lastFireTime = trigger.getPreviousFireTime();
			      simpleJobList.add(String.format("%1s.%2s - next run: %3s (previous run: %4s)", jobGroup, jobName, nextFireTime, lastFireTime));
		      }
		    }

		  
		  }
		  quartzInformation.setSimpleJobDetail(simpleJobList);
		  return quartzInformation;
		}
	
	
}

class QuartzInformation implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getThreadPoolClass() {
		return threadPoolClass;
	}

	public void setThreadPoolClass(String threadPoolClass) {
		this.threadPoolClass = threadPoolClass;
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	public String getSchedulerClass() {
		return schedulerClass;
	}

	public void setSchedulerClass(String schedulerClass) {
		this.schedulerClass = schedulerClass;
	}

	public boolean isClustered() {
		return isClustered;
	}

	public void setClustered(boolean isClustered) {
		this.isClustered = isClustered;
	}

	public String getJobStoreClass() {
		return jobStoreClass;
	}

	public void setJobStoreClass(String jobStoreClass) {
		this.jobStoreClass = jobStoreClass;
	}

	public long getNumberOfJobsExecuted() {
		return numberOfJobsExecuted;
	}

	public void setNumberOfJobsExecuted(long numberOfJobsExecuted) {
		this.numberOfJobsExecuted = numberOfJobsExecuted;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public boolean isInStandbyMode() {
		return inStandbyMode;
	}

	public void setInStandbyMode(boolean inStandbyMode) {
		this.inStandbyMode = inStandbyMode;
	}

	public List<String> getSimpleJobDetail() {
		return simpleJobDetail;
	}

	public void setSimpleJobDetail(List<String> simpleJobDetail) {
		this.simpleJobDetail = simpleJobDetail;
	}

	private String version;
    private String schedulerName;
    private String instanceId;

    private String threadPoolClass;
    private int numberOfThreads;

    private String schedulerClass;
    private boolean isClustered;

    private String jobStoreClass;
    private long numberOfJobsExecuted;

    private Date startTime;
    private boolean inStandbyMode;

    private List<String> simpleJobDetail;

    public String getSchedulerProductName() {
        return "Quartz Scheduler (spring-boot-starter-quartz)";
    }
}



