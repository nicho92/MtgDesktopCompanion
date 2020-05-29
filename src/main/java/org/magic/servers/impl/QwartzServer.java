package org.magic.servers.impl;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.tools.ScripterJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdSchedulerFactory;


public class QwartzServer extends AbstractMTGServer {

	private  Scheduler scheduler;
	private boolean initied=false;
	
	public static void main(String[] args) throws IOException {
		
		MTGControler.getInstance();
		QwartzServer s = new QwartzServer();
		s.start();
	}
	
	public QwartzServer() {
		try {
			scheduler = new StdSchedulerFactory(getProperties()).getScheduler();
		} catch (SchedulerException e) {
			logger.error(e);
		}
	}
	

	private void init() throws SchedulerException
	{

		List<JobDetail> jobs = new ArrayList<>();
		for(String name :  getArray("SCRIPTS_FILES"))
		{
			JobDetail job = newJob(ScripterJob.class).withIdentity(name+"-job", "group1").build();
			
			Optional<MTGScript> optScripter = PluginRegistry.inst().listEnabledPlugins(MTGScript.class).stream().filter(s->FilenameUtils.getExtension(name).toLowerCase().endsWith(s.getExtension().toLowerCase())).findAny();
			if(optScripter.isPresent())
			{
				MTGScript p = optScripter.get();
				File f = new File(p.getScriptDirectory(),name);
				job.getJobDataMap().put(ScripterJob.SCRIPT_FILE, f);
				job.getJobDataMap().put(ScripterJob.SCRIPTER, p);
				jobs.add(job);
				logger.debug("registering :"+job);
			}
			else
			{
				logger.error("no Scripter found for " + name);
			}
		} 
		
		TriggerBuilder<CronTrigger> triggerBuilder = newTrigger()
				    .withIdentity("trigger1", "group1")
				    .startAt(new Date())
				    .withSchedule(CronScheduleBuilder.cronSchedule(getString("CRON_LINE")));

		jobs.forEach(triggerBuilder::forJob);
		
		
		
		Trigger trigger = triggerBuilder.build();
		
		
		for(JobDetail job : jobs)
		 scheduler.scheduleJob(job, trigger);
		
		
		initied=true;
	}
	
	
	@Override
	public void start() throws IOException {
		try {
			
			if(!initied)
				init();
			
			
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
	public void initDefault() {
		setProperty("org.quartz.scheduler.instanceName", "MTGCompanion-schedule");
		setProperty("org.quartz.threadPool.threadCount", "3");
		setProperty("org.quartz.jobStore.class","org.quartz.simpl.RAMJobStore");
		setProperty("SCRIPTS_FILES","HelloWorld.groovy");
		setProperty("CRON_LINE","0 */2 * ? * *");
		setProperty("AUTOSTART", "false");
	}

	
	@Override
	public MTGDocumentation getDocumentation() {
		try {
			return new MTGDocumentation(new URL("https://www.freeformatter.com/cron-expression-generator-quartz.html#cronexpressionexamples"),FORMAT_NOTIFICATION.HTML);
		} catch (MalformedURLException e) {
			return super.getDocumentation();
		}
	}
	
	@Override
	public String getName() {
		return "Scheduler";
	}

}





