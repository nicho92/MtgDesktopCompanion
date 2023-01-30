package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.SealedStock;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.components.charts.SealedHistoryPricesPanel;
import org.magic.gui.components.shops.StockItemsSynchronizationPanel;
import org.magic.gui.models.SealedStockTableModel;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class SealedStockGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private PackagesBrowserPanel packagePanel;

	private SealedStockTableModel model;
	private MTGSealedProduct selectedItem;
	private SealedStock selectedStock;
	private RSyntaxTextArea textEditor;
	private JXTable table;
	private AbstractBuzyIndicatorComponent buzy;

	public SealedStockGUI() {
		initGUI();
	}

	private void initGUI() {

		model = new SealedStockTableModel();
		var objectpanel = new ObjectViewerPanel();
		table = UITools.createNewTable(model);
		UITools.setDefaultRenderer(table, new StockTableRenderer());
		UITools.initTableFilter(table);
		packagePanel = new PackagesBrowserPanel(false);
		GedPanel<SealedStock> gedPanel = new GedPanel<>();
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		textEditor = new RSyntaxTextArea();
		textEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
		textEditor.setWrapStyleWord(true);
		textEditor.setLineWrap(true);
		textEditor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(selectedStock!=null)
				{
					selectedStock.setComment(textEditor.getText());
					selectedStock.setUpdated(true);
				}
			}
		});
		var synchroPanel = new StockItemsSynchronizationPanel();
		var toolsPanel = new JPanel();
		var centerPanel = new JSplitPane();
		centerPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.setDividerLocation(0.5);
		centerPanel.setResizeWeight(0.5);
		var panneauDetail = new JTabbedPane();
		var historyPricePanel= new SealedHistoryPricesPanel();
		var buttonNew = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_N, "stock new");
		var buttonDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_N, "stock delete");
		buttonDelete.setEnabled(false);
		buttonNew.setEnabled(false);

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
		UITools.addTab(panneauDetail, MTGUIComponent.build(new JScrollPane(textEditor), "description", MTGConstants.ICON_MANUAL));
		UITools.addTab(panneauDetail,synchroPanel);


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

			boolean isPackage = selectedNode.getUserObject() instanceof MTGSealedProduct;
			buttonNew.setEnabled(isPackage);

			if(selectedNode!=null && isPackage)
			{

				selectedItem = (MTGSealedProduct)selectedNode.getUserObject();
				historyPricePanel.init(selectedItem, selectedItem.getEdition()+"-"+selectedItem.getTypeProduct());
				objectpanel.init(selectedItem);
			}
		});

		table.getSelectionModel().addListSelectionListener(l->{

			if(!l.getValueIsAdjusting())
			{
				selectedStock = UITools.getTableSelection(table, 0);

				buttonDelete.setEnabled(selectedStock!=null);

				if(selectedStock!=null)
				{

					historyPricePanel.init(selectedStock.getProduct(), selectedStock.getProduct().getEdition()+"-"+ selectedStock.getProduct().getTypeProduct());
					packagePanel.load(selectedStock.getProduct());
					synchroPanel.init(selectedStock);
					objectpanel.init(selectedStock);
					gedPanel.init(SealedStock.class, selectedStock);
					textEditor.setText(selectedStock.getComment());
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
							model.init(getEnabledPlugin(MTGDao.class).listSealedStocks());
						} catch (SQLException e) {
							logger.error(e);
						}
					}

				};

				ThreadManager.getInstance().runInEdt(sw, "delete" + list.size()+ "stocks Sealed");
			}


		});

		buttonUpdate.addActionListener(el->{
			try {
				model.init(getEnabledPlugin(MTGDao.class).listSealedStocks());
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}

		});


		buttonNew.addActionListener(el->{
				AbstractObservableWorker<SealedStock, SealedStock, MTGDao> sw = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGDao.class),1) {

					@Override
					protected SealedStock doInBackground() throws Exception {
						var s = new SealedStock(selectedItem);
						s.setQte(1);
						plug.saveOrUpdateSealedStock(s);
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
				var list = model.getItems().stream().filter(SealedStock::isUpdated).toList();

				AbstractObservableWorker<Void, SealedStock, MTGDao> sw = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGDao.class),list.size()) {
					@Override
					protected Void doInBackground() throws Exception {
						for(SealedStock ss : list)
							plug.saveOrUpdateSealedStock(ss);

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
			AbstractObservableWorker<List<SealedStock>, SealedStock, MTGDao> sw2 = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGDao.class)) {

			@Override
			protected List<SealedStock> doInBackground() throws Exception {
				return plug.listSealedStocks();
			}

			@Override
			protected void done() {
				super.done();
				try {
					model.init(get());
				} catch (InterruptedException| ExecutionException e) {
					Thread.currentThread().interrupt();
				}
				table.packAll();

			}
		};



		SwingWorker<Void, Void> sw  = new SwingWorker<>(){
			@Override
			protected Void doInBackground() throws Exception {
				packagePanel.initTree();
				return null;
			}
			@Override
			protected void done() {
				packagePanel.reload();
				ThreadManager.getInstance().runInEdt(sw2, "Loading sealedstock");
			}
		};




		ThreadManager.getInstance().runInEdt(sw, "Loading sealed tree");


	}
}
