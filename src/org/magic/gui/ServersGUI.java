package org.magic.gui;

import javax.swing.JPanel;

import org.magic.api.interfaces.MTGServer;
import org.magic.gui.components.ServerStatePanel;
import org.magic.services.MagicFactory;
import java.awt.GridLayout;

public class ServersGUI extends JPanel {

	
	public ServersGUI() {
		setLayout(new GridLayout(10, 1, 0, 0));
		for(MTGServer s : MagicFactory.getInstance().getEnabledServers())
		{
			add(new ServerStatePanel(s));
		}
	}
}
