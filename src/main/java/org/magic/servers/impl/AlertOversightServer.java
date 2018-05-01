package org.magic.servers.impl;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.magic.api.beans.MagicCardAlert;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;

import com.google.common.collect.Iterables;

public class AlertOversightServer extends AbstractMTGServer {

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	private Timer timer;
	private TimerTask tache;
	private boolean running = false;
	private boolean enableNotify = true;
	private NumberFormat formatter = new DecimalFormat("#0.00");  
	public void enableGUINotify(boolean enableNotify) {
		this.enableNotify = enableNotify;
	}

	@Override
	public String description() {
		return "Alerts supervision server";
	}

	public AlertOversightServer() {

		super();
		timer = new Timer();
	}

	public void start() {
		running = true;
		tache = new TimerTask() {
			public void run() {
				StringBuilder message = new StringBuilder();
				boolean notify = false;
				if (MTGControler.getInstance().getEnabledDAO().listAlerts() != null)
					for (MagicCardAlert alert : MTGControler.getInstance().getEnabledDAO().listAlerts()) {
						Map<Date,Double> map = null;
						try {
							map = MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(alert.getCard(), alert.getCard().getCurrentSet());
							if(map!=null)
							{
								List<Entry<Date, Double>> res = new ArrayList<>(map.entrySet());
								
								Calendar c = GregorianCalendar.getInstance();
										 c.setTime(res.get(res.size()-1).getKey());
										 
										 Date now = c.getTime();
										 c.set(Calendar.DAY_OF_MONTH, -1);
										 Date yesterday = c.getTime();
										 c.set(Calendar.DAY_OF_MONTH, -7);
										 Date week = c.getTime();
										 
								double valDay = map.get(now) - map.get(yesterday);
								double valWeek = map.get(now) - map.get(week);		 
								
								double pcWeek = (map.get(now) - map.get(week))/map.get(week)*100;
								double pcDay = (map.get(now) - map.get(yesterday))/map.get(yesterday)*100;
							
								if(valDay>0)
									message.append(alert.getCard() + " is up "+formatter.format(pcDay)+"\n");
									else
									message.append(alert.getCard() + " is down\n");	
								
								alert.setTrendingDay(valDay);
								alert.setTrendingWeek(valWeek);							
								
							}
						} catch (IOException e) {
							logger.error(e);
						}
						
						
						
					}

				if (enableNotify && message.length()>0)
					MTGControler.getInstance().notify("Oversight", message.toString(), MessageType.INFO);

			}
		};

		timer.scheduleAtFixedRate(tache, 0, Long.parseLong(getString("TIMEOUT_MINUTE")) * 60000);
		logger.info("Server start with " + getString("TIMEOUT_MINUTE") + " min timeout");

	}

	protected void calculate(MagicCardAlert alert, Date time, Date key) {
		// TODO Auto-generated method stub
		
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
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public void initDefault() {
		setProperty("AUTOSTART", "true");
		setProperty("TIMEOUT_MINUTE", "120");

	}

	@Override
	public String getVersion() {
		return "1.5";
	}

}
