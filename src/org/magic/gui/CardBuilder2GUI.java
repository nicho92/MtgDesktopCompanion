package org.magic.gui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import org.magic.api.beans.MagicEdition;

public class CardBuilder2GUI extends JPanel{
	public CardBuilder2GUI() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);
		
		JPanel panelSets = new JPanel();
		tabbedPane.addTab("Set", null, panelSets, null);
		
		JPanel panelCards = new JPanel();
		tabbedPane.addTab("Cards", null, panelCards, null);
	}

}
