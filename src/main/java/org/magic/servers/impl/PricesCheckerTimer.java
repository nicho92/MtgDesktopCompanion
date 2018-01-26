package org.magic.servers.impl;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class PricesCheckerTimer extends AbstractMTGServer{

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	Timer timer ;
	TimerTask tache ;
	private boolean running=false;
	private boolean enableNotify=true;
	
	public void enableGUINotify(boolean enableNotify) {
		this.enableNotify = enableNotify;
	}

	@Override
    public String description() {
    	return "AutoCheck prices for cards";
    }
	
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
            	StringBuilder message=new StringBuilder();
            	boolean notify=false;
            	if(MTGControler.getInstance().getEnabledDAO().getAlerts()!=null)
            	for(MagicCardAlert alert : MTGControler.getInstance().getEnabledDAO().getAlerts())
                {
            		alert.getOffers().clear();
                	for(MagicPricesProvider prov : MTGControler.getInstance().getEnabledPricers())
                	{
                		List<MagicPrice> okz = new ArrayList<MagicPrice>();
                		try {
							List<MagicPrice> list=prov.getPrice(alert.getCard().getEditions().get(0), alert.getCard());
							for(MagicPrice p : list)
							{	if(p.getValue()<=alert.getPrice() && p.getValue()>Double.parseDouble(MTGControler.getInstance().get("min-price-alert")))
								{
									alert.getOffers().add(p);
									okz.add(p);
									logger.info("Found offer " + prov + ":" + alert.getCard() + " "+ p.getValue() +p.getCurrency() );
									notify=true;
								}
							}
							prov.alertDetected(okz);
							alert.orderDesc();
						} catch (Exception e) {
							MTGLogger.printStackTrace(e);
							logger.error(e);
						}
                	}
                	
                		message.append(alert.getCard()).append(" : ").append(alert.getOffers().size()).append(" offers").append("\n");
					
                }
            	
            	if(enableNotify)
            		if(notify)
            			MTGControler.getInstance().notify("New offers", message.toString(), MessageType.INFO);
            	
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
