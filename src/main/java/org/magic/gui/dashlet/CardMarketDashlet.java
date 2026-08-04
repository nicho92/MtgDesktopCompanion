package org.magic.gui.dashlet;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;

import org.api.mkm.exceptions.MkmException;
import org.api.mkm.tools.MkmAPIConfig;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.gui.MagicGUI;
import org.magic.services.AccountsManager;
import org.mkm.gui.MkmPanel;

public class CardMarketDashlet extends AbstractJDashlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void initGUI() {
	
	setLayout(new BorderLayout());
	
	getContentPane().add(new MkmPanel());
	
	
	
    }
    
    public String getCategory() {
	return "CardMarket";
}
    

    @Override
    public void init() {
	try {
	    MkmAPIConfig.getInstance().init(AccountsManager.inst().getAuthenticator(new MagicCardMarketPricer2()).getTokensAsProperties());
	} catch (MkmException e) {
	   logger.error(e);
	}
	
    }

    @Override
    public ImageIcon getDashletIcon() {
	return new ImageIcon(MagicGUI.class.getResource("/icons/plugins/magiccardmarket.png"));
    }

    @Override
    public String getName() {
       return "CardMarket";
    }
    
    
}
