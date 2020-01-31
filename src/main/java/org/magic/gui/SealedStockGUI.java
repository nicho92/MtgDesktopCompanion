package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.components.charts.SealedHistoryPricesPanel;
import org.magic.gui.models.SealedStockModel;
import org.magic.gui.renderer.IntegerCellEditor;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class SealedStockGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private PackagesBrowserPanel packagePanel;
	private SealedStockModel model;
	private Packaging selectedItem;

	
	public SealedStockGUI() {
		initGUI();
	}
	
	public static void main(String[] args) throws SQLException 
	{
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		SealedStockGUI s = new SealedStockGUI();
		MTGUIComponent.createJDialog(s, true, false).setVisible(true);
		s.onFirstShowing();
	}
	
	private void initGUI() {
		model = new SealedStockModel();
		JXTable table = new JXTable(model);
		packagePanel = new PackagesBrowserPanel(false);
		JPanel toolsPanel = new JPanel();
		JSplitPane centerPanel = new JSplitPane();
		centerPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.setDividerLocation(0.5);
		centerPanel.setResizeWeight(0.5);
		JTabbedPane panneauDetail = new JTabbedPane();
		SealedHistoryPricesPanel historyPricePanel= new SealedHistoryPricesPanel();
		
		JButton buttonNew = new JButton(MTGConstants.ICON_NEW);
		buttonNew.setEnabled(false);
		JButton buttonDelete = new JButton(MTGConstants.ICON_DELETE);
		JButton buttonUpdate = new JButton(MTGConstants.ICON_REFRESH);
		
		setLayout(new BorderLayout());
		
		JLabel lblNewLabel = new JLabel("WARNING THIS MODULE IS NOT YET STABLE");
		lblNewLabel.setForeground(Color.RED);
		toolsPanel.add(lblNewLabel);
		
		toolsPanel.add(buttonNew);
		toolsPanel.add(buttonDelete);
		toolsPanel.add(buttonUpdate);
		
		panneauDetail.addTab(historyPricePanel.getTitle(),historyPricePanel.getIcon(),historyPricePanel);
		
		add(packagePanel,BorderLayout.WEST);
		centerPanel.setLeftComponent(new JScrollPane(table));
		centerPanel.setRightComponent(panneauDetail);
		add(centerPanel,BorderLayout.CENTER);
		add(toolsPanel,BorderLayout.NORTH);
		
		
		model.setWritable(true);
		table.getColumnModel().getColumn(4).setCellEditor(new IntegerCellEditor());
		
		
		
		
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
			}
		});
		
		table.getSelectionModel().addListSelectionListener(l->{
			SealedStock ss = UITools.getTableSelection(table, 0);
			historyPricePanel.init(ss.getProduct(), ss.getProduct().getEdition()+"-"+ ss.getProduct().getType());
		});
		
		buttonDelete.addActionListener(el->{
			SealedStock it = UITools.getTableSelection(table, 0);
			
			int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().get("CONFIRM_DELETE", it.getProduct()), MTGControler.getInstance().getLangService().get("DELETE"),JOptionPane.YES_NO_OPTION);
			
			if(res==JOptionPane.YES_OPTION)
			{
				try {
					MTGControler.getInstance().getEnabled(MTGDao.class).deleteStock(it);
					model.removeItem(it);
				} catch (SQLException e1) {
					MTGControler.getInstance().notify(e1);
				}
			}
			
			
		});
		
		buttonUpdate.addActionListener(el->{
			try {
				model.init(MTGControler.getInstance().getEnabled(MTGDao.class).listSeleadStocks());
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}			
			
		});
		
		
		buttonNew.addActionListener(el->{
			try {
				
				SealedStock s = new SealedStock(selectedItem,1);
				MTGControler.getInstance().getEnabled(MTGDao.class).saveOrUpdateStock(s);
				model.addItem(s);
			} catch (SQLException e) {
				MTGControler.getInstance().notify(e);
			}
		});
	}

	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("PACKAGES");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_PACKAGE;
	}
	
	@Override
	public void onFirstShowing() {
		packagePanel.initTree();
		
		try {
			model.init(MTGControler.getInstance().getEnabled(MTGDao.class).listSeleadStocks());
		} catch (SQLException e) {
			MTGControler.getInstance().notify(e);
		}
	}
}
