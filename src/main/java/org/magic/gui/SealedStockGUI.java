package org.magic.gui;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
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
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.components.charts.SealedHistoryPricesPanel;
import org.magic.gui.models.SealedStockTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;

import groovyjarjarantlr4.v4.codegen.model.ModelElement;
public class SealedStockGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private PackagesBrowserPanel packagePanel;
	
	private SealedStockTableModel model;
	private Packaging selectedItem;
	private AbstractBuzyIndicatorComponent buzy;
	public SealedStockGUI() {
		initGUI();
	}
	
	private void initGUI() {
		
		model = new SealedStockTableModel();
		var objectpanel = new ObjectViewerPanel();
		JXTable table = UITools.createNewTable(model);
		packagePanel = new PackagesBrowserPanel(false);
		GedPanel<SealedStock> gedPanel = new GedPanel<>();
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		
		
		var toolsPanel = new JPanel();
		var centerPanel = new JSplitPane();
		centerPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.setDividerLocation(0.5);
		centerPanel.setResizeWeight(0.5);
		var panneauDetail = new JTabbedPane();
		var historyPricePanel= new SealedHistoryPricesPanel();
		
		var buttonNew = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_N, "stock new");
		buttonNew.setEnabled(false);
		var buttonDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_N, "stock delete");
		buttonDelete.setEnabled(false);
		
		var buttonUpdate = UITools.createBindableJButton(null, MTGConstants.ICON_REFRESH, KeyEvent.VK_U, "stock refresh");
		var buttonSave= UITools.createBindableJButton(null, MTGConstants.ICON_SAVE, KeyEvent.VK_S, "stock save");
		
		
		setLayout(new BorderLayout());
		
		toolsPanel.add(buttonNew);
		toolsPanel.add(buttonDelete);
		toolsPanel.add(buttonUpdate);
		toolsPanel.add(buttonSave);
		toolsPanel.add(buzy);
		
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
				
				buttonDelete.setEnabled(ss!=null);
				
				if(ss!=null)
				{
					
					historyPricePanel.init(ss.getProduct(), ss.getProduct().getEdition()+"-"+ ss.getProduct().getType());
					packagePanel.load(ss.getProduct());
					objectpanel.show(ss);
				}
			}
		});
		
		buttonDelete.addActionListener(el->{
			List<SealedStock> list = UITools.getTableSelections(table, 0);
			
			int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().get("CONFIRM_DELETE", list.size()), MTGControler.getInstance().getLangService().get("DELETE"),JOptionPane.YES_NO_OPTION);
			
			if(res==JOptionPane.YES_OPTION)
			{
				AbstractObservableWorker<Void, SealedStock, MTGDao> sw = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGDao.class),1) {

					@Override
					protected Void doInBackground() throws Exception {
						for(SealedStock ss : list)	
							plug.deleteStock(ss);
						
						return null;
					}
					
					@Override
					protected void notifyEnd()
					{
						try {
							model.init(getEnabledPlugin(MTGDao.class).listSeleadStocks());
						} catch (SQLException e) {
							logger.error(e);
						}
					}

				};
				
				ThreadManager.getInstance().runInEdt(sw, "deletes" + list.size()+ "stocks Sealed");
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
				AbstractObservableWorker<SealedStock, SealedStock, MTGDao> sw = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGDao.class),1) {

					@Override
					protected SealedStock doInBackground() throws Exception {
						var s = new SealedStock(selectedItem,1);
						plug.saveOrUpdateStock(s);
						return s;
					}
					
					@Override
					protected void notifyEnd()
					{
						try {
							model.addItem(get());
						} catch (InterruptedException | ExecutionException e) {
							Thread.currentThread().interrupt();
						}
					}

				};
				
				ThreadManager.getInstance().runInEdt(sw, "Saving stocks Sealed");
		});
		
		
		buttonSave.addActionListener(el->{
				var list = model.getItems().stream().filter(SealedStock::isUpdated).collect(Collectors.toList());
				
				AbstractObservableWorker<Void, SealedStock, MTGDao> sw = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGDao.class),list.size()) {
					@Override
					protected Void doInBackground() throws Exception {
						for(SealedStock ss : list)
							plug.saveOrUpdateStock(ss);
						
						return null;
					}
				};
				
				ThreadManager.getInstance().runInEdt(sw, "Saving stocks Sealed");
				
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
