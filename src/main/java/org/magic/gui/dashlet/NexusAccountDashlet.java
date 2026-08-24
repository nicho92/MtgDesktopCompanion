package org.magic.gui.dashlet;

import javax.swing.JComponent;

import org.api.cardnexus.gui.NexusAccountPanel;
import org.magic.api.interfaces.abstracts.extra.AbstractNexusDashlet;

public class NexusAccountDashlet extends AbstractNexusDashlet {

  

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
	public String getName() {
		return "Nexus Account";
	}

    @Override
    protected JComponent getNexusComponent() {
	return new NexusAccountPanel();
    }
    
}
