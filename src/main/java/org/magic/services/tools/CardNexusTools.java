package org.magic.services.tools;

import org.api.cardnexus.configuration.NexusConfig;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.api.pricers.impl.CardNexusPricer;
import org.magic.services.AccountsManager;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class CardNexusTools {
    	
    	private CardNexusTools() {
	    // can't instanciate
	}
    
    
    	public static void initConfig()
    	{
    	    
    	    NexusConfig.setDefaultGameValue("mtg");
    	    NexusConfig.setToken(AccountsManager.inst().getAuthenticator(new CardNexusPricer()).get("CARDNEXUS_API_KEY"));
    	    NexusConfig.setTempDirectory(MTGConstants.DATA_DIR);
    	    NexusConfig.setFeedRententionDurationDays(1);
    	    NexusConfig.setAcceptLanguage(MTGControler.getInstance().getLocale().getCountry());
    	    
    	    NexusConfig.setListener(callInfo->{
		var info = new NetworkInfo();
			
			info.setStart(callInfo.getStart());
			info.setEnd(callInfo.getEnd());
			info.setReponse(callInfo.getResponse());
			info.setRequest(callInfo.getRequest());
			AbstractTechnicalServiceManager.inst().store(info);		
	    
	});
    	}
    
}
