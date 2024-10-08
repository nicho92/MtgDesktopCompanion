package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.network.NetworkChatPanel;
import org.magic.services.MTGConstants;

public class NetworkGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;


	@Override
	public String getTitle() {
		return capitalize("NETWORK");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_NETWORK;
	}


	public NetworkGUI() {
		setLayout(new BorderLayout());

		add(new NetworkChatPanel(),BorderLayout.CENTER);
	}

}
