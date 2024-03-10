package org.magic.servers.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;

public class AlertTrendServer extends AbstractMTGServer {

	private static final String NOTIFIER = "NOTIFIER";
	private static final String THREAD_PAUSE = "THREAD_PAUSE";
	private static final String ALERT_MIN_PERCENT = "ALERT_MIN_PERCENT";
	private static final String TIMEOUT_MINUTE = "TIMEOUT_MINUTE";
	private static final String AUTOSTART = "AUTOSTART";
	private Timer timer;
	private TimerTask tache;
	private boolean running = false;

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_ALERT;
	}

	@Override
	public String description() {
		return "return price variation for alerted cards";
	}

	public AlertTrendServer() {

		super();
		timer = new Timer();
	}

	@Override
	public void start() {
		running = true;
		tache = new TimerTask() {
			@Override
			public void run() {
				List<CardShake> ret=new ArrayList<>();
				if (getEnabledPlugin(MTGDao.class).listAlerts() != null)
					for (MTGAlert alert : getEnabledPlugin(MTGDao.class).listAlerts()) {
						try {
							HistoryPrice<MTGCard> cpv= getEnabledPlugin(MTGDashBoard.class).getPriceVariation(alert.getCard(),alert.isFoil());
							if(cpv!=null)
							{
								var cs = cpv.toCardShake();

								if(cs!=null) {
								alert.setShake(cs);

								if(Math.abs(cs.getPercentDayChange())>=getInt(ALERT_MIN_PERCENT))
									ret.add(cs);

								if(getInt(THREAD_PAUSE)!=null)
									Thread.sleep(getInt(THREAD_PAUSE));

								}
							}
						}
						catch(IOException e1)
						{
							logger.error(e1);
							alert.setShake(new CardShake());
						}
						catch(InterruptedException ex)
						{
							Thread.currentThread().interrupt();
						}
						catch(Exception e)
						{
							logger.error("Error starting", e);
							alert.setShake(new CardShake());
							running=false;
						}
					}

				if(!ret.isEmpty())
				{
					var notif = new MTGNotification();
					notif.setTitle("Alert Trend Cards");
					notif.setType(MESSAGE_TYPE.INFO);

					for(String not : getArray(NOTIFIER))
					{
						if(!not.isEmpty())
						{
							logger.debug("notify with {} ",not);
							var notifier = getPlugin(not, MTGNotifier.class);
							notif.setMessage(notifFormater.generate(notifier.getFormat(), ret, CardShake.class));
							try {
								notifier.send(notif);
							} catch (IOException e) {
								logger.error(e);
							}
						}

					}
				}
				else
				{
					logger.warn("nothing to notify");
				}


			}
		};

		timer.scheduleAtFixedRate(tache, 0, Long.parseLong(getString(TIMEOUT_MINUTE)) * 60000);
		logger.info("Server start with {} min timeout",getString(TIMEOUT_MINUTE));

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
		return "Alert Trend Server";

	}

	@Override
	public boolean isAutostart() {
		return getBoolean(AUTOSTART);
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(AUTOSTART, "false",
							   TIMEOUT_MINUTE, "120",
							   ALERT_MIN_PERCENT,"40",
							   THREAD_PAUSE,"2000",
							   NOTIFIER,"Tray,Console");
	}

	@Override
	public String getVersion() {
		return "1.5";
	}



}
