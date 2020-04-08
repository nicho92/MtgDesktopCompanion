package org.magic.tools;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

public class ShortcutManager {

	private static Logger logger = MTGLogger.getLogger(ShortcutManager.class);


	public static void setShortCutTo(int key, JButton b) {
		b.getActionMap().put(KeyStroke.getKeyStroke(key,InputEvent.CTRL_DOWN_MASK), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				b.doClick();
				
			}
		});
		logger.debug("Set " + key +" to " + b);
	}

}
