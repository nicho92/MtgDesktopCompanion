package org.magic.servers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;

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
	public String description() {
		return "return price variation for alerted cards";
	}

	public AlertTrendServer() {

		super();
		timer = new Timer();
	}
	
	public void start() {
		running = true;
		tache = new TimerTask() {
			public void run() {
				List<CardShake> ret=new ArrayList<>();
				if (MTGControler.getInstance().getEnabledDAO().listAlerts() != null)
					for (MagicCardAlert alert : MTGControler.getInstance().getEnabledDAO().listAlerts()) {
						try {
							CardPriceVariations map= MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(alert.getCard(), alert.getCard().getCurrentSet());
							if(map!=null)
							{
								Date now = map.getLastDay();
								Date yesterday = map.getYesterday();
								Date week = map.getLastWeek();

								double valDay = map.get(now) - map.get(yesterday);
								double valWeek = map.get(now) - map.get(week);		 
								double pcWeek = (map.get(now) - map.get(week))/map.get(week)*100;
								double pcDay = (map.get(now) - map.get(yesterday))/map.get(yesterday)*100;
								
								CardShake cs = new CardShake();
								cs.setCard(alert.getCard());
								cs.setName(cs.getCard().getName());

								cs.setEd(cs.getCard().getCurrentSet().getSet());
								cs.setDateUpdate(new Date());
								cs.setPercentDayChange(pcDay);
								cs.setPercentWeekChange(pcWeek);
								cs.setPriceDayChange(valDay);
								cs.setPriceWeekChange(valWeek);
								cs.setPrice(map.get(now));
								alert.setShake(cs);
								
								
								if(Math.abs(cs.getPercentDayChange())>=getInt(ALERT_MIN_PERCENT))
									ret.add(cs);
							
								if(getInt(THREAD_PAUSE)!=null)
									Thread.sleep(getInt(THREAD_PAUSE));
							}
						}
						catch(IOException e1)
						{
							logger.error(e1);
						}
						catch(Exception e)
						{
							logger.error(e);
							running=false;
						}
					}
				
				if(!ret.isEmpty())
				{
					MTGNotification notif = new MTGNotification();
					notif.setTitle("Alert Trend Cards");
					notif.setType(MESSAGE_TYPE.INFO);
					
					for(String not : getString(NOTIFIER).split(","))
					{
						MTGNotifier notifier = MTGControler.getInstance().getPlugin(not, MTGNotifier.class);
						notif.setMessage(notifFormater.generate(notifier.getFormat(), ret, CardShake.class));
						try {
							notifier.send(notif);
						} catch (IOException e) {
							logger.error(e);
						}
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
		return "Alert Trend Server";

	}

	@Override
	public boolean isAutostart() {
		return getBoolean(AUTOSTART);
	}

	@Override
	public void initDefault() {
		setProperty(AUTOSTART, "false");
		setProperty(TIMEOUT_MINUTE, "120");
		setProperty(ALERT_MIN_PERCENT,"40");
		setProperty(THREAD_PAUSE,"2000");
		setProperty(NOTIFIER,"Tray,Console");
	}

	@Override
	public String getVersion() {
		return "1.5";
	}

}
