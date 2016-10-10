package org.magic.servers.impl;

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
import org.magic.services.MagicFactory;

public class PricesCheckerTimer extends AbstractMTGServer{

	
	Timer timer ;
	TimerTask tache ;
	private boolean running=false;
	static final Logger logger = LogManager.getLogger(PricesCheckerTimer.class.getName());

	
	public PricesCheckerTimer() {
		
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("AUTOSTART", "true");
			props.put("TIMEOUT", "60000");
			save();
		}
		
		timer = new Timer();
		
		if(props.getProperty("AUTOSTART").equals("true"))
        	start();
        
	}
	

	public void start()
	{
		tache = new TimerTask() {    
            public void run() {
            	
            	if(MagicFactory.getInstance().getEnabledDAO().getAlerts()!=null)
            	for(MagicCardAlert alert : MagicFactory.getInstance().getEnabledDAO().getAlerts())
                {
            		alert.getOffers().clear();
                	for(MagicPricesProvider prov : MagicFactory.getInstance().getEnabledPricers())
                	{
                		try {
							List<MagicPrice> list=prov.getPrice(alert.getCard().getEditions().get(0), alert.getCard());
							for(MagicPrice p : list)
								if(p.getValue()<=alert.getPrice())
									alert.getOffers().add(p);
							
						} catch (Exception e) {
							logger.error(e);
							//e.printStackTrace();
						}
                	}
                }
            }
        };
		
		timer.scheduleAtFixedRate(tache,0,Long.parseLong(props.getProperty("TIMEOUT")));
		running=true;
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
	
	
}
