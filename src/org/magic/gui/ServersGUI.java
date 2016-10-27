package org.magic.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import org.magic.api.interfaces.MTGServer;
import org.magic.gui.components.ServerStatePanel;
import org.magic.services.MTGDesktopCompanionControler;

public class ServersGUI extends JPanel {

	
	public ServersGUI() {
		setLayout(new GridLayout(10, 1, 0, 0));
		for(MTGServer s : MTGDesktopCompanionControler.getInstance().getEnabledServers())
		{
			add(new ServerStatePanel(s));
		}
	}
}
