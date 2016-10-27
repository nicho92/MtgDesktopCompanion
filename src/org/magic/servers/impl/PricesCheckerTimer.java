package org.magic.servers.impl;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGDesktopCompanionControler;

public class PricesCheckerTimer extends AbstractMTGServer{

	
	Timer timer ;
	TimerTask tache ;
	private boolean running=false;
	static final Logger logger = LogManager.getLogger(PricesCheckerTimer.class.getName());

	
	public PricesCheckerTimer() {
		
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("AUTOSTART", "true");
			props.put("TIMEOUT_MINUTE", "120");
			save();
		}
		timer = new Timer();
	}
	

	public void start()
	{
		running=true;
		tache = new TimerTask() {    
            public void run() {
            	StringBuffer message=new StringBuffer();
            	boolean notify=false;
            	if(MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts()!=null)
            	for(MagicCardAlert alert : MTGDesktopCompanionControler.getInstance().getEnabledDAO().getAlerts())
                {
            		alert.getOffers().clear();
                	for(MagicPricesProvider prov : MTGDesktopCompanionControler.getInstance().getEnabledPricers())
                	{
                		try {
							List<MagicPrice> list=prov.getPrice(alert.getCard().getEditions().get(0), alert.getCard());
							for(MagicPrice p : list)
								if(p.getValue()<=alert.getPrice())
								{
									alert.getOffers().add(p);
									notify=true;
								}
						
							alert.orderDesc();
						} catch (Exception e) {
							logger.error(e);
						}
                	}
                	
                	if(notify)
                		message.append(alert.getCard()).append(" : ").append(alert.getOffers().size()).append(" offers").append("\n");
					
                }
            	
            	MTGDesktopCompanionControler.getInstance().notify("New offers", message.toString(), MessageType.INFO);
            	
            }
        };
		
		timer.scheduleAtFixedRate(tache,0,Long.parseLong(props.getProperty("TIMEOUT_MINUTE"))*60000);
		logger.info("Server start with "+ props.getProperty("TIMEOUT_MINUTE")+" min timeout");
	    
	}
	
	public void stop()
	{
		tache.cancel();
		timer.purge();
		running=false;
	}

	@Override
	public boolean isAlive() {
		return running;
	}

	@Override
	public String getName() {
		return "Price Timer";
		
	}


	@Override
	public boolean isAutostart() {
		return props.getProperty("AUTOSTART").equals("true");
	}
	
	
}
