package org.magic.gui;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.PluginEntry;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGComboProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGGraders;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPictureCache;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.api.interfaces.MTGTrackingService;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ConfigurationPanel;
import org.magic.gui.components.HelpCompononent;
import org.magic.gui.models.conf.PluginTreeTableModel;
import org.magic.gui.renderer.MTGPluginTreeCellRenderer;
import org.magic.gui.renderer.standard.BooleanCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.PluginRegistry;
public class ConfigurationPanelGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTabbedPane subTabbedProviders ;
	private JLabel lblCopyright;
	private JLabel btnHelp;
	private JPanel bottomPanel;
	private HelpCompononent helpComponent;
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_CONFIG;
	}
	
	@Override
	public String getTitle() {
		return capitalize("CONFIGURATION");
	}
	
	
	
	
	public ConfigurationPanelGUI() {

		setLayout(new BorderLayout(0, 0));

		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		var providerConfigPanel = new JPanel();
		tabbedPane.addTab(capitalize("PROVIDERS"), MTGConstants.ICON_TAB_PLUGIN,providerConfigPanel, null);
		providerConfigPanel.setLayout(new BorderLayout(0, 0));

		subTabbedProviders = new JTabbedPane(SwingConstants.TOP);
		providerConfigPanel.add(subTabbedProviders);
		
		bottomPanel = new JPanel();
		lblCopyright = new JLabel("");
		btnHelp = new JLabel(MTGConstants.ICON_SMALL_HELP);
		helpComponent = new HelpCompononent();
		helpComponent.setPreferredSize(new Dimension(500, 0));
		helpComponent.setVisible(false);
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(lblCopyright,BorderLayout.WEST);
		bottomPanel.add(btnHelp,BorderLayout.EAST);
		providerConfigPanel.add(bottomPanel, BorderLayout.SOUTH);
		providerConfigPanel.add(helpComponent,BorderLayout.EAST);
		
		
		btnHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				helpComponent.setVisible(!helpComponent.isVisible());
			}
		});
		
		
		createTab(capitalize("CARDS"), MTGConstants.ICON_TAB_CARD, PluginRegistry.inst().getEntry(MTGCardsProvider.class));
		createTab(capitalize("PICTURES"), MTGConstants.ICON_TAB_PICTURE, PluginRegistry.inst().getEntry(MTGPictureProvider.class));
		createTab(capitalize("PRICERS"), MTGConstants.ICON_TAB_PRICES, PluginRegistry.inst().getEntry(MTGPricesProvider.class));
		createTab(capitalize("DATABASES"), MTGConstants.ICON_TAB_DAO, PluginRegistry.inst().getEntry(MTGDao.class));
		createTab(capitalize("SHOPPERS"), MTGConstants.ICON_TAB_SHOP, PluginRegistry.inst().getEntry(MTGShopper.class));
		createTab(capitalize("CARDS_IMPORT_EXPORT"), MTGConstants.ICON_TAB_IMPORT_EXPORT, PluginRegistry.inst().getEntry(MTGCardsExport.class));
		createTab(capitalize("DECKS_IMPORTER"), MTGConstants.ICON_TAB_DECK, PluginRegistry.inst().getEntry(MTGDeckSniffer.class));
		createTab(capitalize("DASHBOARD_MODULE"), MTGConstants.ICON_TAB_VARIATIONS,PluginRegistry.inst().getEntry(MTGDashBoard.class));
		createTab(capitalize("SERVERS"), MTGConstants.ICON_TAB_SERVER, PluginRegistry.inst().getEntry(MTGServer.class));
		createTab(capitalize("NOTIFICATION"), MTGConstants.ICON_TAB_NOTIFICATION, PluginRegistry.inst().getEntry(MTGNotifier.class));
		createTab(capitalize("CACHES"), MTGConstants.ICON_TAB_CACHE,PluginRegistry.inst().getEntry(MTGPictureCache.class));
		createTab(capitalize("RSS_MODULE"), MTGConstants.ICON_TAB_NEWS, PluginRegistry.inst().getEntry(MTGNewsProvider.class));
		createTab(capitalize("WALLPAPER"), MTGConstants.ICON_TAB_WALLPAPER,PluginRegistry.inst().getEntry(MTGWallpaperProvider.class));
		createTab(capitalize("BUILDER_MODULE"), MTGConstants.ICON_TAB_CONSTRUCT, PluginRegistry.inst().getEntry(MTGPictureEditor.class));
		createTab(capitalize("TOKENS"), MTGConstants.ICON_TAB_CARD, PluginRegistry.inst().getEntry(MTGTokensProvider.class));
		createTab(capitalize("INDEXER"), MTGConstants.ICON_TAB_SIMILARITY, PluginRegistry.inst().getEntry(MTGCardsIndexer.class));
		createTab(capitalize("SUGGESTION"), MTGConstants.ICON_TAB_SUGGESTION, PluginRegistry.inst().getEntry(MTGTextGenerator.class));
		createTab(capitalize("SCRIPT"), MTGConstants.ICON_TAB_RULES, PluginRegistry.inst().getEntry(MTGScript.class));
		createTab(capitalize("POOL"), MTGConstants.ICON_TAB_POOL, PluginRegistry.inst().getEntry(MTGPool.class));
		createTab(capitalize("COMBO"), MTGConstants.ICON_TAB_COMBO, PluginRegistry.inst().getEntry(MTGComboProvider.class));
		createTab(capitalize("GED"), MTGConstants.ICON_TAB_GED, PluginRegistry.inst().getEntry(MTGGedStorage.class));
		createTab(capitalize("GRADING"), MTGConstants.ICON_TAB_GRADING, PluginRegistry.inst().getEntry(MTGGraders.class));
		createTab(capitalize("RECOGNITION"), MTGConstants.ICON_TAB_RECOGNITION, PluginRegistry.inst().getEntry(MTGCardRecognition.class));
		createTab(capitalize("TRACKING"), MTGConstants.ICON_TAB_DELIVERY, PluginRegistry.inst().getEntry(MTGTrackingService.class));
		createTab(capitalize("EXTERNAL_SHOP"), MTGConstants.ICON_TAB_EXT_SHOP, PluginRegistry.inst().getEntry(MTGExternalShop.class));
		
		
		
		tabbedPane.addTab(capitalize("CONFIGURATION"), MTGConstants.ICON_TAB_ADMIN,new JScrollPane(new ConfigurationPanel()), null);
		tabbedPane.addTab(capitalize("ACTIVE_SERVERS"), MTGConstants.ICON_TAB_ACTIVESERVER, new ServersGUI(),null);

	}
	
	@SuppressWarnings("unchecked")
	private <T extends MTGPlugin> void createTab(String label, Icon ic, PluginEntry<T> pe)
	{
		
		if(pe.getPlugins().isEmpty())
			PluginRegistry.inst().listPlugins(pe.getParametrizedClass());
		
		var table = new JXTreeTable(new PluginTreeTableModel<>(pe.isMultiprovider(), pe.getPlugins()));
		table.setShowGrid(true, false);
		table.setTreeCellRenderer(new MTGPluginTreeCellRenderer());
		table.setDefaultRenderer(Boolean.class, new BooleanCellEditorRenderer());
		
		subTabbedProviders.addTab(label, ic,new JScrollPane(table), null);
		table.addTreeSelectionListener(e -> {
			
			if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getPathCount() > 1)
				((PluginTreeTableModel<?>) table.getTreeTableModel()).setSelectedNode((T) e.getNewLeadSelectionPath().getPathComponent(1));
			
			
			if(e.getNewLeadSelectionPath()!=null)
			{
				lblCopyright.setText(((T) e.getNewLeadSelectionPath().getPathComponent(1)).termsAndCondition());
				
				if(e.getNewLeadSelectionPath().getLastPathComponent() instanceof MTGPlugin)
					helpComponent.init(((T) e.getNewLeadSelectionPath().getPathComponent(1)));
			}
		});
		table.packAll();
	}
	

	
	

}
