package org.magic.gui.dashlet;

import javax.swing.JComponent;

import org.api.cardnexus.gui.NexusListsPanel;
import org.magic.api.interfaces.abstracts.extra.AbstractNexusDashlet;

public class NexusListingDashlet extends AbstractNexusDashlet {


    private static final long serialVersionUID = 1L;

    @Override
	public String getName() {
		return "NexusLists";
	}

    @Override
    protected JComponent getNexusComponent() {
	return new NexusListsPanel();
    }

}
