package org.magic.api.main;

import javax.swing.JFrame;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.GameGUI;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class GameClient {

	public static void main(String[] args) {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		
		ThreadManager.getInstance().invokeLater(() -> {
			JFrame f = new JFrame(MTGControler.getInstance().getLangService().getCapitalize("GAME_MODULE"));
			f.setIconImage(MTGConstants.ICON_GAME.getImage());
			f.getContentPane().add(new GameGUI());
			f.setVisible(true);
			f.pack();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
		
		
		
	}
	
}
