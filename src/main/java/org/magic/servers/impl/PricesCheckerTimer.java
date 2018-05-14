package org.magic.servers.impl;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;

public class PricesCheckerTimer extends AbstractMTGServer {


	private Timer timer;
	private TimerTask tache;
	private boolean running = false;

	@Override
	public String description() {
		return "alerted cards offers oversight";
	}

	public PricesCheckerTimer() {

		super();
		timer = new Timer();
	}

	public void start() {
		running = true;
		tache = new TimerTask() {
			public void run() {
				if (MTGControler.getInstance().getEnabledDAO().listAlerts() != null)
					for (MagicCardAlert alert : MTGControler.getInstance().getEnabledDAO().listAlerts()) {
						alert.getOffers().clear();
						for (MTGPricesProvider prov : MTGControler.getInstance().getEnabledPricers()) 
						{
							List<MagicPrice> okz = new ArrayList<>();
							try {
								List<MagicPrice> list = prov.getPrice(alert.getCard().getCurrentSet(),alert.getCard());
								for (MagicPrice p : list) {
									if (p.getValue() <= alert.getPrice() && p.getValue() > Double.parseDouble(MTGControler.getInstance().get("min-price-alert"))) {
										alert.getOffers().add(p);
										okz.add(p);
										logger.info("Found offer " + prov + ":" + alert.getCard() + " " + p.getValue()+ p.getCurrency());
									}
								}
								prov.alertDetected(okz);
								alert.orderDesc();
							} catch (Exception e) {
								logger.error("error loading price " + prov, e);
							}
						}
					}
				
					MTGNotification notif = new MTGNotification();
					notif.setTitle("New offers");
					notif.setType(MessageType.INFO);
					for(String not : getString("NOTIFIER").split(","))
					{
						try {
										
							MTGNotifier notifier = MTGControler.getInstance().getPlugin(not, MTGNotifier.class);
							notif.setMessage(notifFormater.generate(notifier.getFormat(), MTGControler.getInstance().getEnabledDAO().listAlerts(), MagicCardAlert.class));
							notifier.send(notif);
						} catch (IOException e) {
							logger.error(e);
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
		return "Alert Price Checker";

	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public void initDefault() {
		setProperty("AUTOSTART", "false");
		setProperty("TIMEOUT_MINUTE", "120");
		setProperty("NOTIFIER","Tray");
	}

	@Override
	public String getVersion() {
		return "1.5";
	}

}
