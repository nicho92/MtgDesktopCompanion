package org.magic.gui.components;

import javax.swing.JLabel;

import org.magic.tools.MTG;

public class JLangLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	
	
	public JLangLabel(String key) {
		setText(MTG.capitalize(key));
	}
	
}
