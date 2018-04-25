package org.magic.gui;

import javax.swing.JTabbedPane;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.GamingRoomPanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class GameGUI extends JTabbedPane {

	public GameGUI() {
		addTab(MTGControler.getInstance().getLangService().getCapitalize("GAME"), MTGConstants.ICON_TAB_GAME,GamePanelGUI.getInstance());
		addTab(MTGControler.getInstance().getLangService().getCapitalize("CHAT_ROOM"),MTGConstants.ICON_TAB_CHAT, new GamingRoomPanel());
	}
}
