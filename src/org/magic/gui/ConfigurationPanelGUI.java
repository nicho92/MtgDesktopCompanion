package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.db.MagicDAO;
import org.magic.gui.models.SystemTableModel;

public class ConfigurationPanelGUI extends JPanel {
	
	JLabel lblLocation;
	JLabel lblDbSize;
	private MagicCardsProvider provider;
	private MagicDAO dao;
	private JLabel lblNbCards ;
	private JTable table;
		
	
	public ConfigurationPanelGUI(MagicCardsProvider provider,MagicDAO dao) {
		
		this.dao=dao;
		this.provider=provider;
		
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		JScrollPane applicationConfigPanel = new JScrollPane();
		tabbedPane.addTab("Application", null, applicationConfigPanel, null);

		SystemTableModel mod = new SystemTableModel();
		
		table = new JTable(mod);
		applicationConfigPanel.setViewportView(table);
		
		JPanel webSiteConfigPanel = new JPanel();
		tabbedPane.addTab("WebSite", null, webSiteConfigPanel, null);
		
		JPanel databaseConfigPanel = new JPanel();
		tabbedPane.addTab("Database", null, databaseConfigPanel, null);
		GridBagLayout gbl_databaseConfigPanel = new GridBagLayout();
		gbl_databaseConfigPanel.columnWidths = new int[]{142, 116, 0};
		gbl_databaseConfigPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_databaseConfigPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_databaseConfigPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		databaseConfigPanel.setLayout(gbl_databaseConfigPanel);
		
		JLabel lblDatabaseLocation = new JLabel("Database Location :");
		GridBagConstraints gbc_lblDatabaseLocation = new GridBagConstraints();
		gbc_lblDatabaseLocation.anchor = GridBagConstraints.WEST;
		gbc_lblDatabaseLocation.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabaseLocation.gridx = 0;
		gbc_lblDatabaseLocation.gridy = 1;
		databaseConfigPanel.add(lblDatabaseLocation, gbc_lblDatabaseLocation);
		
		lblLocation = new JLabel("");
		GridBagConstraints gbc_lblLocation = new GridBagConstraints();
		gbc_lblLocation.insets = new Insets(0, 0, 5, 0);
		gbc_lblLocation.gridx = 1;
		gbc_lblLocation.gridy = 1;
		databaseConfigPanel.add(lblLocation, gbc_lblLocation);
		
		lblDbSize = new JLabel("");
		GridBagConstraints gbc_lblDbSize = new GridBagConstraints();
		gbc_lblDbSize.insets = new Insets(0, 0, 5, 0);
		gbc_lblDbSize.gridx = 1;
		gbc_lblDbSize.gridy = 2;
		databaseConfigPanel.add(lblDbSize, gbc_lblDbSize);
		
		JLabel lblDatabaseSize = new JLabel("Database size :");
		GridBagConstraints gbc_lblDatabaseSize = new GridBagConstraints();
		gbc_lblDatabaseSize.anchor = GridBagConstraints.WEST;
		gbc_lblDatabaseSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabaseSize.gridx = 0;
		gbc_lblDatabaseSize.gridy = 2;
		databaseConfigPanel.add(lblDatabaseSize, gbc_lblDatabaseSize);
		
		JLabel lblCardsInDb = new JLabel("Cards in DB :");
		GridBagConstraints gbc_lblCardsInDb = new GridBagConstraints();
		gbc_lblCardsInDb.anchor = GridBagConstraints.WEST;
		gbc_lblCardsInDb.insets = new Insets(0, 0, 0, 5);
		gbc_lblCardsInDb.gridx = 0;
		gbc_lblCardsInDb.gridy = 3;
		databaseConfigPanel.add(lblCardsInDb, gbc_lblCardsInDb);
		
		lblNbCards = new JLabel("");
		GridBagConstraints gbc_lblNbCards = new GridBagConstraints();
		gbc_lblNbCards.gridx = 1;
		gbc_lblNbCards.gridy = 3;
		databaseConfigPanel.add(lblNbCards, gbc_lblNbCards);
		
		JPanel providerConfigPanel = new JPanel();
		tabbedPane.addTab("Providers", null, providerConfigPanel, null);
		
		initDBTab();
		
	}

	private void initDBTab() {
		lblLocation.setText(dao.getDBLocation());
		lblDbSize.setText(dao.getDBSize()/1024 +" Ko");
		try {
			lblNbCards.setText(dao.getCardsCount(null) +"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
