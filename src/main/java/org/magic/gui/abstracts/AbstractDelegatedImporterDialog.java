package org.magic.gui.abstracts;

import javax.swing.JDialog;

import org.magic.api.beans.MagicDeck;

public abstract class AbstractDelegatedImporterDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public abstract MagicDeck getSelectedDeck();



}
