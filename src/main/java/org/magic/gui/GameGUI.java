package org.magic.gui;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.GamingRoomPanel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
public class GameGUI extends MTGUIComponent {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_GAME;
	}
	
	@Override
	public String getTitle() {
		return capitalize("GAME_MODULE");
	}
	public GameGUI() {
		var pane = new JTabbedPane();
		
		pane.addTab(capitalize("GAME"), MTGConstants.ICON_TAB_GAME,GamePanelGUI.getInstance());
		pane.addTab(capitalize("CHAT_ROOM"),MTGConstants.ICON_TAB_CHAT, new GamingRoomPanel());
		setLayout(new BorderLayout());
		add(pane,BorderLayout.CENTER);
	}
}
