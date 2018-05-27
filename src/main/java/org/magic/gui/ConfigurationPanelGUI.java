package org.magic.gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.components.ConfigurationPanel;
import org.magic.gui.components.LoggerViewPanel;
import org.magic.gui.models.conf.ProviderTreeTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class ConfigurationPanelGUI extends JPanel {

	private JTabbedPane subTabbedProviders ;
	
	public ConfigurationPanelGUI() {

		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		JPanel providerConfigPanel = new JPanel();
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("PROVIDERS"), MTGConstants.ICON_TAB_PLUGIN,
				providerConfigPanel, null);
		providerConfigPanel.setLayout(new BorderLayout(0, 0));

		subTabbedProviders = new JTabbedPane(JTabbedPane.TOP);
		providerConfigPanel.add(subTabbedProviders);

		
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CARDS"), MTGConstants.ICON_BACK, false,MTGControler.getInstance().getCardsProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("PICTURES"), MTGConstants.ICON_TAB_PICTURE, false,MTGControler.getInstance().getPicturesProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("PRICERS"), MTGConstants.ICON_TAB_PRICES, true,MTGControler.getInstance().getPricerProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DATABASES"), MTGConstants.ICON_TAB_DAO, false, MTGControler.getInstance().getDaoProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SHOPPERS"), MTGConstants.ICON_TAB_SHOP, true, MTGControler.getInstance().getShoppersProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CARDS_IMPORT_EXPORT"), MTGConstants.ICON_TAB_IMPORT_EXPORT, true, MTGControler.getInstance().getImportExportProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DECKS_IMPORTER"), MTGConstants.ICON_TAB_DECK, true, MTGControler.getInstance().getDeckSnifferProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DASHBOARD_MODULE"), MTGConstants.ICON_TAB_VARIATIONS, false, MTGControler.getInstance().getDashboardsProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SERVERS"), MTGConstants.ICON_TAB_SERVER, true, MTGControler.getInstance().getServers());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("NOTIFICATION"), MTGConstants.ICON_TAB_NOTIFICATION, true, MTGControler.getInstance().getNotifierProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CACHES"), MTGConstants.ICON_TAB_CACHE,false, MTGControler.getInstance().getCachesProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"), MTGConstants.ICON_TAB_NEWS, true, MTGControler.getInstance().getNewsProviders());
		createTab(MTGControler.getInstance().getLangService().getCapitalize("WALLPAPER"), MTGConstants.ICON_TAB_WALLPAPER, true, MTGControler.getInstance().getWallpaperProviders());

		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION"), MTGConstants.ICON_TAB_ADMIN,new ConfigurationPanel(), null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("ACTIVE_SERVERS"), MTGConstants.ICON_TAB_ACTIVESERVER, new ServersGUI(),null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("LOGS"), MTGConstants.ICON_TAB_RULES, new LoggerViewPanel(),null);

	}
	

	private <T extends MTGPlugin> void createTab(String label, Icon ic, boolean multi,List<T> list)
	{
		JScrollPane scroll = new JScrollPane();
		subTabbedProviders.addTab(label, ic,scroll, null);
		JXTreeTable table = new JXTreeTable(new ProviderTreeTableModel<T>(multi, list));
		scroll.setViewportView(table);
		table.addTreeSelectionListener(e -> {
			if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getPathCount() > 1)
				((ProviderTreeTableModel<?>) table.getTreeTableModel()).setSelectedNode((T) e.getNewLeadSelectionPath().getPathComponent(1));
		});

	}
	
	

}
