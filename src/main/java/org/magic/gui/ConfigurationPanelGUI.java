package org.magic.gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ConfigurationPanel;
import org.magic.gui.components.LoggerViewPanel;
import org.magic.gui.models.conf.PluginTreeTableModel;
import org.magic.gui.renderer.MTGPluginTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import javax.swing.JLabel;

public class ConfigurationPanelGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTabbedPane subTabbedProviders ;
	private JLabel lblCopyright;
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_CONFIG;
	}
	
	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION");
	}
	
	
	
	
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
		
		lblCopyright = new JLabel("New label");
		providerConfigPanel.add(lblCopyright, BorderLayout.SOUTH);

		
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CARDS"), MTGConstants.ICON_TAB_CARD, false,MTGControler.getInstance().getPlugins(MTGCardsProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("PICTURES"), MTGConstants.ICON_TAB_PICTURE, false,MTGControler.getInstance().getPlugins(MTGPictureProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("PRICERS"), MTGConstants.ICON_TAB_PRICES, true,MTGControler.getInstance().getPlugins(MTGPricesProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DATABASES"), MTGConstants.ICON_TAB_DAO, false, MTGControler.getInstance().getPlugins(MTGDao.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SHOPPERS"), MTGConstants.ICON_TAB_SHOP, true, MTGControler.getInstance().getPlugins(MTGShopper.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CARDS_IMPORT_EXPORT"), MTGConstants.ICON_TAB_IMPORT_EXPORT, true, MTGControler.getInstance().getPlugins(MTGCardsExport.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DECKS_IMPORTER"), MTGConstants.ICON_TAB_DECK, true, MTGControler.getInstance().getPlugins(MTGDeckSniffer.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DASHBOARD_MODULE"), MTGConstants.ICON_TAB_VARIATIONS, false, MTGControler.getInstance().getPlugins(MTGDashBoard.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SERVERS"), MTGConstants.ICON_TAB_SERVER, true, MTGControler.getInstance().getPlugins(MTGServer.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("NOTIFICATION"), MTGConstants.ICON_TAB_NOTIFICATION, true, MTGControler.getInstance().getPlugins(MTGNotifier.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CACHES"), MTGConstants.ICON_TAB_CACHE,false, MTGControler.getInstance().getPlugins(MTGPicturesCache.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"), MTGConstants.ICON_TAB_NEWS, true, MTGControler.getInstance().getPlugins(MTGNewsProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("WALLPAPER"), MTGConstants.ICON_TAB_WALLPAPER, true, MTGControler.getInstance().getPlugins(MTGWallpaperProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("BUILDER_MODULE"), MTGConstants.ICON_TAB_CONSTRUCT, false, MTGControler.getInstance().getPlugins(MTGPictureEditor.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("INDEXER"), MTGConstants.ICON_TAB_SIMILARITY, false, MTGControler.getInstance().getPlugins(MTGCardsIndexer.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SUGGESTION"), MTGConstants.ICON_TAB_SUGGESTION, false, MTGControler.getInstance().getPlugins(MTGTextGenerator.class));

		
		
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION"), MTGConstants.ICON_TAB_ADMIN,new ConfigurationPanel(), null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("ACTIVE_SERVERS"), MTGConstants.ICON_TAB_ACTIVESERVER, new ServersGUI(),null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("LOGS"), MTGConstants.ICON_TAB_RULES, new LoggerViewPanel(),null);

	}
	

	private <T extends MTGPlugin> void createTab(String label, Icon ic, boolean multi,List<T> list)
	{
		JXTreeTable table = new JXTreeTable(new PluginTreeTableModel<T>(multi, list));
		table.setShowGrid(true, false);
		table.setTreeCellRenderer(new MTGPluginTreeCellRenderer());
		table.setDefaultRenderer(Boolean.class, (JTable table2, Object value, boolean isSelected,boolean hasFocus, int row, int column)->{
				JPanel p = new JPanel();
				JCheckBox cbox = new JCheckBox("",Boolean.parseBoolean(value.toString()));
				cbox.setOpaque(false);
				p.add(cbox);
				
				if(isSelected)
					p.setBackground(table.getSelectionBackground());
				else
					p.setBackground(table.getBackground());
				
				return p;
		});
		
		
		subTabbedProviders.addTab(label, ic,new JScrollPane(table), null);
		table.addTreeSelectionListener(e -> {
			if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getPathCount() > 1)
				((PluginTreeTableModel<?>) table.getTreeTableModel()).setSelectedNode((T) e.getNewLeadSelectionPath().getPathComponent(1));
			
			
			lblCopyright.setText(((T) e.getNewLeadSelectionPath().getPathComponent(1)).termsAndCondition());
			
		});
		table.packAll();

	}
	
	

}
