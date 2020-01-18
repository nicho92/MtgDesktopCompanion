package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.PluginEntry;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGComboProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGGraders;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ConfigurationPanel;
import org.magic.gui.components.HelpCompononent;
import org.magic.gui.components.LoggerViewPanel;
import org.magic.gui.models.conf.PluginTreeTableModel;
import org.magic.gui.renderer.MTGPluginTreeCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
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
		return MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION");
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public ConfigurationPanelGUI() {

		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		JPanel providerConfigPanel = new JPanel();
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("PROVIDERS"), MTGConstants.ICON_TAB_PLUGIN,providerConfigPanel, null);
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
		
		
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CARDS"), MTGConstants.ICON_TAB_CARD, PluginRegistry.inst().getEntry(MTGCardsProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("PICTURES"), MTGConstants.ICON_TAB_PICTURE, PluginRegistry.inst().getEntry(MTGPictureProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("PRICERS"), MTGConstants.ICON_TAB_PRICES, PluginRegistry.inst().getEntry(MTGPricesProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DATABASES"), MTGConstants.ICON_TAB_DAO, PluginRegistry.inst().getEntry(MTGDao.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SHOPPERS"), MTGConstants.ICON_TAB_SHOP, PluginRegistry.inst().getEntry(MTGShopper.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CARDS_IMPORT_EXPORT"), MTGConstants.ICON_TAB_IMPORT_EXPORT, PluginRegistry.inst().getEntry(MTGCardsExport.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DECKS_IMPORTER"), MTGConstants.ICON_TAB_DECK, PluginRegistry.inst().getEntry(MTGDeckSniffer.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("DASHBOARD_MODULE"), MTGConstants.ICON_TAB_VARIATIONS,PluginRegistry.inst().getEntry(MTGDashBoard.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SERVERS"), MTGConstants.ICON_TAB_SERVER, PluginRegistry.inst().getEntry(MTGServer.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("NOTIFICATION"), MTGConstants.ICON_TAB_NOTIFICATION, PluginRegistry.inst().getEntry(MTGNotifier.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("CACHES"), MTGConstants.ICON_TAB_CACHE,PluginRegistry.inst().getEntry(MTGPicturesCache.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("RSS_MODULE"), MTGConstants.ICON_TAB_NEWS, PluginRegistry.inst().getEntry(MTGNewsProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("WALLPAPER"), MTGConstants.ICON_TAB_WALLPAPER,PluginRegistry.inst().getEntry(MTGWallpaperProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("BUILDER_MODULE"), MTGConstants.ICON_TAB_CONSTRUCT, PluginRegistry.inst().getEntry(MTGPictureEditor.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("INDEXER"), MTGConstants.ICON_TAB_SIMILARITY, PluginRegistry.inst().getEntry(MTGCardsIndexer.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SUGGESTION"), MTGConstants.ICON_TAB_SUGGESTION, PluginRegistry.inst().getEntry(MTGTextGenerator.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SCRIPT"), MTGConstants.ICON_TAB_RULES, PluginRegistry.inst().getEntry(MTGScript.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("POOL"), MTGConstants.ICON_TAB_POOL, PluginRegistry.inst().getEntry(MTGPool.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("COMBO"), MTGConstants.ICON_TAB_COMBO, PluginRegistry.inst().getEntry(MTGComboProvider.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("GED"), MTGConstants.ICON_TAB_GED, PluginRegistry.inst().getEntry(MTGGedStorage.class));
		createTab(MTGControler.getInstance().getLangService().getCapitalize("GRADING"), MTGConstants.ICON_TAB_GRADING, PluginRegistry.inst().getEntry(MTGGraders.class));
		
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION"), MTGConstants.ICON_TAB_ADMIN,new ConfigurationPanel(), null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("ACTIVE_SERVERS"), MTGConstants.ICON_TAB_ACTIVESERVER, new ServersGUI(),null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("LOGS"), MTGConstants.ICON_TAB_RULES, new LoggerViewPanel(),null);

	}
	
	@SuppressWarnings("unchecked")
	private <T extends MTGPlugin> void createTab(String label, Icon ic, PluginEntry<T> pe)
	{
		
		if(pe.getPlugins().isEmpty())
			PluginRegistry.inst().listPlugins(pe.getParametrizedClass());
		
		JXTreeTable table = new JXTreeTable(new PluginTreeTableModel<>(pe.isMultiprovider(), pe.getPlugins()));
		table.setShowGrid(true, false);
		table.setTreeCellRenderer(new MTGPluginTreeCellRenderer());
		table.setDefaultRenderer(Boolean.class, (JTable t, Object value, boolean isSelected,boolean hasFocus, int row, int column)->{
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
