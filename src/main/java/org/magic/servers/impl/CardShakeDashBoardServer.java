package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;

import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.CollectionEvaluator;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

public class CardShakeDashBoardServer extends AbstractMTGServer {

	private static final String THREAD_PAUSE = "THREAD_PAUSE";
	private static final String TIMEOUT_MINUTE = "TIMEOUT_MINUTE";
	private static final String AUTOSTART = "AUTOSTART";
	private static final String COLLECTION="COLLECTION";
	private Timer timer;
	private TimerTask tache;
	private boolean running = false;

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_DASHBOARD;
	}

	@Override
	public String description() {
		return "backup prices editions";
	}

	public CardShakeDashBoardServer() {

		super();
		timer = new Timer();
	}

	@Override
	public void start() {
		running = true;
		tache = new TimerTask() {
			@Override
			public void run() {

					File dest;
					CollectionEvaluator evaluator;
					try {
						evaluator = new CollectionEvaluator(new MagicCollection(getString(COLLECTION)));
						logger.debug("backuping files");
						dest = new File(evaluator.getDirectory(),new SimpleDateFormat("yyyyMMdd").format(new Date()));
					} catch (IOException e1) {
						logger.error(e1);
						return;
					}


					for(File f : evaluator.getDirectory().listFiles(pathname->!pathname.isDirectory())){
						try {
							FileTools.moveFileToDirectory(f, dest, true);
						} catch (IOException e) {
							logger.error(e);
						}
					}

					logger.debug("updating cache");
					try {
						evaluator.initCache();
					} catch (IOException e) {
						logger.error(e);
					}
					logger.info("cache update done");


			}
		};

		timer.scheduleAtFixedRate(tache, 0, Long.parseLong(getString(TIMEOUT_MINUTE)) * 60000);
		logger.info(()->"Server start with {} min timeout "+getString(TIMEOUT_MINUTE));

	}


	@Override
	public void stop() {
		tache.cancel();
		timer.purge();
		running = false;
	}

	@Override
	public boolean isAlive() {
		return running;
	}

	@Override
	public String getName() {
		return "CardShake cache server";

	}

	@Override
	public boolean isAutostart() {
		return getBoolean(AUTOSTART);
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(AUTOSTART, "false",
							   TIMEOUT_MINUTE, "1440",
							   THREAD_PAUSE,"2000",
							   COLLECTION,"Library");
	}

	@Override
	public String getVersion() {
		return "1.5";
	}



}
