package org.magic.gui;

import java.awt.GridLayout;

import javax.swing.ImageIcon;

import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ServerStatePanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class ServersGUI extends MTGUIComponent {

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
		
		ThreadManager.getInstance().runInEdt(()->{
				for (MTGServer s : MTGControler.getInstance().getPlugins(MTGServer.class)) {
					add(new ServerStatePanel(s));
				}
		});
		
		
	}
}
