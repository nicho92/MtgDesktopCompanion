package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.MTGScript;
import org.magic.services.MTGConstants;

import groovy.util.ResourceException;
import groovy.util.ScriptException;


public abstract class AbstractMTGScript extends AbstractMTGPlugin implements MTGScript{
	
	protected static final String DIR = "DIR";
	
	public AbstractMTGScript() {
		super();
		
		confdir = new File(MTGConstants.CONF_DIR, "scripts");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
		
		if(!getFile(DIR).exists())
			try {
				FileUtils.forceMkdir(getFile(DIR));
			} catch (IOException e) {
				logger.error("Error creating " + getFile(DIR),e);
			}
		
	}
	
	
	@Override
	public Object run(File f) throws ResourceException, ScriptException {
		try {
			logger.debug("running " + f);
			return runContent(FileUtils.readFileToString(f, MTGConstants.DEFAULT_ENCODING));
		} catch (IOException e) {
			throw new ScriptException(e);
		}
	}
	
	
	@Override
	public Object run(String scriptName) throws ResourceException, ScriptException {
		return run(Paths.get(getFile(DIR).getAbsolutePath(),scriptName+"."+getExtension()).toFile());
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.SCRIPT;
	}
	
	@Override
	public void initDefault() {
		setProperty(DIR, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "scripts").toFile().getAbsolutePath());
	}
	
}
