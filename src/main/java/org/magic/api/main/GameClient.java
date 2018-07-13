package org.magic.api.main;

import javax.swing.JFrame;

import org.magic.gui.GameGUI;
import org.magic.services.MTGConstants;
import org.magic.services.ThreadManager;

public class GameClient {

	public static void main(String[] args) {
		
		ThreadManager.getInstance().runInEdt(() -> {
			JFrame f = new JFrame();
			f.setIconImage(MTGConstants.ICON_GAME.getImage());
			f.getContentPane().add(new GameGUI());
			f.setVisible(true);
			f.pack();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
		
		
		
	}
	
}
