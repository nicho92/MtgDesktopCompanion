package org.magic.gui;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Packaging;
import org.magic.api.beans.SealedStock;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.components.charts.SealedHistoryPricesPanel;
import org.magic.gui.models.SealedStockTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;
public class SealedStockGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private PackagesBrowserPanel packagePanel;
	
	private SealedStockTableModel model;
	private Packaging selectedItem;
	private ObjectViewerPanel objectpanel;
	
	public SealedStockGUI() {
		initGUI();
	}
	
	private void initGUI() {
		model = new SealedStockTableModel();
		objectpanel = new ObjectViewerPanel();
		JXTable table = UITools.createNewTable(model);
		packagePanel = new PackagesBrowserPanel(false);
		GedPanel<SealedStock> gedPanel = new GedPanel<>();
		
		JPanel toolsPanel = new JPanel();
		JSplitPane centerPanel = new JSplitPane();
		centerPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.setDividerLocation(0.5);
		centerPanel.setResizeWeight(0.5);
		JTabbedPane panneauDetail = new JTabbedPane();
		SealedHistoryPricesPanel historyPricePanel= new SealedHistoryPricesPanel();
		
		JButton buttonNew = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_N, "stock new");
		buttonNew.setEnabled(false);
		JButton buttonDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_N, "stock delete");
		JButton buttonUpdate = UITools.createBindableJButton(null, MTGConstants.ICON_REFRESH, KeyEvent.VK_N, "stock refresh");
		
		setLayout(new BorderLayout());
		
		JLabel lblNewLabel = new JLabel("WARNING THIS MODULE IS NOT YET STABLE");
		lblNewLabel.setForeground(Color.RED);
		toolsPanel.add(lblNewLabel);
		
		toolsPanel.add(buttonNew);
		toolsPanel.add(buttonDelete);
		toolsPanel.add(buttonUpdate);
		
		panneauDetail.addTab(capitalize("INFO"),MTGConstants.ICON_TAB_PICTURE,packagePanel.getThumbnailPanel());
		UITools.addTab(panneauDetail, historyPricePanel);
		UITools.addTab(panneauDetail, gedPanel);
		
		if (MTGControler.getInstance().get("debug-json-panel").equalsIgnoreCase("true"))
			UITools.addTab(panneauDetail, objectpanel);
		
		
		
		add(packagePanel,BorderLayout.WEST);
		centerPanel.setLeftComponent(new JScrollPane(table));
		centerPanel.setRightComponent(panneauDetail);
		add(centerPanel,BorderLayout.CENTER);
		add(toolsPanel,BorderLayout.NORTH);
		
		model.setWritable(true);
	
		
		packagePanel.getTree().addTreeSelectionListener(e-> {
			
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)packagePanel.getTree().getLastSelectedPathComponent();
			
			if(selectedNode==null || selectedNode.getUserObject()==null)
				return;
			
			boolean isPackage = selectedNode.getUserObject() instanceof Packaging;
			buttonNew.setEnabled(isPackage);
			
			if(selectedNode!=null && isPackage)
			{
				
				selectedItem = (Packaging)selectedNode.getUserObject();
				historyPricePanel.init(selectedItem, selectedItem.getEdition()+"-"+selectedItem.getType());
				objectpanel.show(selectedItem);
			}
		});
		
		table.getSelectionModel().addListSelectionListener(l->{
			
			if(!l.getValueIsAdjusting())
			{
				SealedStock ss = UITools.getTableSelection(table, 0);
				if(ss!=null)
				{
					historyPricePanel.init(ss.getProduct(), ss.getProduct().getEdition()+"-"+ ss.getProduct().getType());
					packagePanel.load(ss.getProduct());
					objectpanel.show(selectedItem);
				}
			}
		});
		
		buttonDelete.addActionListener(el->{
			SealedStock it = UITools.getTableSelection(table, 0);
			
			int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().get("CONFIRM_DELETE", it.getProduct()), MTGControler.getInstance().getLangService().get("DELETE"),JOptionPane.YES_NO_OPTION);
			
			if(res==JOptionPane.YES_OPTION)
			{
				try {
					getEnabledPlugin(MTGDao.class).deleteStock(it);
					model.removeItem(it);
				} catch (SQLException e1) {
					MTGControler.getInstance().notify(e1);
				}
			}
			
			
		});
		
		buttonUpdate.addActionListener(el->{
			try {
				model.init(getEnabledPlugin(MTGDao.class).listSeleadStocks());
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}			
			
		});
		
		
		buttonNew.addActionListener(el->{
			try {
				
				SealedStock s = new SealedStock(selectedItem,1);
				getEnabledPlugin(MTGDao.class).saveOrUpdateStock(s);
				model.addItem(s);
			} catch (SQLException e) {
				MTGControler.getInstance().notify(e);
			}
		});
	}

	@Override
	public String getTitle() {
		return capitalize("PACKAGES");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_PACKAGE;
	}
	
	@Override
	public void onFirstShowing() {
		packagePanel.initTree();
		
		try {
			model.init(getEnabledPlugin(MTGDao.class).listSeleadStocks());
		} catch (SQLException e) {
			MTGControler.getInstance().notify(e);
		}
	}
}
