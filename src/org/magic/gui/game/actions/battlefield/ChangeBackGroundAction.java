package org.magic.gui.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.magic.gui.game.components.BattleFieldPanel;
import org.magic.services.MTGControler;

public class ChangeBackGroundAction extends AbstractAction {

	BattleFieldPanel battleFieldPanel;
	
	public ChangeBackGroundAction(BattleFieldPanel battleFieldPanel) {
			super("Change Background");
			putValue(SHORT_DESCRIPTION,"Change the background of Battlefield");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_B);
	        this.battleFieldPanel=battleFieldPanel;
	        
	        
	        if(MTGControler.getInstance().get("/player-profil/background")!=null)
	        try {
	        	BufferedImage im = ImageIO.read(new File(MTGControler.getInstance().get("/player-profil/background")));
				battleFieldPanel.setBackgroundPicture(im);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
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
				battleFieldPanel.setBackgroundPicture(im);
				battleFieldPanel.repaint();
				
				MTGControler.getInstance().setProperty("/player-profil/background", choose.getSelectedFile().getAbsolutePath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
			
		

	}

}
