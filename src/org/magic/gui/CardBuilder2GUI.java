package org.magic.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.sql.SQLException;

import org.magic.api.beans.MagicEdition;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.services.MTGDesktopCompanionControler;

public class CardBuilder2GUI extends JPanel{
	public CardBuilder2GUI() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);
		
		JPanel panelSets = new JPanel();
		tabbedPane.addTab("Set", null, panelSets, null);
		panelSets.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panelSets.add(panel, BorderLayout.EAST);
		
		MagicCardDetailPanel magicCardDetailPanel = new MagicCardDetailPanel();
		magicCardDetailPanel.setEditable(true);
		panelSets.add(magicCardDetailPanel, BorderLayout.CENTER);
		
		JPanel panelCards = new JPanel();
		tabbedPane.addTab("Cards", null, panelCards, null);
		
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		MTGDesktopCompanionControler.getInstance().getEnabledProviders().init();
		MTGDesktopCompanionControler.getInstance().getEnabledDAO().init();
		JFrame f = new JFrame();
		f.getContentPane().add(new CardBuilder2GUI());
		
		f.setVisible(true);
	}
	
}
