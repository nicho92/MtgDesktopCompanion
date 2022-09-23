package org.magic.api.interfaces.abstracts;

import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.listEnabledPlugins;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGPictureCache;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.FileTools;


public abstract class AbstractMTGScript extends AbstractMTGPlugin implements MTGScript{

	protected static final String DIR = "DIR";

	protected AbstractMTGScript() {
		if(!getFile(DIR).exists())
			try {
				FileUtils.forceMkdir(getFile(DIR));
			} catch (IOException e) {
				logger.error("Error creating {} {}",getFile(DIR),e);
			}
	}


	@Override
	public File getScriptDirectory() {
		return getFile(DIR);
	}

	@Override
	public void init()
	{
		addVariable("dao", getEnabledPlugin(MTGDao.class));
		addVariable("provider", getEnabledPlugin(MTGCardsProvider.class));
		addVariable("picture", getEnabledPlugin(MTGPictureProvider.class));
		addVariable("indexer", getEnabledPlugin(MTGCardsIndexer.class));
		addVariable("generator", getEnabledPlugin(MTGTextGenerator.class));
		addVariable("pricers", getEnabledPlugin(MTGPricesProvider.class));
		addVariable("dashboard",getEnabledPlugin(MTGDashBoard.class));
		addVariable("sniffers",listEnabledPlugins(MTGDeckSniffer.class));
		addVariable("cache",getEnabledPlugin(MTGPictureCache.class));
		addVariable("shoppers",listEnabledPlugins(MTGShopper.class));
		addVariable("importexporters",listEnabledPlugins(MTGCardsExport.class));
		addVariable("controler",MTGControler.getInstance());
		addVariable("servers",listEnabledPlugins(MTGServer.class));
		addVariable("externalShop",listEnabledPlugins(MTGExternalShop.class));
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
			logger.debug("running {}",f);
			return runContent(FileTools.readFile(f));
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
	public Map<String, String> getDefaultAttributes() {
		var m = new HashMap<String,String>();
		m.put(DIR, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "scripts").toFile().getAbsolutePath());
		return m;
	}
}
