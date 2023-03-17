package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ConstructPanel;
import org.magic.gui.components.deck.SealedDeckBuildPanel;
import org.magic.services.MTGConstants;
public class DeckBuilderGUI extends MTGUIComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_DECK;
	}

	@Override
	public String getTitle() {
		return capitalize("DECK_MODULE");
	}



	public DeckBuilderGUI() {
		setLayout(new BorderLayout());
		var tab = new JTabbedPane();
		add(tab, BorderLayout.CENTER);

		tab.addTab(capitalize("CONSTRUCT"),MTGConstants.ICON_TAB_CONSTRUCT, new ConstructPanel());
		tab.addTab(capitalize("SEALED"), MTGConstants.ICON_TAB_SEALED, new SealedDeckBuildPanel());

	}

}
