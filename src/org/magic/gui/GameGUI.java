package org.magic.gui;

import javax.swing.JTabbedPane;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.gui.components.GamingRoomPanel;

public class GameGUI extends JTabbedPane {

	
	
	public GameGUI() {
		addTab("Game", GamePanelGUI.getInstance());
		addTab("Chat Room", new GamingRoomPanel());
	}
}
