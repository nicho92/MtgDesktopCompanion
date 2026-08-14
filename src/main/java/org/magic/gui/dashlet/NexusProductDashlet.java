package org.magic.gui.dashlet;

import javax.swing.JComponent;

import org.api.cardnexus.gui.NexusProductPanel;
import org.magic.api.interfaces.abstracts.extra.AbstractNexusDashlet;

public class NexusProductDashlet extends AbstractNexusDashlet {

  
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
	public String getName() {
		return "Nexus Products";
	}

    @Override
    protected JComponent getNexusComponent() {
	return new NexusProductPanel(true,true);
    }
    
}
