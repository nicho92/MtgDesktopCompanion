package org.magic.servers.impl;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;

public class AlertTrendServer extends AbstractMTGServer {

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

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
				StringBuilder message = new StringBuilder();
				if (MTGControler.getInstance().getEnabledDAO().listAlerts() != null)
					for (MagicCardAlert alert : MTGControler.getInstance().getEnabledDAO().listAlerts()) {
						Map<Date,Double> map = null;
						try {
							map = MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(alert.getCard(), alert.getCard().getCurrentSet());
							if(map!=null)
							{
								List<Entry<Date, Double>> res = new ArrayList<>(map.entrySet());
								Date now = res.get(res.size()-1).getKey();
								Date yesterday = res.get(res.size()-2).getKey();
								Date week = res.get(res.size()-7).getKey();

								double valDay = map.get(now) - map.get(yesterday);
								double valWeek = map.get(now) - map.get(week);		 
								double pcWeek = (map.get(now) - map.get(week))/map.get(week)*100;
								double pcDay = (map.get(now) - map.get(yesterday))/map.get(yesterday)*100;
								
								CardShake cs = new CardShake();
								cs.setCard(alert.getCard());
								cs.setEd(cs.getCard().getCurrentSet().getSet());
								cs.setDateUpdate(new Date());
								cs.setPercentDayChange(pcDay);
								cs.setPercentWeekChange(pcWeek);
								cs.setPriceDayChange(valDay);
								cs.setPriceWeekChange(valWeek);
								cs.setPrice(map.get(now));
								alert.setShake(cs);
								
								if(Math.abs(pcDay)>=getInt("ALERT_MIN_PERCENT"))
								{
									
										message.append(alert.getCard())
											   .append("(")
											   .append(alert.getCard().getCurrentSet().getId())
											   .append(") : ")
											   .append( (pcDay>0)?"+":"")
											   .append(formatter.format(pcDay)+"%")
											   .append(" -> ")
											   .append(alert.getShake().getPrice())
											   .append("$\n");
								}			
								
								
								
								if(getInt("THREAD_PAUSE")!=null)
									Thread.sleep(getInt("THREAD_PAUSE"));
							}
						} catch (IOException e)
						{
							
							logger.error(e);
						}
						catch(InterruptedException e)
						{
							logger.error(e);
							running=false;
						}
						
						
						
					}
				
				if(message.length()>0)
				{
					MTGNotification notif = new MTGNotification();
									notif.setTitle("Alerts Variation");
									notif.setMessage(message.toString());
									notif.setType(MessageType.INFO);
				
					for(String not : getString("NOTIFIER").split(","))
					{
						try {
						MTGControler.getInstance().getNotifier(not).send(notif);
						} catch (IOException e) {
							logger.error(e);
						}
					}
				}
				

			}
		};

		timer.scheduleAtFixedRate(tache, 0, Long.parseLong(getString("TIMEOUT_MINUTE")) * 60000);
		logger.info("Server start with " + getString("TIMEOUT_MINUTE") + " min timeout");

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
		return getBoolean("AUTOSTART");
	}

	@Override
	public void initDefault() {
		setProperty("AUTOSTART", "false");
		setProperty("TIMEOUT_MINUTE", "120");
		setProperty("ALERT_MIN_PERCENT","40");
		setProperty("THREAD_PAUSE","2000");
		setProperty("NOTIFIER","Tray,Console");
	}

	@Override
	public String getVersion() {
		return "1.5";
	}

}
