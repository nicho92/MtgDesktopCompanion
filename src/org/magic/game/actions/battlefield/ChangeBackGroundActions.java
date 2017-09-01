package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class ChangeBackGroundActions extends AbstractAction {
	public ChangeBackGroundActions() {
			super("Change Background");
			putValue(SHORT_DESCRIPTION,"Change the background of Battlefield");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_B);
	        
	        
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser choose = new JFileChooser();
		choose.showOpenDialog(null);
		if(choose.getSelectedFile()!=null)
		{
			BufferedImage im;
			try {
				im = ImageIO.read(choose.getSelectedFile());
				GamePanelGUI.getInstance().getPanelBattleField().setBackgroundPicture(im);
				GamePanelGUI.getInstance().getPanelBattleField().repaint();
				
				MTGControler.getInstance().setProperty("/game/player-profil/background", choose.getSelectedFile().getAbsolutePath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
			
		

	}

}
