package org.magic.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;

public class ConfigurationPanelGUI extends JPanel {
	public ConfigurationPanelGUI() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		JPanel applicationConfigPanel = new JPanel();
		tabbedPane.addTab("Application", null, applicationConfigPanel, null);
		
		JPanel databaseConfigPanel = new JPanel();
		tabbedPane.addTab("Database", null, databaseConfigPanel, null);
		
		JPanel webSiteConfigPanel = new JPanel();
		tabbedPane.addTab("WebSite", null, webSiteConfigPanel, null);
		
		JPanel providerConfigPanel = new JPanel();
		tabbedPane.addTab("Providers", null, providerConfigPanel, null);
	}

}
