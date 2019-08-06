package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
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
import org.magic.tools.UITools;

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
	
	
	
	
	public ConfigurationPanelGUI() {

		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		JPanel providerConfigPanel = new JPanel();
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("PROVIDERS"), MTGConstants.ICON_TAB_PLUGIN,providerConfigPanel, null);
		providerConfigPanel.setLayout(new BorderLayout(0, 0));

		subTabbedProviders = new JTabbedPane(JTabbedPane.TOP);
		providerConfigPanel.add(subTabbedProviders);
		
		bottomPanel = new JPanel();
		lblCopyright = new JLabel("");
		btnHelp = new JLabel(MTGConstants.ICON_SMALL_HELP);
		helpComponent = new HelpCompononent();
		helpComponent.setPreferredSize(new Dimension(400, 0));
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
		createTab(MTGControler.getInstance().getLangService().getCapitalize("SCRIPT"), MTGConstants.ICON_TAB_RULES, true, MTGControler.getInstance().getPlugins(MTGScript.class));

		
		
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("CONFIGURATION"), MTGConstants.ICON_TAB_ADMIN,new ConfigurationPanel(), null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("ACTIVE_SERVERS"), MTGConstants.ICON_TAB_ACTIVESERVER, new ServersGUI(),null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("LOGS"), MTGConstants.ICON_TAB_RULES, new LoggerViewPanel(),null);

	}
	

	private <T extends MTGPlugin> void createTab(String label, Icon ic, boolean multi,List<T> list)
	{
		JXTreeTable table = new JXTreeTable(new PluginTreeTableModel<>(multi, list));
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
		
		table.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2)
				{
					try {
						
						MTGPlugin plug = UITools.getTableSelection(table, 0);
						Desktop.getDesktop().browse(plug.getDocumentation().toURI());
					} catch (ClassCastException e1) {
						//do nothing not a plugin
					} catch (IOException|URISyntaxException e2) {
						logger.error(e2);
					} 
				} 
			}
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
