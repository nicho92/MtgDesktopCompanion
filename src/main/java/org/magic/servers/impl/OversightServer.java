package org.magic.servers.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.api.sorters.PricesCardsShakeSorter;
import org.magic.api.sorters.PricesCardsShakeSorter.SORT;
import org.magic.services.MTGConstants;

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


	@Override
	public void start() {
		running = true;
		tache = new TimerTask() {
			@Override
			public void run() {
					List<CardShake> ret=null;
					try {
						ret = getEnabledPlugin(MTGDashBoard.class).getShakerFor(null);
						
						logger.debug("Filtering dayly change <{}% with array of {} results",getInt("ALERT_MIN_PERCENT"),ret.size());
						ret.removeIf(cs->Math.abs(cs.getPercentDayChange())<getInt("ALERT_MIN_PERCENT")/100);
						Collections.sort(ret, new PricesCardsShakeSorter(SORT.valueOf(getString("SORT_FILTER")),false));
					} catch (IOException e1) {
						logger.error(e1);
					}
					
					if(ret !=null && ret.isEmpty())
					{
						logger.debug("return of {} is empty",getEnabledPlugin(MTGDashBoard.class));
						return;
					}
					
					
					var notif = new MTGNotification();
									notif.setTitle("Oversight");
									notif.setType(MESSAGE_TYPE.INFO);

					for(var not : getArray("NOTIFIER"))
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
		var t = getLong(TIMEOUT_MINUTE);
		timer.scheduleAtFixedRate(tache, 0, t * 60000);
		logger.info("Server start with {}min timeout",t);

	}


	@Override
	public void stop() {
		tache.cancel();
		timer.purge();
		timer.cancel();
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
	public Map<String, String> getDefaultAttributes() {
		return Map.of("AUTOSTART", "false",
								TIMEOUT_MINUTE, "120",
								"ALERT_MIN_PERCENT","40",
								"NOTIFIER","Tray,Console",
								"SORT_FILTER","DAY_PRICE_CHANGE",
								"FORMAT_FILTER","");
	}

	@Override
	public String getVersion() {
		return "1.5";
	}


}
