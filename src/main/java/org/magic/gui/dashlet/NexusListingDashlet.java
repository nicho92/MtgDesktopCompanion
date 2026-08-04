package org.magic.gui.dashlet;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;

import org.api.cardnexus.gui.ListManagementPanel;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.api.pricers.impl.CardNexusPricer;
import org.magic.services.tools.CardNexusTools;

public class NexusListingDashlet extends AbstractJDashlet {

  
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
	public String getCategory() {
		return "CardNexus";
	}
    
    @Override
    public void initGUI() {
	CardNexusTools.initConfig();
	setLayout(new BorderLayout());
	getContentPane().add(new ListManagementPanel());
	
    }

    @Override
    public void init() {
	
    }

    @Override
    public ImageIcon getDashletIcon() {
	return (ImageIcon) new CardNexusPricer().getIcon();
    }

    @Override
	public String getName() {
		return "NexusLists";
	}
    
}
