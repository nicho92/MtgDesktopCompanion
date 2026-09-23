package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.hangman.HangmanPanel;
import org.magic.services.MTGConstants;

public class HangmanDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;
	private HangmanPanel gamePanel;

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_GAME;
	}

	@Override
	public String getCategory() {
		return "Game";
	}

	@Override
	public void initGUI() {
	    
	    	setLayout(new BorderLayout());
	    	
	    	gamePanel = new HangmanPanel();
	    	
		getContentPane().add(gamePanel, BorderLayout.CENTER);

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")), (int) Double.parseDouble(getString("y")),
					(int) Double.parseDouble(getString("w")), (int) Double.parseDouble(getString("h")));

			setBounds(r);
		}
	}

	@Override
	public String getName() {
		return "MTGHangman";
	}

	@Override
	public void init() {
	    gamePanel.onStartGame();

	}

}