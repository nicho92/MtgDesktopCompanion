package org.magic.servers.impl;

import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.getPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.sorters.PricesCardsShakeSorter;
import org.magic.sorters.PricesCardsShakeSorter.SORT;

public class OversightServer extends AbstractMTGServer {
	private static final String TIMEOUT_MINUTE = "TIMEOUT_MINUTE";
	private Timer timer;
	private TimerTask tache;
	private boolean running = false;

	@Override
	public String description() {
		return "Oversight for daily prices variations";
	}

	public OversightServer() {
		super();
		timer = new Timer();
	}
	
	
	public void start() {
		running = true;
		tache = new TimerTask() {
			public void run() {
					List<CardShake> ret=null;
					try {
						ret = getEnabledPlugin(MTGDashBoard.class).getShakerFor(null);
						ret.removeIf(cs->Math.abs(cs.getPercentDayChange())<getInt("ALERT_MIN_PERCENT"));
						Collections.sort(ret, new PricesCardsShakeSorter(SORT.valueOf(getString("SORT_FILTER")),false));
					} catch (IOException e1) {
						logger.error(e1);
					}
				
					var notif = new MTGNotification();
									notif.setTitle("Oversight");
									notif.setType(MESSAGE_TYPE.INFO);
									
					for(String not : getString("NOTIFIER").split(","))
					{
						MTGNotifier notifier = getPlugin(not, MTGNotifier.class);
						notif.setMessage(notifFormater.generate(notifier.getFormat(), ret, CardShake.class));
						try {
							notifier.send(notif);
						} catch (IOException e) {
							logger.error(e);
						}
					}
				
			}

		};

		timer.scheduleAtFixedRate(tache, 0, Long.parseLong(getString(TIMEOUT_MINUTE)) * 60000);
		logger.info("Server start with " + getString(TIMEOUT_MINUTE) + " min timeout");

	}

	
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
		return "Oversight Server";

	}
	
	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_DASHBOARD;
	}
	

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public void initDefault() {
		setProperty("AUTOSTART", "false");
		setProperty(TIMEOUT_MINUTE, "120");
		setProperty("ALERT_MIN_PERCENT","40");
		setProperty("NOTIFIER","Tray,Console");
		setProperty("SORT_FILTER","DAY_PRICE_CHANGE");
		setProperty("FORMAT_FILTER","");
	}

	@Override
	public String getVersion() {
		return "1.5";
	}


}
