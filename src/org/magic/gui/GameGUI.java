package org.magic.gui;

import javax.swing.JTabbedPane;

import org.magic.gui.components.GamingRoomPanel;
import org.magic.gui.game.components.GamePanelGUI;

public class GameGUI extends JTabbedPane {

	
	public GameGUI() {
		addTab("GameBoard", GamePanelGUI.getInstance());
		addTab("Chat Room", new GamingRoomPanel());
	}
}
