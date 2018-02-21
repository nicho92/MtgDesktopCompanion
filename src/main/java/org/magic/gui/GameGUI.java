package org.magic.gui;

import javax.swing.JTabbedPane;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.GamingRoomPanel;
import org.magic.gui.components.SealedPanel;
import org.magic.services.MTGControler;

public class GameGUI extends JTabbedPane {

	
	
	public GameGUI() {
		addTab(MTGControler.getInstance().getLangService().getCapitalize("GAME"), GamePanelGUI.getInstance());
		addTab("Sealed", new SealedPanel());
		addTab(MTGControler.getInstance().getLangService().getCapitalize("CHAT_ROOM"), new GamingRoomPanel());
	}
}
