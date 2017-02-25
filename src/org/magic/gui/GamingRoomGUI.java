package org.magic.gui;

import javax.swing.JTabbedPane;

import org.magic.gui.components.GamingRoomPanel;
import org.magic.gui.game.components.GamePanelGUI;

public class GamingRoomGUI extends JTabbedPane {

	
	public GamingRoomGUI() {
		addTab("GameBoard", GamePanelGUI.getInstance());
		addTab("Chat Room", new GamingRoomPanel());
	}
}
