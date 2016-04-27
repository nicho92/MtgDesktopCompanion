package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.interfaces.DashBoard;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.MagicShopper;
import org.magic.gui.models.DashBoardTableModel;
import org.magic.gui.models.MagicDAOProvidersTableModel;
import org.magic.gui.models.MagicPricesProvidersTableModel;
import org.magic.gui.models.MagicShoppersTableModel;
import org.magic.gui.models.ProvidersTableModel;
import org.magic.gui.models.SystemTableModel;

public class ConfigurationPanelGUI extends JPanel {
	private JTable table;
	private JTable cardsProviderTable;
	private JXTreeTable priceProviderTable;
	private JXTreeTable daoProviderTable;
	private JXTreeTable shopperTreeTable;
	private JXTreeTable dashboardTreeTable;
	
	public ConfigurationPanelGUI() {
		
		
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		JScrollPane applicationConfigPanel = new JScrollPane();
		tabbedPane.addTab("Application", null, applicationConfigPanel, null);

		SystemTableModel mod = new SystemTableModel();
		
		table = new JTable(mod);
		applicationConfigPanel.setViewportView(table);
		
		
		JPanel providerConfigPanel = new JPanel();
		tabbedPane.addTab("Providers", null, providerConfigPanel, null);
		providerConfigPanel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane subTabbedProviders = new JTabbedPane(JTabbedPane.TOP);
		providerConfigPanel.add(subTabbedProviders);
		
		JScrollPane cardsProvidersScrollPane = new JScrollPane();
		subTabbedProviders.addTab("Cards", null, cardsProvidersScrollPane, null);
		
		cardsProviderTable = new JTable();
		cardsProvidersScrollPane.setViewportView(cardsProviderTable);
		
		JScrollPane priceProviderScrollPane = new JScrollPane();
		subTabbedProviders.addTab("Pricers", null, priceProviderScrollPane, null);
		priceProviderTable = new JXTreeTable(new MagicPricesProvidersTableModel());
		cardsProviderTable.setModel(new ProvidersTableModel());
		
		priceProviderTable.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if(e.getNewLeadSelectionPath()!=null)
					if(e.getNewLeadSelectionPath().getPathCount()>1);
						((MagicPricesProvidersTableModel)priceProviderTable.getTreeTableModel()).setSelectedNode((MagicPricesProvider)e.getNewLeadSelectionPath().getPathComponent(1));
			}
		});
		priceProviderScrollPane.setViewportView(priceProviderTable);
		
		JScrollPane daoProviderScrollPane = new JScrollPane();
		subTabbedProviders.addTab("DataBases", null, daoProviderScrollPane, null);
		
		daoProviderTable = new JXTreeTable(new MagicDAOProvidersTableModel());
		daoProviderTable.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if(e.getNewLeadSelectionPath()!=null)
					if(e.getNewLeadSelectionPath().getPathCount()>1);
						((MagicDAOProvidersTableModel)daoProviderTable.getTreeTableModel()).setSelectedNode((MagicDAO)e.getNewLeadSelectionPath().getPathComponent(1));
			}
		});
		daoProviderScrollPane.setViewportView(daoProviderTable);
		
		JScrollPane shopperScrollPane = new JScrollPane();
		subTabbedProviders.addTab("Shoppers", null, shopperScrollPane, null);
		
		shopperTreeTable = new JXTreeTable(new MagicShoppersTableModel());
		shopperTreeTable.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if(e.getNewLeadSelectionPath()!=null)
					if(e.getNewLeadSelectionPath().getPathCount()>1);
						((MagicShoppersTableModel)shopperTreeTable.getTreeTableModel()).setSelectedNode((MagicShopper)e.getNewLeadSelectionPath().getPathComponent(1));
			}
		});
		shopperScrollPane.setViewportView(shopperTreeTable);
		
		JScrollPane dashboardScrollPane = new JScrollPane();
		subTabbedProviders.addTab("DashBoards", null, dashboardScrollPane, null);
		
		dashboardTreeTable = new JXTreeTable();
		dashboardScrollPane.setViewportView(dashboardTreeTable);
		
		shopperTreeTable.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if(e.getNewLeadSelectionPath()!=null)
					if(e.getNewLeadSelectionPath().getPathCount()>1);
						((DashBoardTableModel)dashboardTreeTable.getTreeTableModel()).setSelectedNode((DashBoard)e.getNewLeadSelectionPath().getPathComponent(1));
			}
		});
		
		
	}


}
