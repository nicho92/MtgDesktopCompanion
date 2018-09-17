package org.magic.gui;

import java.awt.GridLayout;

import javax.swing.ImageIcon;

import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.MTGUIPanel;
import org.magic.gui.components.ServerStatePanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class ServersGUI extends MTGUIPanel {

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_ACTIVESERVER;
	}
	
	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("ACTIVE_SERVERS");
	}
	
	
	public ServersGUI() {
		setLayout(new GridLayout(10, 1, 0, 0));
		for (MTGServer s : MTGControler.getInstance().getServers()) {
			add(new ServerStatePanel(s));
		}
	}
}
