package org.magic.services.jobs;

import java.io.File;

import javax.script.ScriptException;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.MTGScript;
import org.magic.services.PluginRegistry;
import org.magic.services.logging.MTGLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScripterJob implements Job {


	protected Logger logger = MTGLogger.getLogger(this.getClass());


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		var name=context.getJobDetail().getJobDataMap().get("SCRIPT_NAME").toString();

		var optScripter = PluginRegistry.inst().listEnabledPlugins(MTGScript.class).stream().filter(s->FilenameUtils.getExtension(name).toLowerCase().endsWith(s.getExtension().toLowerCase())).findAny();
		if(optScripter.isPresent())
		{
			var p = optScripter.get();
			var f = new File(p.getScriptDirectory(),name);
			try {
				p.run(f);
			} catch (ScriptException e) {
				logger.error(e);
			}
		}
		else
		{
			logger.error("No scripter found for {}, Available ext are : {}",name,PluginRegistry.inst().listEnabledPlugins(MTGScript.class).stream().map(MTGScript::getExtension).toList());
		}



	}

}
