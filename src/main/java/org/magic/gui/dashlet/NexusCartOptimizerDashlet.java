package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.api.cardnexus.gui.NexusWizardPanel;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.api.pricers.impl.CardNexusPricer;
import org.magic.services.tools.CardNexusTools;

public class NexusCartOptimizerDashlet extends AbstractJDashlet {

  
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
	getContentPane().add(new NexusWizardPanel());
	
	if (getProperties().size() > 0) {
		var r = new Rectangle((int) Double.parseDouble(getString("x")), (int) Double.parseDouble(getString("y")),
				(int) Double.parseDouble(getString("w")), (int) Double.parseDouble(getString("h")));

		setBounds(r);
	}
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
		return "Nexus Cart Optimizer";
	}
    
}
