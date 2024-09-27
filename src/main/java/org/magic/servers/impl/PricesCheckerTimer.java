package org.magic.servers.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import javax.swing.Icon;

import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class PricesCheckerTimer extends AbstractMTGServer {


	private static final String TIMEOUT_MINUTE = "TIMEOUT_MINUTE";
	private Timer timer;
	private TimerTask tache;
	private boolean running = false;

	@Override
	public String description() {
		return "alerted cards offers oversight";
	}

	public PricesCheckerTimer() {
		timer = new Timer();
	}

	@Override
	public void start() {
		running = true;
		tache = new TimerTask() {
			@Override
			public void run() {
				if (getEnabledPlugin(MTGDao.class).listAlerts() != null)
					for (MTGAlert alert : getEnabledPlugin(MTGDao.class).listAlerts()) {
						alert.getOffers().clear();
						for (MTGPricesProvider prov : listEnabledPlugins(MTGPricesProvider.class))
						{
							List<MTGPrice> okz = new ArrayList<>();
							try {
								Stream<MTGPrice> stream = prov.getPrice(alert.getCard()).stream().filter(p->p.getValue() <= alert.getPrice()&& p.getValue() > Double.parseDouble(MTGControler.getInstance().get("min-price-alert")));

								if(alert.isFoil())
									stream=stream.filter(MTGPrice::isFoil);


								List<MTGPrice> res= stream.toList();
								alert.getOffers().addAll(res);
								okz.addAll(res);
								prov.alertDetected(okz);
								alert.orderDesc();
							} catch (Exception e) {
								logger.error("error loading price {}",prov, e);
							}
						}
					}

				var notif = new MTGNotification();
					notif.setTitle("New offers");
					notif.setType(MESSAGE_TYPE.INFO);
					for(String not : getArray("NOTIFIER"))
					{
						try {

							MTGNotifier notifier = getPlugin(not, MTGNotifier.class);
							notif.setMessage(notifFormater.generate(notifier.getFormat(), getEnabledPlugin(MTGDao.class).listAlerts(), MTGAlert.class));
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
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("AUTOSTART", MTGProperty.newBooleanProperty(FALSE, "Run server at startup"),
								TIMEOUT_MINUTE, MTGProperty.newIntegerProperty("120","Timeout in minute where server will do the job",1,-1),
								"NOTIFIER", new MTGProperty("Tray","select the notifiers where you wan't to send the information. Separated by comma. See [plugins](Plugins#notifier)"));
		
		
	}

	@Override
	public String getVersion() {
		return "1.5";
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_EURO;
	}

}
