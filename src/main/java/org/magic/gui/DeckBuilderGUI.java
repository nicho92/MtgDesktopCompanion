package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.magic.gui.components.ConstructPanel;
import org.magic.gui.components.SealedPanel;
import org.magic.services.MTGControler;

public class DeckBuilderGUI extends JPanel {

	public DeckBuilderGUI() {
		setLayout(new BorderLayout());
		JTabbedPane tab = new JTabbedPane();
		add(tab, BorderLayout.CENTER);

		tab.add(MTGControler.getInstance().getLangService().getCapitalize("CONSTRUCT"), new ConstructPanel());
		tab.add(MTGControler.getInstance().getLangService().getCapitalize("SEALED"), new SealedPanel());

	}

}
