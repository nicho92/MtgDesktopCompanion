package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.time.DateUtils;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGConstants;

public abstract class AbstractDashBoard extends AbstractMTGPlugin implements MTGDashBoard {

	protected CollectionEvaluator evaluator;
	
	
	protected abstract List<CardShake> getShakeForEdition(MagicEdition ed) throws IOException;
	
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DASHBOARD;
	}

	public AbstractDashBoard() {
		super();
		try {
			evaluator = new CollectionEvaluator();
		} catch (IOException e) {
			logger.error(e);
		}
		confdir = new File(MTGConstants.CONF_DIR, "dashboards");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	
	@Override
	public String[] getDominanceFilters() {
		return new String[] { "" };
	}
	
	@Override
	public List<CardShake> getShakesForEdition(MagicEdition edition) throws IOException {
		
		Date c = evaluator.getCacheDate(edition);
		Date d = new Date();
		
		if(!DateUtils.isSameDay(c, d))
		{
			logger.debug(edition + " not in cache.Loading it");
			evaluator.initCache(edition,getShakeForEdition(edition));	
		}
		
		return evaluator.loadFromCache(edition);
		
		
		
		
	}
	
}
