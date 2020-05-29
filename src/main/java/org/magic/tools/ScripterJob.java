package org.magic.tools;

import java.io.File;

import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGScript;
import org.magic.services.MTGLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScripterJob implements Job {

	public static final String SCRIPTER = "scripter";
	public static final String SCRIPT_FILE = "scriptFile";
	
	
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MTGScript s = (MTGScript) context.getJobDetail().getJobDataMap().get(SCRIPTER);
		File f = (File) context.getJobDetail().getJobDataMap().get(SCRIPT_FILE);
		logger.debug("Running job " + context);
		
		try {
			s.run(f);
		} catch (ScriptException e) {
			logger.error(e);
		}

	}

}
