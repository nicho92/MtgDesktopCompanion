package org.magic.servers.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGAlert; 
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;

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
					var ret=new ArrayList<CardShake>();
					for (MTGAlert alert : getEnabledPlugin(MTGDao.class).listAlerts()) 
					{
						try {
							var cpv= getEnabledPlugin(MTGDashBoard.class).getPriceVariation(alert.getCard(),alert.isFoil());
							if(cpv!=null)
							{
								var cs = cpv.toCardShake();

								if(cs!=null) {
									alert.setShake(cs);
	
									if(Math.abs(cs.getPercentDayChange())>=getInt(ALERT_MIN_PERCENT))
										ret.add(cs);
	
									if(getInt(THREAD_PAUSE)!=null)
										ThreadManager.getInstance().sleep(getInt(THREAD_PAUSE));
	
									}
							}
						}
						catch(IOException e1)
						{
							logger.error(e1);
							alert.setShake(new CardShake());
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

		var duration = getLong(TIMEOUT_MINUTE);
		timer.scheduleAtFixedRate(tache, 0, duration * 60000);
		logger.info("Server start with {} min timeout",duration);

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
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of(AUTOSTART, MTGProperty.newBooleanProperty(FALSE, "Run server at startup"),
				   			 TIMEOUT_MINUTE, MTGProperty.newIntegerProperty("120","Timeout in minute when server will do the job",1,-1),
							   ALERT_MIN_PERCENT,MTGProperty.newIntegerProperty("40","Percentage threshold of the price variation where notification will be send",1,100),
							   THREAD_PAUSE,MTGProperty.newIntegerProperty("2000","Timeout in minute between each query. Used when external source block for ddos",2000,-1),
							   NOTIFIER, new MTGProperty("Tray,Console","select the notifiers to push information. Separated by comma. See  [Notifiers](Plugins#notifier)"));
	}

	@Override
	public String getVersion() {
		return "1.5";
	}



}
