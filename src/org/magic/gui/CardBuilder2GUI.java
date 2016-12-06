package org.magic.gui;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.services.MTGDesktopCompanionControler;

public class CardBuilder2GUI extends JPanel{
	private JTable table;
	public CardBuilder2GUI() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);
		
		JPanel panelSets = new JPanel();
		tabbedPane.addTab("Set", null, panelSets, null);
		panelSets.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panelSets.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable(new MagicEditionsTableModel());
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		panelSets.add(panel, BorderLayout.NORTH);
		
		JButton btnAdd = new JButton("Add");
		panel.add(btnAdd);
		
		JButton btnRemove = new JButton("Remove");
		panel.add(btnRemove);
		
		MagicEditionDetailPanel magicEditionDetailPanel = new MagicEditionDetailPanel(false);
		magicEditionDetailPanel.setEditable(true);
		panelSets.add(magicEditionDetailPanel, BorderLayout.EAST);
		
		JPanel panelCards = new JPanel();
		tabbedPane.addTab("Cards", null, panelCards, null);
		panelCards.setLayout(new BorderLayout(0, 0));
		
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		MTGDesktopCompanionControler.getInstance().getEnabledProviders().init();
		MTGDesktopCompanionControler.getInstance().getEnabledDAO().init();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new CardBuilder2GUI());
		
		f.setVisible(true);
	}
	
}
