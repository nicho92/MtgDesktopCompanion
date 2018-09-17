package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.magic.gui.abstracts.MTGUIPanel;
import org.magic.gui.components.ConstructPanel;
import org.magic.gui.components.SealedPanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class DeckBuilderGUI extends MTGUIPanel {

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
		return MTGControler.getInstance().getLangService().getCapitalize("DECK_MODULE");
	}
	
	
	
	public DeckBuilderGUI() {
		setLayout(new BorderLayout());
		JTabbedPane tab = new JTabbedPane();
		add(tab, BorderLayout.CENTER);

		tab.addTab(MTGControler.getInstance().getLangService().getCapitalize("CONSTRUCT"),MTGConstants.ICON_TAB_CONSTRUCT, new ConstructPanel());
		tab.addTab(MTGControler.getInstance().getLangService().getCapitalize("SEALED"), MTGConstants.ICON_TAB_SEALED, new SealedPanel());

	}

}
