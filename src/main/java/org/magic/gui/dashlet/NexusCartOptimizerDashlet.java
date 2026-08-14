package org.magic.gui.dashlet;

import javax.swing.JComponent;

import org.api.cardnexus.gui.NexusWizardPanel;
import org.magic.api.interfaces.abstracts.extra.AbstractNexusDashlet;

public class NexusCartOptimizerDashlet extends AbstractNexusDashlet {

    private static final long serialVersionUID = 1L;

    @Override
	public String getName() {
		return "Nexus Cart Optimizer";
	}

    @Override
    protected JComponent getNexusComponent() {
	return new NexusWizardPanel();
    }
    
}
