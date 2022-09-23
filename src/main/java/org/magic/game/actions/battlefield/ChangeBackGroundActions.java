package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.apache.logging.log4j.Logger;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.tools.ImageTools;

public class ChangeBackGroundActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	private File lastDir = MTGConstants.DATA_DIR;


	public ChangeBackGroundActions() {
		super("Change Background");
		putValue(SHORT_DESCRIPTION, "Change the background of Battlefield");
		putValue(MNEMONIC_KEY, KeyEvent.VK_B);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		var choose = new JFileChooser(lastDir);
		choose.showOpenDialog(null);
		if (choose.getSelectedFile() != null) {

			lastDir = choose.getCurrentDirectory();

			BufferedImage im;
			try {
				im = ImageTools.read(choose.getSelectedFile());
				GamePanelGUI.getInstance().getPanelBattleField().setBackgroundPicture(im);
				GamePanelGUI.getInstance().getPanelBattleField().repaint();

				MTGControler.getInstance().setProperty("/game/player-profil/background",choose.getSelectedFile().getAbsolutePath());
			} catch (IOException e1) {
				logger.error(e1);
			}

		}

	}

}
