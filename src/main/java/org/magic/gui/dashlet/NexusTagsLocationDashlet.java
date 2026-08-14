package org.magic.gui.dashlet;

import javax.swing.JComponent;

import org.api.cardnexus.gui.NexusTagsAndLocationPanel;
import org.magic.api.interfaces.abstracts.extra.AbstractNexusDashlet;

public class NexusTagsLocationDashlet extends AbstractNexusDashlet{

  
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
	public String getName() {
		return "Nexus tags and Location";
	}

    @Override
    protected JComponent getNexusComponent() {
	return new NexusTagsAndLocationPanel();
    }
    
}
