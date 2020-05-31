package org.magic.tools;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.script.ScriptException;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGScript;
import org.magic.services.MTGLogger;
import org.magic.services.PluginRegistry;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScripterJob implements Job {

	
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		
		String name=context.getJobDetail().getJobDataMap().get("SCRIPT_NAME").toString();
		
		Optional<MTGScript> optScripter = PluginRegistry.inst().listEnabledPlugins(MTGScript.class).stream().filter(s->FilenameUtils.getExtension(name).toLowerCase().endsWith(s.getExtension().toLowerCase())).findAny();
		if(optScripter.isPresent())
		{
			MTGScript p = optScripter.get();
			File f = new File(p.getScriptDirectory(),name);
			try {
				p.run(f);
			} catch (ScriptException e) {
				logger.error(e);
			}
		}
		else
		{
			logger.error("No scripter found for "+ name + "Available ext are : " + PluginRegistry.inst().listEnabledPlugins(MTGScript.class).stream().map(MTGScript::getExtension).collect(Collectors.toList()));
		}
		
		

	}

}
