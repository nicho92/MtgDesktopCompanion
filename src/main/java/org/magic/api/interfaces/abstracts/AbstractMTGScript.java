package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;

import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;


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
	public void init()
	{
		addVariable("dao", MTGControler.getInstance().getEnabled(MTGDao.class));
		addVariable("provider", MTGControler.getInstance().getEnabled(MTGCardsProvider.class));
		addVariable("picture", MTGControler.getInstance().getEnabled(MTGPictureProvider.class));
		addVariable("indexer", MTGControler.getInstance().getEnabled(MTGCardsIndexer.class));
		addVariable("generator", MTGControler.getInstance().getEnabled(MTGTextGenerator.class));
		addVariable("pricers", MTGControler.getInstance().getEnabled(MTGPricesProvider.class));
		addVariable("dashboard",MTGControler.getInstance().getEnabled(MTGDashBoard.class));
		addVariable("sniffers",MTGControler.getInstance().listEnabled(MTGDeckSniffer.class));
	}
	
	@Override
	public boolean isJsr223() {
		return false;
	}
	
	@Override
	public void setOutput(Writer w) {
			
	}
	
	@Override
	public String getContentType() {
		return "text/"+getName().toLowerCase();
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
