package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.scripts.impl.JavaScript;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;


public abstract class AbstractMTGScript extends AbstractMTGPlugin implements MTGScript{
	
	protected static final String DIR = "DIR";
	
	protected Bindings binds;
	protected ScriptEngine engine;
	
	
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
	public boolean isJsr223() {
		return true;
	}
	
	
	@Override
	public Object runContent(String content) throws ScriptException {
		init();
		return engine.eval(content,binds);
	}
	
	protected void init() {
		if(isJsr223()) {
			engine = new ScriptEngineManager().getEngineByName(getName());
			binds = engine.createBindings();
			binds.put("dao", MTGControler.getInstance().getEnabled(MTGDao.class));
			binds.put("provider", MTGControler.getInstance().getEnabled(MTGCardsProvider.class));
			binds.put("picture", MTGControler.getInstance().getEnabled(MTGPictureProvider.class));
		}
		else
		{
			logger.warn(getName() + " is not jsr223 compatible. engine property still null");
		}
	}


	@Override
	public Object run(File f) throws ScriptException {
		try {
			logger.debug("running " + f);
			return runContent(FileUtils.readFileToString(f, MTGConstants.DEFAULT_ENCODING));
		} catch (IOException e) {
			throw new ScriptException(e);
		}
	}
	
	
	@Override
	public Object run(String scriptName) throws ScriptException {
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
